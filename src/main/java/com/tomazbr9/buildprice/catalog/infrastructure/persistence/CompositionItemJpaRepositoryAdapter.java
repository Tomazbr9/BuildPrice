package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.port.out.CompositionItemRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.CompositionItem;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionItemJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.ItemJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.mapper.CompositionItemMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CompositionItemJpaRepositoryAdapter
        implements CompositionItemRepository {

    private final CompositionItemJpaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<CompositionItem> findByCompositionId(
            UUID compositionId
    ) {
        return repository
                .findByComposition_Id(compositionId)
                .stream()
                .map(CompositionItemMapper::toEntity)
                .toList();
    }

    @Override
    @Transactional
    public CompositionItem save(
            CompositionItem compositionItem
    ) {

        CompositionJpaEntity composition =
                entityManager.getReference(
                        CompositionJpaEntity.class,
                        compositionItem.getCompositionId()
                );

        ItemJpaEntity item =
                entityManager.getReference(
                        ItemJpaEntity.class,
                        compositionItem.getItemId()
                );

        CompositionItemJpaEntity entity =
                CompositionItemMapper.toJpaEntity(
                        compositionItem,
                        composition,
                        item
                );

        entityManager.persist(entity);

        return CompositionItemMapper.toEntity(entity);
    }
}