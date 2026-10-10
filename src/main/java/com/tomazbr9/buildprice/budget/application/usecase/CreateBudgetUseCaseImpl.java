package com.tomazbr9.buildprice.budget.application.usecase;

import com.tomazbr9.buildprice.budget.application.command.CreateBudgetCommand;
import com.tomazbr9.buildprice.budget.application.dto.BudgetResult;
import com.tomazbr9.buildprice.budget.application.exception.BudgetProjectNotFoundException;
import com.tomazbr9.buildprice.budget.application.exception.SinapiTableVersionNotFoundException;
import com.tomazbr9.buildprice.budget.application.mapper.BudgetResultMapper;
import com.tomazbr9.buildprice.budget.application.port.in.CreateBudgetUseCase;
import com.tomazbr9.buildprice.budget.application.port.out.BudgetRepository;
import com.tomazbr9.buildprice.budget.application.port.out.ProjectGateway;
import com.tomazbr9.buildprice.budget.application.port.out.SinapiCatalogGateway;
import com.tomazbr9.buildprice.budget.domain.entity.Budget;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateBudgetUseCaseImpl
        implements CreateBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final ProjectGateway projectGateway;
    private final SinapiCatalogGateway sinapiCatalogGateway;
    private final CurrentUserProvider currentUserProvider;

    public CreateBudgetUseCaseImpl(
            BudgetRepository budgetRepository,
            ProjectGateway projectGateway,
            SinapiCatalogGateway sinapiCatalogGateway,
            CurrentUserProvider currentUserProvider
    ) {
        this.budgetRepository = budgetRepository;
        this.projectGateway = projectGateway;
        this.sinapiCatalogGateway = sinapiCatalogGateway;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional
    public BudgetResult execute(
            CreateBudgetCommand command
    ) {

        UUID userId =
                currentUserProvider.getCurrentUserId();

        ProjectGateway.ProjectData project =
                projectGateway
                        .findById(command.projectId())
                        .filter(found ->
                                found.userId()
                                        .equals(userId)
                        )
                        .orElseThrow(
                                BudgetProjectNotFoundException::new
                        );

        if (!sinapiCatalogGateway.versionExists(
                command.sinapiTableVersionId()
        )) {
            throw new SinapiTableVersionNotFoundException();
        }

        Budget budget =
                Budget.create(
                        project.id(),
                        command.sinapiTableVersionId(),
                        project.defaultBdiPercentage()
                );

        Budget saved =
                budgetRepository.save(budget);

        return BudgetResultMapper.toResult(saved);
    }
}