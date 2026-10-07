package com.tomazbr9.buildprice.projects.domain.entity;

import com.tomazbr9.buildprice.projects.domain.enums.ProjectTaxReliefRegime;

import java.math.BigDecimal;
import java.util.UUID;

public class Project {

    private final UUID id;
    private final UUID userId;

    private UUID clientId;
    private String name;
    private UUID stateId;
    private ProjectTaxReliefRegime taxReliefRegime;
    private BigDecimal bdiPercentage;

    private Project(
            UUID id,
            UUID userId,
            UUID clientId,
            String name,
            UUID stateId,
            ProjectTaxReliefRegime taxReliefRegime,
            BigDecimal bdiPercentage
    ) {
        this.id = validateId(id);
        this.userId = validateUserId(userId);
        this.clientId = clientId;
        this.name = validateName(name);
        this.stateId = validateStateId(stateId);
        this.taxReliefRegime = validateTaxReliefRegime(taxReliefRegime);
        this.bdiPercentage = validateBdiPercentage(bdiPercentage);
    }

    public static Project create(
            UUID userId,
            UUID clientId,
            String name,
            UUID stateId,
            ProjectTaxReliefRegime taxReliefRegime,
            BigDecimal bdiPercentage
    ) {
        return new Project(
                UUID.randomUUID(),
                userId,
                clientId,
                name,
                stateId,
                taxReliefRegime,
                bdiPercentage
        );
    }

    public static Project restore(
            UUID id,
            UUID userId,
            UUID clientId,
            String name,
            UUID stateId,
            ProjectTaxReliefRegime taxReliefRegime,
            BigDecimal bdiPercentage
    ) {
        return new Project(
                id,
                userId,
                clientId,
                name,
                stateId,
                taxReliefRegime,
                bdiPercentage
        );
    }

    public void update(
            UUID clientId,
            String name,
            UUID stateId,
            ProjectTaxReliefRegime taxReliefRegime,
            BigDecimal bdiPercentage
    ) {
        this.clientId = clientId;
        this.name = validateName(name);
        this.stateId = validateStateId(stateId);
        this.taxReliefRegime = validateTaxReliefRegime(taxReliefRegime);
        this.bdiPercentage = validateBdiPercentage(bdiPercentage);
    }

    private static UUID validateId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Project id is required");
        }

        return id;
    }

    private static UUID validateUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }

        return userId;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }

        return name.trim();
    }

    private static UUID validateStateId(UUID stateId) {
        if (stateId == null) {
            throw new IllegalArgumentException("State id is required");
        }

        return stateId;
    }

    private static ProjectTaxReliefRegime validateTaxReliefRegime(
            ProjectTaxReliefRegime taxReliefRegime
    ) {
        if (taxReliefRegime == null) {
            throw new IllegalArgumentException(
                    "Tax relief regime is required"
            );
        }

        return taxReliefRegime;
    }

    private static BigDecimal validateBdiPercentage(
            BigDecimal bdiPercentage
    ) {
        if (bdiPercentage == null) {
            return BigDecimal.ZERO;
        }

        if (bdiPercentage.signum() < 0) {
            throw new IllegalArgumentException(
                    "BDI percentage cannot be negative"
            );
        }

        return bdiPercentage;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }

    public UUID getStateId() {
        return stateId;
    }

    public ProjectTaxReliefRegime getTaxReliefRegime() {
        return taxReliefRegime;
    }

    public BigDecimal getBdiPercentage() {
        return bdiPercentage;
    }
}