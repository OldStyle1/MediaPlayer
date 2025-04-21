package com.example.Client.controller;

import com.example.Client.model.Role;
import com.example.Client.model.RoleType;
import com.example.Client.model.User;
import com.example.Client.payload.RegisterRequest;
import com.example.Client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAdmin(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.createUserWithRole(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                RoleType.ROLE_ADMIN
            );
            return ResponseEntity.ok("Admin user created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/users/create-publisher")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPublisher(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.createUserWithRole(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                RoleType.ROLE_PUBLISHER
            );
            return ResponseEntity.ok("Publisher user created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserRoles(@PathVariable Long userId) {
        try {
            Set<Role> roles = userService.getUserRoles(userId);
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRoleToUser(@PathVariable Long userId, @PathVariable String roleName) {
        try {
            User user = userService.addRoleToUser(userId, roleName);
            return ResponseEntity.ok("Role added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName) {
        try {
            User user = userService.removeRoleFromUser(userId, roleName);
            return ResponseEntity.ok("Role removed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
} 