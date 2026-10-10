package com.tomazbr9.buildprice.budget.application.exception;

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException() {
        super("Orçamento não encontrado");
    }
}
