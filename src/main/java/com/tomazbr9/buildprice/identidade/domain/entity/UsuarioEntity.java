package com.tomazbr9.buildprice.identidade.domain.entity;

import com.tomazbr9.buildprice.identidade.domain.enums.PapelUsuario;

import java.util.UUID;

public class UsuarioEntity {

    private UUID id;
    private String nome;
    private String email;
    private String senhaHash;
    private PapelUsuario papel;

    public UsuarioEntity(){

    }

    public static UsuarioEntity create(String nome, String email, String senhaHash){
        UsuarioEntity usuario = new UsuarioEntity();

        usuario.id = UUID.randomUUID();
        usuario.nome = nome;
        usuario.email = email;
        usuario.senhaHash = senhaHash;
        usuario.papel = PapelUsuario.ROLE_USUARIO;

        return usuario;

    }

    public static UsuarioEntity restore(UUID id, String nome, String email, String senhaHash, PapelUsuario papel){
        UsuarioEntity usuario = new UsuarioEntity();

        usuario.id = id;
        usuario.nome = nome;
        usuario.email = email;
        usuario.senhaHash = senhaHash;
        usuario.papel = papel;

        return usuario;
    }

    public UUID getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }

    public String getEmail(){
        return email;
    }

    public String getSenhaHash(){
        return senhaHash;
    }

    public PapelUsuario getPapel(){
        return papel;
    }

}
