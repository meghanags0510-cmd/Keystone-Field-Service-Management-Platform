package com.keystone.repository;
import com.keystone.domain.User;
import com.keystone.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    List<User> findByRole(Role role);
}
