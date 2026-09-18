package com.keystone.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name", nullable = false) private String fullName;
    @Column(nullable = false, unique = true) private String email;
    @Column(name = "password_hash", nullable = false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id") private Customer customer;
    @Column(nullable = false) private boolean enabled = true;

    public User() {}
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { passwordHash = v; }
    public Role getRole() { return role; }
    public void setRole(Role v) { role = v; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer v) { customer = v; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean v) { enabled = v; }
}
