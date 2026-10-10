package com.tomazbr9.buildprice.budget.application.usecase;

import com.tomazbr9.buildprice.budget.application.command.CreateBudgetGroupCommand;
import com.tomazbr9.buildprice.budget.application.dto.BudgetGroupResult;
import com.tomazbr9.buildprice.budget.application.exception.BudgetNotFoundException;
import com.tomazbr9.buildprice.budget.application.exception.InvalidParentBudgetGroupException;
import com.tomazbr9.buildprice.budget.application.port.out.BudgetGroupRepository;
import com.tomazbr9.buildprice.budget.application.port.out.BudgetRepository;
import com.tomazbr9.buildprice.budget.application.port.out.ProjectGateway;
import com.tomazbr9.buildprice.budget.domain.entity.Budget;
import com.tomazbr9.buildprice.budget.domain.entity.BudgetGroup;
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
class CreateBudgetGroupUseCaseImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private BudgetGroupRepository budgetGroupRepository;

    @Mock
    private ProjectGateway projectGateway;

    @Mock
    private CurrentUserProvider currentUserProvider;

    private CreateBudgetGroupUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateBudgetGroupUseCaseImpl(
                budgetRepository,
                budgetGroupRepository,
                projectGateway,
                currentUserProvider
        );
    }

    @Test
    void shouldCreateRootBudgetGroup() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        Budget budget =
                Budget.restore(
                        budgetId,
                        projectId,
                        UUID.randomUUID(),
                        BudgetStatus.DRAFT,
                        new BigDecimal("20.00")
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(budgetRepository.findById(budgetId))
                .thenReturn(Optional.of(budget));

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

        when(budgetGroupRepository.save(any(BudgetGroup.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        CreateBudgetGroupCommand command =
                new CreateBudgetGroupCommand(
                        budgetId,
                        null,
                        "Fundação",
                        0
                );

        BudgetGroupResult result =
                useCase.execute(command);

        assertNotNull(result.id());
        assertEquals(budgetId, result.budgetId());
        assertNull(result.parentGroupId());
        assertEquals("Fundação", result.name());
        assertEquals(0, result.sortOrder());

        verify(budgetGroupRepository)
                .save(any(BudgetGroup.class));
    }

    @Test
    void shouldCreateChildBudgetGroup() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        UUID parentGroupId = UUID.randomUUID();

        Budget budget =
                Budget.restore(
                        budgetId,
                        projectId,
                        UUID.randomUUID(),
                        BudgetStatus.DRAFT,
                        BigDecimal.ZERO
                );

        BudgetGroup parent =
                BudgetGroup.restore(
                        parentGroupId,
                        budgetId,
                        null,
                        "Fundação",
                        0
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(budgetRepository.findById(budgetId))
                .thenReturn(Optional.of(budget));

        when(projectGateway.findById(projectId))
                .thenReturn(
                        Optional.of(
                                new ProjectGateway.ProjectData(
                                        projectId,
                                        userId,
                                        BigDecimal.ZERO
                                )
                        )
                );

        when(budgetGroupRepository.findById(parentGroupId))
                .thenReturn(Optional.of(parent));

        when(budgetGroupRepository.save(any(BudgetGroup.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        CreateBudgetGroupCommand command =
                new CreateBudgetGroupCommand(
                        budgetId,
                        parentGroupId,
                        "Escavação",
                        1
                );

        BudgetGroupResult result =
                useCase.execute(command);

        assertEquals(parentGroupId, result.parentGroupId());
        assertEquals("Escavação", result.name());
    }

    @Test
    void shouldThrowWhenBudgetDoesNotExist() {

        UUID budgetId = UUID.randomUUID();

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(UUID.randomUUID());

        when(budgetRepository.findById(budgetId))
                .thenReturn(Optional.empty());

        CreateBudgetGroupCommand command =
                new CreateBudgetGroupCommand(
                        budgetId,
                        null,
                        "Fundação",
                        0
                );

        assertThrows(
                BudgetNotFoundException.class,
                () -> useCase.execute(command)
        );

        verifyNoInteractions(projectGateway);

        verify(budgetGroupRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowWhenBudgetBelongsToAnotherUser() {

        UUID authenticatedUserId = UUID.randomUUID();
        UUID projectOwnerId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        Budget budget =
                Budget.restore(
                        budgetId,
                        projectId,
                        UUID.randomUUID(),
                        BudgetStatus.DRAFT,
                        BigDecimal.ZERO
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(authenticatedUserId);

        when(budgetRepository.findById(budgetId))
                .thenReturn(Optional.of(budget));

        when(projectGateway.findById(projectId))
                .thenReturn(
                        Optional.of(
                                new ProjectGateway.ProjectData(
                                        projectId,
                                        projectOwnerId,
                                        BigDecimal.ZERO
                                )
                        )
                );

        CreateBudgetGroupCommand command =
                new CreateBudgetGroupCommand(
                        budgetId,
                        null,
                        "Fundação",
                        0
                );

        assertThrows(
                BudgetNotFoundException.class,
                () -> useCase.execute(command)
        );

        verify(budgetGroupRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowWhenParentGroupDoesNotExist() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        UUID parentGroupId = UUID.randomUUID();

        Budget budget =
                Budget.restore(
                        budgetId,
                        projectId,
                        UUID.randomUUID(),
                        BudgetStatus.DRAFT,
                        BigDecimal.ZERO
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(budgetRepository.findById(budgetId))
                .thenReturn(Optional.of(budget));

        when(projectGateway.findById(projectId))
                .thenReturn(
                        Optional.of(
                                new ProjectGateway.ProjectData(
                                        projectId,
                                        userId,
                                        BigDecimal.ZERO
                                )
                        )
                );

        when(budgetGroupRepository.findById(parentGroupId))
                .thenReturn(Optional.empty());

        CreateBudgetGroupCommand command =
                new CreateBudgetGroupCommand(
                        budgetId,
                        parentGroupId,
                        "Escavação",
                        1
                );

        assertThrows(
                InvalidParentBudgetGroupException.class,
                () -> useCase.execute(command)
        );

        verify(budgetGroupRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowWhenParentGroupBelongsToAnotherBudget() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        UUID otherBudgetId =
                UUID.randomUUID();

        UUID parentGroupId =
                UUID.randomUUID();

        Budget budget =
                Budget.restore(
                        budgetId,
                        projectId,
                        UUID.randomUUID(),
                        BudgetStatus.DRAFT,
                        BigDecimal.ZERO
                );

        BudgetGroup parent =
                BudgetGroup.restore(
                        parentGroupId,
                        otherBudgetId,
                        null,
                        "Outro Grupo",
                        0
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(budgetRepository.findById(budgetId))
                .thenReturn(Optional.of(budget));

        when(projectGateway.findById(projectId))
                .thenReturn(
                        Optional.of(
                                new ProjectGateway.ProjectData(
                                        projectId,
                                        userId,
                                        BigDecimal.ZERO
                                )
                        )
                );

        when(budgetGroupRepository.findById(parentGroupId))
                .thenReturn(Optional.of(parent));

        CreateBudgetGroupCommand command =
                new CreateBudgetGroupCommand(
                        budgetId,
                        parentGroupId,
                        "Grupo Inválido",
                        0
                );

        assertThrows(
                InvalidParentBudgetGroupException.class,
                () -> useCase.execute(command)
        );

        verify(budgetGroupRepository, never())
                .save(any());
    }

    @Test
    void shouldPersistGroupWithCorrectBudgetAndParent() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        UUID parentGroupId = UUID.randomUUID();

        Budget budget =
                Budget.restore(
                        budgetId,
                        projectId,
                        UUID.randomUUID(),
                        BudgetStatus.DRAFT,
                        BigDecimal.ZERO
                );

        BudgetGroup parent =
                BudgetGroup.restore(
                        parentGroupId,
                        budgetId,
                        null,
                        "Fundação",
                        0
                );

        when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        when(budgetRepository.findById(budgetId))
                .thenReturn(Optional.of(budget));

        when(projectGateway.findById(projectId))
                .thenReturn(
                        Optional.of(
                                new ProjectGateway.ProjectData(
                                        projectId,
                                        userId,
                                        BigDecimal.ZERO
                                )
                        )
                );

        when(budgetGroupRepository.findById(parentGroupId))
                .thenReturn(Optional.of(parent));

        when(budgetGroupRepository.save(any(BudgetGroup.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        useCase.execute(
                new CreateBudgetGroupCommand(
                        budgetId,
                        parentGroupId,
                        "Concreto",
                        2
                )
        );

        ArgumentCaptor<BudgetGroup> captor =
                ArgumentCaptor.forClass(
                        BudgetGroup.class
                );

        verify(budgetGroupRepository)
                .save(captor.capture());

        BudgetGroup saved =
                captor.getValue();

        assertEquals(
                budgetId,
                saved.getBudgetId()
        );

        assertEquals(
                parentGroupId,
                saved.getParentGroupId()
        );
    }
}