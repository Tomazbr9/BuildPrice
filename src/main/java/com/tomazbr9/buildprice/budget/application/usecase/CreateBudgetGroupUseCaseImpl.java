package com.tomazbr9.buildprice.budget.application.usecase;

import com.tomazbr9.buildprice.budget.application.command.CreateBudgetGroupCommand;
import com.tomazbr9.buildprice.budget.application.dto.BudgetGroupResult;
import com.tomazbr9.buildprice.budget.application.exception.BudgetNotFoundException;
import com.tomazbr9.buildprice.budget.application.exception.InvalidParentBudgetGroupException;
import com.tomazbr9.buildprice.budget.application.mapper.BudgetGroupResultMapper;
import com.tomazbr9.buildprice.budget.application.port.in.CreateBudgetGroupUseCase;
import com.tomazbr9.buildprice.budget.application.port.out.BudgetGroupRepository;
import com.tomazbr9.buildprice.budget.application.port.out.BudgetRepository;
import com.tomazbr9.buildprice.budget.application.port.out.ProjectGateway;
import com.tomazbr9.buildprice.budget.domain.entity.Budget;
import com.tomazbr9.buildprice.budget.domain.entity.BudgetGroup;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateBudgetGroupUseCaseImpl
        implements CreateBudgetGroupUseCase {

    private final BudgetRepository budgetRepository;
    private final BudgetGroupRepository budgetGroupRepository;
    private final ProjectGateway projectGateway;
    private final CurrentUserProvider currentUserProvider;

    public CreateBudgetGroupUseCaseImpl(
            BudgetRepository budgetRepository,
            BudgetGroupRepository budgetGroupRepository,
            ProjectGateway projectGateway,
            CurrentUserProvider currentUserProvider
    ) {
        this.budgetRepository = budgetRepository;
        this.budgetGroupRepository = budgetGroupRepository;
        this.projectGateway = projectGateway;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional
    public BudgetGroupResult execute(
            CreateBudgetGroupCommand command
    ) {

        UUID userId =
                currentUserProvider.getCurrentUserId();

        Budget budget =
                budgetRepository
                        .findById(command.budgetId())
                        .orElseThrow(
                                BudgetNotFoundException::new
                        );

        boolean budgetBelongsToUser =
                projectGateway
                        .findById(budget.getProjectId())
                        .filter(project ->
                                project.userId()
                                        .equals(userId)
                        )
                        .isPresent();

        if (!budgetBelongsToUser) {
            throw new BudgetNotFoundException();
        }

        if (command.parentGroupId() != null) {

            BudgetGroup parent =
                    budgetGroupRepository
                            .findById(command.parentGroupId())
                            .orElseThrow(
                                    InvalidParentBudgetGroupException::new
                            );

            if (!parent.getBudgetId()
                    .equals(command.budgetId())) {

                throw new InvalidParentBudgetGroupException();
            }
        }

        BudgetGroup group =
                BudgetGroup.create(
                        command.budgetId(),
                        command.parentGroupId(),
                        command.name(),
                        command.sortOrder()
                );

        BudgetGroup saved =
                budgetGroupRepository.save(group);

        return BudgetGroupResultMapper.toResult(saved);
    }
}