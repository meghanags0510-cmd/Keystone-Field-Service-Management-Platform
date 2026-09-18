package com.keystone.service;

import com.keystone.domain.Customer;
import com.keystone.domain.Site;
import com.keystone.dto.*;
import com.keystone.exception.ApiException;
import com.keystone.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CustomerService {
    private final CustomerRepository customers;
    private final SiteRepository sites;
    public CustomerService(CustomerRepository customers, SiteRepository sites) {
        this.customers = customers; this.sites = sites;
    }

    public List<Customer> list() { return customers.findAll(); }

    public Customer create(CustomerRequest r) {
        if (customers.existsByEmailIgnoreCase(r.email()))
            throw new ApiException(HttpStatus.CONFLICT, "Customer email already exists");
        return customers.save(new Customer(r.name().trim(), r.email().trim().toLowerCase(), r.phone()));
    }

    public List<Site> sites(Long customerId) {
        if (!customers.existsById(customerId)) throw new ApiException(HttpStatus.NOT_FOUND, "Customer not found");
        return sites.findByCustomerId(customerId);
    }

    public Site createSite(Long customerId, SiteRequest r) {
        Customer c = customers.findById(customerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));
        Site s = new Site();
        s.setCustomer(c); s.setName(r.name().trim()); s.setAddress(r.address().trim()); s.setCity(r.city().trim());
        return sites.save(s);
    }
}
