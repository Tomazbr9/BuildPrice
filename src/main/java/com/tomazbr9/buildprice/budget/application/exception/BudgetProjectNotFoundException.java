package com.tomazbr9.buildprice.budget.application.exception;

public class BudgetProjectNotFoundException extends RuntimeException {
    public BudgetProjectNotFoundException() {
        super("Projeto não encontrado");
    }
}
