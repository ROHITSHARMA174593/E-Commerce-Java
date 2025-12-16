package com.ecom.EcomSB.config;

import com.ecom.EcomSB.model.AppRole;
import com.ecom.EcomSB.model.Role;
import com.ecom.EcomSB.model.User;
import com.ecom.EcomSB.repositories.RoleRepository;
import com.ecom.EcomSB.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RoleSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Retrieve or create roles
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_SELLER)));

        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

        Set<Role> userRoles = Set.of(userRole);
        Set<Role> sellerRoles = Set.of(sellerRole);
        Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);

        // Create users if not already present
        createUser("user1", "user1@example.com", "password123", userRoles);
        createUser("seller1", "seller1@example.com", "password123", sellerRoles);
        createUser("admin", "admin@example.com", "adminPass", adminRoles);
    }

    private void createUser(String username, String email, String password, Set<Role> roles) {
        if (!userRepository.existsByUserName(username)) {
            User user = new User(username, email, passwordEncoder.encode(password));
            user.setRoles(roles);
            userRepository.save(user);
            System.out.println("Seeded user: " + username);
        } else {
            // Ensure roles and password are correct even if user exists
            userRepository.findByUserName(username).ifPresent(user -> {
                user.setRoles(roles);
                user.setPassword(passwordEncoder.encode(password)); // Force password reset
                userRepository.save(user);
                System.out.println("Updated roles and password for user: " + username);
            });
        }
    }
}
