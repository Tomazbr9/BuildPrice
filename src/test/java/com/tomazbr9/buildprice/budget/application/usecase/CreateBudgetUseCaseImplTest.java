package com.tomazbr9.buildprice.budget.application.usecase;

import com.tomazbr9.buildprice.budget.application.command.CreateBudgetCommand;
import com.tomazbr9.buildprice.budget.application.dto.BudgetResult;
import com.tomazbr9.buildprice.budget.application.exception.BudgetProjectNotFoundException;
import com.tomazbr9.buildprice.budget.application.exception.SinapiTableVersionNotFoundException;
import com.tomazbr9.buildprice.budget.application.port.out.BudgetRepository;
import com.tomazbr9.buildprice.budget.application.port.out.ProjectGateway;
import com.tomazbr9.buildprice.budget.application.port.out.SinapiCatalogGateway;
import com.tomazbr9.buildprice.budget.domain.entity.Budget;
import com.tomazbr9.buildprice.budget.domain.enums.BudgetStatus;
import com.tomazbr9.buildprice.shared.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBudgetUseCaseImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ProjectGateway projectGateway;

    @Mock
    private SinapiCatalogGateway sinapiCatalogGateway;

    @Mock
    private CurrentUserProvider currentUserProvider;

    private CreateBudgetUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateBudgetUseCaseImpl(
                budgetRepository,
                projectGateway,
                sinapiCatalogGateway,
                currentUserProvider
        );
    }

    @Test
    void shouldCreateBudgetUsingProjectDefaultBdi() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID versionId = UUID.randomUUID();

        BigDecimal projectBdi =
                new BigDecimal("20.00");

        ProjectGateway.ProjectData projectData =
                new ProjectGateway.ProjectData(
                        projectId,
                        userId,
                        projectBdi
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(projectGateway.findById(projectId))
                .thenReturn(Optional.of(projectData));

        when(sinapiCatalogGateway.versionExists(versionId))
                .thenReturn(true);

        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        CreateBudgetCommand command =
                new CreateBudgetCommand(
                        projectId,
                        versionId
                );

        BudgetResult result =
                useCase.execute(command);

        assertNotNull(result.id());

        assertEquals(
                projectId,
                result.projectId()
        );

        assertEquals(
                versionId,
                result.sinapiTableVersionId()
        );

        assertEquals(
                BudgetStatus.DRAFT,
                result.status()
        );

        assertEquals(
                0,
                projectBdi.compareTo(
                        result.bdiPercentage()
                )
        );
    }

    @Test
    void shouldPersistBudgetWithProjectDefaultBdi() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID versionId = UUID.randomUUID();

        BigDecimal projectBdi =
                new BigDecimal("25.50");

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(projectGateway.findById(projectId))
                .thenReturn(
                        Optional.of(
                                new ProjectGateway.ProjectData(
                                        projectId,
                                        userId,
                                        projectBdi
                                )
                        )
                );

        when(sinapiCatalogGateway.versionExists(versionId))
                .thenReturn(true);

        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        useCase.execute(
                new CreateBudgetCommand(
                        projectId,
                        versionId
                )
        );

        ArgumentCaptor<Budget> captor =
                ArgumentCaptor.forClass(
                        Budget.class
                );

        verify(budgetRepository)
                .save(captor.capture());

        Budget budget =
                captor.getValue();

        assertEquals(
                projectId,
                budget.getProjectId()
        );

        assertEquals(
                versionId,
                budget.getSinapiTableVersionId()
        );

        assertEquals(
                BudgetStatus.DRAFT,
                budget.getStatus()
        );

        assertEquals(
                0,
                projectBdi.compareTo(
                        budget.getBdiPercentage()
                )
        );
    }

    @Test
    void shouldThrowWhenProjectDoesNotExist() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(projectGateway.findById(projectId))
                .thenReturn(Optional.empty());

        CreateBudgetCommand command =
                new CreateBudgetCommand(
                        projectId,
                        UUID.randomUUID()
                );

        assertThrows(
                BudgetProjectNotFoundException.class,
                () -> useCase.execute(command)
        );

        verifyNoInteractions(
                sinapiCatalogGateway
        );

        verify(
                budgetRepository,
                never()
        ).save(any());
    }

    @Test
    void shouldThrowWhenProjectBelongsToAnotherUser() {

        UUID authenticatedUserId =
                UUID.randomUUID();

        UUID projectOwnerId =
                UUID.randomUUID();

        UUID projectId =
                UUID.randomUUID();

        ProjectGateway.ProjectData projectData =
                new ProjectGateway.ProjectData(
                        projectId,
                        projectOwnerId,
                        new BigDecimal("20.00")
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(
                        authenticatedUserId
                );

        when(projectGateway.findById(projectId))
                .thenReturn(
                        Optional.of(projectData)
                );

        CreateBudgetCommand command =
                new CreateBudgetCommand(
                        projectId,
                        UUID.randomUUID()
                );

        assertThrows(
                BudgetProjectNotFoundException.class,
                () -> useCase.execute(command)
        );

        verifyNoInteractions(
                sinapiCatalogGateway
        );

        verify(
                budgetRepository,
                never()
        ).save(any());
    }

    @Test
    void shouldThrowWhenSinapiVersionDoesNotExist() {

        UUID userId =
                UUID.randomUUID();

        UUID projectId =
                UUID.randomUUID();

        UUID versionId =
                UUID.randomUUID();

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(projectGateway.findById(projectId))
                .thenReturn(
                        Optional.of(
                                new ProjectGateway.ProjectData(
                                        projectId,
                                        userId,
                                        new BigDecimal("20.00")
                                )
                        )
                );

        when(
                sinapiCatalogGateway
                        .versionExists(versionId)
        ).thenReturn(false);

        CreateBudgetCommand command =
                new CreateBudgetCommand(
                        projectId,
                        versionId
                );

        assertThrows(
                SinapiTableVersionNotFoundException.class,
                () -> useCase.execute(command)
        );

        verify(
                budgetRepository,
                never()
        ).save(any());
    }

    @Test
    void shouldUpdateBudgetBdi() {

        Budget budget =
                Budget.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new BigDecimal("20.00")
                );

        budget.updateBdiPercentage(
                new BigDecimal("25.00")
        );

        assertEquals(
                0,
                new BigDecimal("25.00")
                        .compareTo(
                                budget.getBdiPercentage()
                        )
        );
    }

    @Test
    void shouldNotAcceptNegativeBdi() {

        Budget budget =
                Budget.create(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new BigDecimal("20.00")
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        budget.updateBdiPercentage(
                                new BigDecimal("-1")
                        )
        );
    }
}