package com.tomazbr9.buildprice.budget.application.exception;

public class InvalidParentBudgetGroupException extends RuntimeException {
    public InvalidParentBudgetGroupException() {
        super("O grupo de orçamento pai é inválido.");
    }
}
