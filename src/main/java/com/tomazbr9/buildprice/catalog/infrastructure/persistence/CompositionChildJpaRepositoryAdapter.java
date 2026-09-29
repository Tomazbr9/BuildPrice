package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.port.out.CompositionChildRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.CompositionChild;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionChildJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.mapper.CompositionChildMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CompositionChildJpaRepositoryAdapter
        implements CompositionChildRepository {

    private final CompositionChildJpaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<CompositionChild> findByCompositionId(
            UUID compositionId
    ) {
        return repository
                .findByComposition_Id(compositionId)
                .stream()
                .map(CompositionChildMapper::toEntity)
                .toList();
    }

    @Override
    @Transactional
    public CompositionChild save(
            CompositionChild compositionChild
    ) {

        CompositionJpaEntity composition =
                entityManager.getReference(
                        CompositionJpaEntity.class,
                        compositionChild.getCompositionId()
                );

        CompositionJpaEntity childComposition =
                entityManager.getReference(
                        CompositionJpaEntity.class,
                        compositionChild.getChildCompositionId()
                );

        CompositionChildJpaEntity entity =
                CompositionChildMapper.toJpaEntity(
                        compositionChild,
                        composition,
                        childComposition
                );

        entityManager.persist(entity);

        return CompositionChildMapper.toEntity(entity);
    }

    @Override
    @Transactional
    public List<CompositionChild> saveAll(
            List<CompositionChild> compositionChildren
    ) {

        List<CompositionChild> saved =
                new ArrayList<>(compositionChildren.size());

        for (CompositionChild relation
                : compositionChildren) {

            CompositionJpaEntity composition =
                    entityManager.getReference(
                            CompositionJpaEntity.class,
                            relation.getCompositionId()
                    );

            CompositionJpaEntity childComposition =
                    entityManager.getReference(
                            CompositionJpaEntity.class,
                            relation.getChildCompositionId()
                    );

            CompositionChildJpaEntity entity =
                    CompositionChildMapper.toJpaEntity(
                            relation,
                            composition,
                            childComposition
                    );

            entityManager.persist(entity);

            saved.add(
                    CompositionChildMapper.toEntity(entity)
            );
        }

        return saved;
    }
}