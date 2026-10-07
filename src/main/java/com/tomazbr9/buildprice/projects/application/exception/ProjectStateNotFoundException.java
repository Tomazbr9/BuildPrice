package com.tomazbr9.buildprice.projects.application.exception;

public class ProjectStateNotFoundException extends RuntimeException {
    public ProjectStateNotFoundException() {
        super("Estado não encontrado");
    }
}
