package com.tomazbr9.buildprice.projects.application.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException() {
        super("Projeto não encontrado");
    }
}
