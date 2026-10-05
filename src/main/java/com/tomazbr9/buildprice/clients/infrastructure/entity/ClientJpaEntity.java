package com.tomazbr9.buildprice.clients.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "tb_clients")
public class ClientJpaEntity {

    @Id
    private UUID id;

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    protected ClientJpaEntity() {
    }

    public ClientJpaEntity(
            UUID id,
            UUID userId,
            String name,
            String email,
            String phone
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}