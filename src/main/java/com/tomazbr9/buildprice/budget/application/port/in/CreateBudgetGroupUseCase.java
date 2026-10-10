package com.tomazbr9.buildprice.budget.application.port.in;

import com.tomazbr9.buildprice.budget.application.command.CreateBudgetGroupCommand;
import com.tomazbr9.buildprice.budget.application.dto.BudgetGroupResult;

public interface CreateBudgetGroupUseCase {

    BudgetGroupResult execute(
            CreateBudgetGroupCommand command
    );
}