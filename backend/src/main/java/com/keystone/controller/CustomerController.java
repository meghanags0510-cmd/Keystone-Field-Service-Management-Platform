package com.keystone.controller;

import com.keystone.domain.*;
import com.keystone.dto.*;
import com.keystone.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }

    @GetMapping @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')")
    public List<Customer> list() { return service.list(); }

    @PostMapping @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')")
    public Customer create(@Valid @RequestBody CustomerRequest r) { return service.create(r); }

    @GetMapping("/{id}/sites") @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER','CUSTOMER')")
    public List<Site> sites(@PathVariable Long id) { return service.sites(id); }

    @PostMapping("/{id}/sites") @PreAuthorize("hasAnyRole('MANAGER','DISPATCHER')")
    public Site createSite(@PathVariable Long id, @Valid @RequestBody SiteRequest r) { return service.createSite(id, r); }
}
