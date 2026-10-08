package com.tomazbr9.buildprice.projects.infrastructure.persistence;

import com.tomazbr9.buildprice.projects.application.port.out.ProjectRepository;
import com.tomazbr9.buildprice.projects.domain.entity.Project;
import com.tomazbr9.buildprice.projects.infrastructure.entity.ProjectJpaEntity;
import com.tomazbr9.buildprice.projects.infrastructure.mapper.ProjectMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProjectJpaRepositoryAdapter
        implements ProjectRepository {

    private final ProjectJpaRepository repository;
    private final EntityManager entityManager;

    public ProjectJpaRepositoryAdapter(
            ProjectJpaRepository repository,
            EntityManager entityManager
    ) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    public Project save(Project project) {

        ProjectJpaEntity entity =
                ProjectMapper.toJpaEntity(project);

        if (!repository.existsById(project.getId())) {
            entityManager.persist(entity);
            return project;
        }

        ProjectJpaEntity saved =
                repository.save(entity);

        return ProjectMapper.toDomain(saved);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return repository
                .findById(id)
                .map(ProjectMapper::toDomain);
    }

    @Override
    public List<Project> findByUserId(UUID userId) {
        return repository
                .findByUserId(userId)
                .stream()
                .map(ProjectMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}