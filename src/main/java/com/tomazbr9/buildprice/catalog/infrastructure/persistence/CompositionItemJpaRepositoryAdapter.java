package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.port.out.CompositionItemRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CompositionItemJpaRepositoryAdapter
        implements CompositionItemRepository {

    private final CompositionItemJpaRepository compositionItemJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<CompositionItem> findByCompositionId(
            UUID compositionId
    ) {
        return compositionItemJpaRepository
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

    @Override
    @Transactional
    public List<CompositionItem> saveAll(
            List<CompositionItem> compositionsItems
    ) {

        List<CompositionItem> saved =
                new ArrayList<>(compositionsItems.size());

        for (CompositionItem compositionItem : compositionsItems) {

            CompositionJpaEntity composition = entityManager.getReference(
                    CompositionJpaEntity.class,
                    compositionItem.getCompositionId()
            );

            ItemJpaEntity item = entityManager.getReference(
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

            saved.add(
                    CompositionItemMapper.toEntity(entity)
            );
        }

        return saved;
    }
}