package com.keystone.config;

import com.keystone.domain.*;
import com.keystone.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class SeedDataConfig {
    @Bean
    CommandLineRunner seed(CustomerRepository customers, UserRepository users, SiteRepository sites,
                           PartRepository parts, PasswordEncoder encoder) {
        return args -> {
            Customer acme = customers.findAll().stream().findFirst().orElseGet(() ->
                    customers.save(new Customer("Acme Business Park", "ops@acme.local", "+91 9000000000")));
            Site site = sites.findByCustomerId(acme.getId()).stream().findFirst().orElseGet(() -> {
                Site s = new Site(); s.setCustomer(acme); s.setName("Acme Tower"); s.setAddress("100 Main Road"); s.setCity("Bengaluru");
                return sites.save(s);
            });

            createUser(users, encoder, "Meridian Manager", "manager@keystone.local", Role.MANAGER, null);
            createUser(users, encoder, "Dispatch Desk", "dispatcher@keystone.local", Role.DISPATCHER, null);
            User tech = createUser(users, encoder, "Field Technician", "technician@keystone.local", Role.TECHNICIAN, null);
            createUser(users, encoder, "Acme Customer", "customer@keystone.local", Role.CUSTOMER, acme);

            if (parts.count() == 0) {
                Part p = new Part(); p.setName("HVAC Filter"); p.setSku("HVAC-FLT-001"); p.setUnitCost(new BigDecimal("24.50")); p.setStockQuantity(25); parts.save(p);
                Part q = new Part(); q.setName("Copper Connector"); q.setSku("ELEC-CON-001"); q.setUnitCost(new BigDecimal("8.75")); q.setStockQuantity(40); parts.save(q);
            }
        };
    }

    private User createUser(UserRepository users, PasswordEncoder encoder, String name, String email,
                            Role role, Customer customer) {
        return users.findByEmailIgnoreCase(email).orElseGet(() -> {
            User u = new User(); u.setFullName(name); u.setEmail(email); u.setPasswordHash(encoder.encode("Password123!"));
            u.setRole(role); u.setCustomer(customer); u.setEnabled(true); return users.save(u);
        });
    }
}
