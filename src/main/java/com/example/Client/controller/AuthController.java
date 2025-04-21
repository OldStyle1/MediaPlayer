package com.example.Client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.Client.model.Role;
import com.example.Client.model.RoleType;
import com.example.Client.payload.LoginRequest;
import com.example.Client.payload.RegisterRequest;
import com.example.Client.payload.JwtResponse;
import com.example.Client.service.RoleService;
import com.example.Client.service.UserService;
import com.example.Client.util.JwtUtils;
import com.example.Client.model.User;

import java.util.HashSet;
import java.util.Set;

@Tag(name = "Авторизация и регистрация", description = "API для аутентификации и регистрации пользователей")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Вход в систему",
            description = "Аутентификация пользователя и получение JWT токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Успешная аутентификация",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Неверные учетные данные")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String jwt = jwtUtils.generateToken(authentication);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Регистрация нового пользователя",
            description = "Создание нового аккаунта с ролью USER и последующей автоматической аутентификацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Успешная регистрация",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Пользователь с таким именем уже существует")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest().body("Username is already taken!");
            }

            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setEmail(registerRequest.getEmail());
            user.setActive(true);
            
            // Назначаем роль USER по умолчанию
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.findByName(RoleType.ROLE_USER.name())
                .orElseThrow(() -> new RuntimeException("Error: Role USER not found.")));
            user.setRoles(roles);

            userService.save(user);

            // Автоматический логин после регистрации
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(registerRequest.getUsername());
            loginRequest.setPassword(registerRequest.getPassword());
            return login(loginRequest);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Регистрация нового издателя контента",
            description = "Создание нового аккаунта с ролью PUBLISHER (требуется одобрение администратора)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Заявка на регистрацию издателя отправлена"),
            @ApiResponse(responseCode = "400",
                    description = "Пользователь с таким именем уже существует")
    })
    @PostMapping("/register/publisher")
    public ResponseEntity<?> registerPublisher(@RequestBody RegisterRequest registerRequest) {
        try {
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest().body("Username is already taken!");
            }

            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setEmail(registerRequest.getEmail());
            user.setActive(true);
            
            // Назначаем роль PUBLISHER
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.findByName(RoleType.ROLE_PUBLISHER.name())
                .orElseThrow(() -> new RuntimeException("Error: Role PUBLISHER not found.")));
            user.setRoles(roles);

            userService.save(user);

            return ResponseEntity.ok("Publisher registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}