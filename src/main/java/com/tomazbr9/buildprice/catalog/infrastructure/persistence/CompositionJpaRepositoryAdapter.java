package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.port.out.CompositionRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.SinapiTableVersionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.mapper.CompositionMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CompositionJpaRepositoryAdapter
        implements CompositionRepository {

    private final CompositionJpaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Optional<Composition> findById(UUID id) {
        return repository
                .findById(id)
                .map(CompositionMapper::toEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Composition> findBySinapiTableVersionId(
            UUID sinapiTableVersionId
    ) {
        return repository
                .findByVersion_Id(sinapiTableVersionId)
                .stream()
                .map(CompositionMapper::toEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Composition> findBySinapiTableVersionIdAndCode(
            UUID sinapiTableVersionId,
            String code
    ) {
        return repository
                .findByVersion_IdAndCode(
                        sinapiTableVersionId,
                        code
                )
                .map(CompositionMapper::toEntity);
    }

    @Override
    @Transactional
    public Composition save(Composition composition) {

        SinapiTableVersionJpaEntity version =
                entityManager.getReference(
                        SinapiTableVersionJpaEntity.class,
                        composition.getSinapiTableVersionId()
                );

        CompositionJpaEntity entity =
                CompositionMapper.toJpaEntity(
                        composition,
                        version
                );

        entityManager.persist(entity);

        return CompositionMapper.toEntity(entity);
    }
}