package com.vetvision.config;

import com.vetvision.entity.Role;
import com.vetvision.entity.User;
import com.vetvision.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapOrganization {
    @Bean
    CommandLineRunner organizationBootstrapRunner(UserRepository users, PasswordEncoder encoder,
            @Value("${app.bootstrap.organization.email:}") String email,
            @Value("${app.bootstrap.organization.password:}") String password,
            @Value("${app.bootstrap.organization.first-name:Organization}") String firstName,
            @Value("${app.bootstrap.organization.last-name:Admin}") String lastName) {
        return args -> {
            if (email.isBlank() && password.isBlank()) return;
            if (email.isBlank() || password.length() < 12) {
                throw new IllegalStateException("Set both BOOTSTRAP_ORG_EMAIL and BOOTSTRAP_ORG_PASSWORD (at least 12 characters).");
            }
            if (users.existsByEmailIgnoreCase(email)) return;
            User user = new User();
            user.setEmail(email.trim().toLowerCase());
            user.setPassword(encoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole(Role.ORGANIZATION);
            users.save(user);
        };
    }
}