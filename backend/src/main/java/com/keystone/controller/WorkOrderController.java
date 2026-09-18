package com.keystone.controller;

import com.keystone.domain.*;
import com.keystone.dto.*;
import com.keystone.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {
    private final WorkOrderService service;
    public WorkOrderController(WorkOrderService service) { this.service = service; }

    @GetMapping
    public List<WorkOrder> list(Authentication auth) { return service.list(auth); }

    @GetMapping("/{id}")
    public WorkOrder get(@PathVariable Long id, Authentication auth) { return service.get(id, auth); }

    @PostMapping @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER','CUSTOMER')")
    public WorkOrder create(@Valid @RequestBody WorkOrderRequest r) { return service.create(r); }

    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')")
    public WorkOrder update(@PathVariable Long id, @Valid @RequestBody WorkOrderRequest r, Authentication auth) {
        return service.update(id, r, auth);
    }

    @PostMapping("/{id}/assign") @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')")
    public WorkOrder assign(@PathVariable Long id, @Valid @RequestBody AssignRequest r, Authentication auth) {
        return service.assign(id, r, auth);
    }

    @PostMapping("/{id}/status")
    public WorkOrder status(@PathVariable Long id, @Valid @RequestBody StatusRequest r, Authentication auth) {
        return service.status(id, r, auth);
    }

    @GetMapping("/{id}/history")
    public List<WorkOrderStatusHistory> history(@PathVariable Long id, Authentication auth) {
        return service.history(id, auth);
    }

    @PostMapping("/{id}/parts") @PreAuthorize("hasAnyRole('MANAGER','TECHNICIAN')")
    public Map<String,String> parts(@PathVariable Long id, @Valid @RequestBody PartUsageRequest r, Authentication auth) {
        service.addPart(id, r, auth); return Map.of("message", "Part usage recorded");
    }

    @PostMapping("/{id}/time") @PreAuthorize("hasRole('TECHNICIAN')")
    public Map<String,String> time(@PathVariable Long id, @Valid @RequestBody TimeLogRequest r, Authentication auth) {
        service.addTime(id, r, auth); return Map.of("message", "Time recorded");
    }
}
