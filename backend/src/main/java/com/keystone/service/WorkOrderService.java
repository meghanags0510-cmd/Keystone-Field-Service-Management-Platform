package com.keystone.service;

import com.keystone.domain.*;
import com.keystone.dto.*;
import com.keystone.exception.ApiException;
import com.keystone.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkOrderService {
    private final WorkOrderRepository orders;
    private final CustomerRepository customers;
    private final SiteRepository sites;
    private final UserRepository users;
    private final StatusHistoryRepository history;
    private final PartRepository parts;
    private final PartUsageRepository usages;
    private final TimeLogRepository timeLogs;

    public WorkOrderService(WorkOrderRepository orders, CustomerRepository customers, SiteRepository sites,
                            UserRepository users, StatusHistoryRepository history, PartRepository parts,
                            PartUsageRepository usages, TimeLogRepository timeLogs) {
        this.orders=orders; this.customers=customers; this.sites=sites; this.users=users;
        this.history=history; this.parts=parts; this.usages=usages; this.timeLogs=timeLogs;
    }

    public List<WorkOrder> list(Authentication auth) {
        User u = currentUser(auth);
        return switch (u.getRole()) {
            case TECHNICIAN -> orders.findByAssigneeIdOrderByCreatedAtDesc(u.getId());
            case CUSTOMER -> u.getCustomer() == null ? List.of() : orders.findByCustomerIdOrderByCreatedAtDesc(u.getCustomer().getId());
            default -> orders.findAllByOrderByCreatedAtDesc();
        };
    }

    public WorkOrder get(Long id, Authentication auth) {
        WorkOrder o = orders.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Work order not found"));
        ensureVisible(o, currentUser(auth));
        return o;
    }

    @Transactional
    public WorkOrder create(WorkOrderRequest r) {
        Customer c = customers.findById(r.customerId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));
        Site s = sites.findById(r.siteId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Site not found"));
        if (!s.getCustomer().getId().equals(c.getId()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Site does not belong to customer");

        WorkOrder o = new WorkOrder();
        o.setCode("WO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        o.setTitle(r.title().trim());
        o.setDescription(r.description());
        o.setPriority(r.priority());
        o.setStatus(WorkOrderStatus.NEW);
        o.setCustomer(c);
        o.setSite(s);
        o.setSlaDueAt(r.slaDueAt() != null ? r.slaDueAt() : defaultSla(r.priority()));
        return orders.save(o);
    }

    public WorkOrder update(Long id, WorkOrderRequest r, Authentication auth) {
        WorkOrder o = get(id, auth);
        if (o.getStatus() == WorkOrderStatus.CLOSED || o.getStatus() == WorkOrderStatus.CANCELLED)
            throw new ApiException(HttpStatus.CONFLICT, "Terminal work orders are immutable");
        Customer c = customers.findById(r.customerId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));
        Site s = sites.findById(r.siteId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Site not found"));
        if (!s.getCustomer().getId().equals(c.getId()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Site does not belong to customer");
        o.setTitle(r.title().trim()); o.setDescription(r.description()); o.setPriority(r.priority());
        o.setCustomer(c); o.setSite(s);
        if (r.slaDueAt() != null) o.setSlaDueAt(r.slaDueAt());
        o.touch();
        return orders.save(o);
    }

    @Transactional
    public WorkOrder assign(Long id, AssignRequest r, Authentication auth) {
        WorkOrder o = get(id, auth);
        if (o.getStatus() == WorkOrderStatus.CLOSED || o.getStatus() == WorkOrderStatus.CANCELLED)
            throw new ApiException(HttpStatus.CONFLICT, "Cannot assign a terminal work order");
        User technician = users.findById(r.technicianId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Technician not found"));
        if (technician.getRole() != Role.TECHNICIAN)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Assignee must be a technician");
        o.setAssignee(technician);
        transitionInternal(o, WorkOrderStatus.ASSIGNED, currentUser(auth), "Assigned to technician");
        return orders.save(o);
    }

    @Transactional
    public WorkOrder status(Long id, StatusRequest r, Authentication auth) {
        WorkOrder o = get(id, auth);
        User actor = currentUser(auth);
        if (actor.getRole() == Role.TECHNICIAN && (o.getAssignee() == null || !o.getAssignee().getId().equals(actor.getId())))
            throw new ApiException(HttpStatus.FORBIDDEN, "Technician can act only on assigned work");
        if (r.status() == WorkOrderStatus.CLOSED && actor.getRole() != Role.MANAGER)
            throw new ApiException(HttpStatus.FORBIDDEN, "Only a manager can close work");
        if (r.status() == WorkOrderStatus.CANCELLED && actor.getRole() == Role.TECHNICIAN)
            throw new ApiException(HttpStatus.FORBIDDEN, "Technician cannot cancel work");
        transitionInternal(o, r.status(), actor, r.note());
        return orders.save(o);
    }

    @Transactional
    public void addPart(Long id, PartUsageRequest r, Authentication auth) {
        WorkOrder o = get(id, auth);
        User actor = currentUser(auth);
        if (actor.getRole() == Role.TECHNICIAN && !Objects.equals(o.getAssignee() == null ? null : o.getAssignee().getId(), actor.getId()))
            throw new ApiException(HttpStatus.FORBIDDEN, "Only the assigned technician can log parts");
        Part p = parts.findById(r.partId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Part not found"));
        if (p.getStockQuantity() < r.quantity())
            throw new ApiException(HttpStatus.CONFLICT, "Insufficient stock");
        p.setStockQuantity(p.getStockQuantity() - r.quantity());
        PartUsage usage = new PartUsage();
        usage.setWorkOrder(o); usage.setPart(p); usage.setQuantity(r.quantity());
        parts.save(p); usages.save(usage);
    }

    @Transactional
    public void addTime(Long id, TimeLogRequest r, Authentication auth) {
        WorkOrder o = get(id, auth);
        User actor = currentUser(auth);
        if (actor.getRole() != Role.TECHNICIAN || o.getAssignee() == null || !o.getAssignee().getId().equals(actor.getId()))
            throw new ApiException(HttpStatus.FORBIDDEN, "Only the assigned technician can log time");
        TimeLog log = new TimeLog();
        log.setWorkOrder(o); log.setTechnician(actor); log.setMinutes(r.minutes()); log.setNote(r.note());
        timeLogs.save(log);
    }

    public List<WorkOrderStatusHistory> history(Long id, Authentication auth) {
        get(id, auth);
        return history.findByWorkOrderIdOrderByChangedAtAsc(id);
    }

    private void transitionInternal(WorkOrder o, WorkOrderStatus next, User actor, String note) {
        WorkOrderStatus from = o.getStatus();
        if (from == next) throw new ApiException(HttpStatus.CONFLICT, "Work order is already in " + next);
        if (!allowed(from, next))
            throw new ApiException(HttpStatus.CONFLICT, "Illegal transition from " + from + " to " + next);

        if (next == WorkOrderStatus.IN_PROGRESS && actor.getRole() == Role.TECHNICIAN &&
                (o.getAssignee() == null || !o.getAssignee().getId().equals(actor.getId())))
            throw new ApiException(HttpStatus.FORBIDDEN, "Only the assigned technician can start work");

        o.setStatus(next); o.touch();
        WorkOrderStatusHistory h = new WorkOrderStatusHistory();
        h.setWorkOrder(o); h.setFromStatus(from); h.setToStatus(next); h.setChangedBy(actor); h.setNote(note);
        history.save(h);
    }

    private boolean allowed(WorkOrderStatus from, WorkOrderStatus to) {
        return switch (from) {
            case NEW -> to == WorkOrderStatus.ASSIGNED || to == WorkOrderStatus.CANCELLED;
            case ASSIGNED -> to == WorkOrderStatus.IN_PROGRESS || to == WorkOrderStatus.CANCELLED;
            case IN_PROGRESS -> to == WorkOrderStatus.ON_HOLD || to == WorkOrderStatus.COMPLETED || to == WorkOrderStatus.CANCELLED;
            case ON_HOLD -> to == WorkOrderStatus.IN_PROGRESS || to == WorkOrderStatus.CANCELLED;
            case COMPLETED -> to == WorkOrderStatus.CLOSED;
            case CLOSED, CANCELLED -> false;
        };
    }

    private LocalDateTime defaultSla(Priority p) {
        int hours = switch (p) { case CRITICAL -> 4; case HIGH -> 12; case MEDIUM -> 24; case LOW -> 72; };
        return LocalDateTime.now().plusHours(hours);
    }

    private User currentUser(Authentication auth) {
        return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void ensureVisible(WorkOrder o, User u) {
        if (u.getRole() == Role.CUSTOMER && (u.getCustomer() == null || !o.getCustomer().getId().equals(u.getCustomer().getId())))
            throw new ApiException(HttpStatus.FORBIDDEN, "You cannot access this work order");
        if (u.getRole() == Role.TECHNICIAN && (o.getAssignee() == null || !o.getAssignee().getId().equals(u.getId())))
            throw new ApiException(HttpStatus.FORBIDDEN, "You cannot access this work order");
    }
}
