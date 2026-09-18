package com.tomazbr9.buildprice.identity.domain.entity;

import com.tomazbr9.buildprice.identity.domain.enums.UserRole;
import com.tomazbr9.buildprice.identity.domain.valueobjects.Email;

import java.util.UUID;

public class UserEntity {

    private UUID id;
    private String name;
    private Email email;
    private String passwordHash;
    private UserRole role;

    public static UserEntity create(String name, Email email, String passwordHash){
        UserEntity user = new UserEntity();

        user.id = UUID.randomUUID();
        user.name = name;
        user.email = email;
        user.passwordHash = passwordHash;
        user.role = UserRole.USER;

        return user;

    }

    public static UserEntity restore(UUID id, String name, Email email, String passwordHash, UserRole role){
        UserEntity user = new UserEntity();

        user.id = id;
        user.name = name;
        user.email = email;
        user.passwordHash = passwordHash;
        user.role = role;

        return user;
    }

    public UUID getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public Email getEmail(){
        return email;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public UserRole getRole(){
        return role;
    }

}
