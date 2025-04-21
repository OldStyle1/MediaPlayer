package com.example.Client.service;

import com.example.Client.model.Role;
import com.example.Client.model.RoleType;
import com.example.Client.model.User;
import com.example.Client.repository.RoleRepository;
import com.example.Client.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user){
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Email уже используется");
        }
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {return userRepository.findByEmail(email);}

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getCurrentUser() {
        // Получение текущего пользователя из контекста безопасности
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User saveUser(User user){return userRepository.save(user);}

    public User createUserWithRole(String username, String email, String password, RoleType roleType) {
        // Проверяем существование пользователя
        if (existsByUsername(username)) {
            throw new RuntimeException("Пользователь с именем " + username + " уже существует");
        }
        
        if (existsByEmail(email)) {
            throw new RuntimeException("Email " + email + " уже используется");
        }
        
        // Создаем нового пользователя
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        
        // Назначаем роль
        Role role = roleRepository.findByName(roleType.name())
                .orElseThrow(() -> new RuntimeException("Роль " + roleType.name() + " не найдена"));
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        
        return userRepository.save(user);
    }

    /**
     * Получает все роли пользователя
     */
    public Set<Role> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        return user.getRoles();
    }

    /**
     * Добавляет роль пользователю
     */
    public User addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));
        
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    /**
     * Удаляет роль у пользователя
     */
    public User removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));
        
        user.getRoles().remove(role);
        return userRepository.save(user);
    }
}
