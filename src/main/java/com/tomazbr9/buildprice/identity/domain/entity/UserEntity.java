package com.tomazbr9.buildprice.identity.domain.entity;

import com.tomazbr9.buildprice.identity.domain.enums.UserRole;

import java.util.UUID;

public class UserEntity {

    private UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private UserRole role;

    public UserEntity(){

    }

    public static UserEntity create(String name, String email, String passwordHash){
        UserEntity usuario = new UserEntity();

        usuario.id = UUID.randomUUID();
        usuario.name = name;
        usuario.email = email;
        usuario.passwordHash = passwordHash;
        usuario.role = UserRole.USUARIO;

        return usuario;

    }

    public static UserEntity restore(UUID id, String nome, String email, String senhaHash, UserRole papel){
        UserEntity user = new UserEntity();

        user.id = id;
        user.name = nome;
        user.email = email;
        user.passwordHash = senhaHash;
        user.role = papel;

        return user;
    }

    public UUID getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public UserRole getRole(){
        return role;
    }

}
