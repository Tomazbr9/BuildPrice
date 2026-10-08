package com.tomazbr9.buildprice.projects.infrastructure.persistence;

import com.tomazbr9.buildprice.projects.infrastructure.entity.ProjectJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectJpaRepository
        extends JpaRepository<ProjectJpaEntity, UUID> {

    List<ProjectJpaEntity> findByUserId(UUID userId);
}