package com.tomazbr9.buildprice.budget.application.port.in;

import com.tomazbr9.buildprice.budget.application.command.CreateBudgetCommand;
import com.tomazbr9.buildprice.budget.application.dto.BudgetResult;

public interface CreateBudgetUseCase {

    BudgetResult execute(
            CreateBudgetCommand command
    );
}