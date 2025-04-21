package com.example.Client.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

//TODO make roles such as: ROLE_USER, ROLE_ADMIN, ROLE_PUBLISHER
//watch last chat 403
@Entity
@Table(name = "roles")

public class Role{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}