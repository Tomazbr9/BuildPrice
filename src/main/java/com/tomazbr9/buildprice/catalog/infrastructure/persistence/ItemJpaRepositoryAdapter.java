package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.port.out.ItemRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Item;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.ItemJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.SinapiTableVersionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.mapper.ItemMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ItemJpaRepositoryAdapter
        implements ItemRepository {

    private final ItemJpaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Optional<Item> findById(UUID id) {
        return repository
                .findById(id)
                .map(ItemMapper::toEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findBySinapiTableVersionId(
            UUID sinapiTableVersionId
    ) {
        return repository
                .findByVersion_Id(sinapiTableVersionId)
                .stream()
                .map(ItemMapper::toEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Item> findBySinapiTableVersionIdAndCode(
            UUID sinapiTableVersionId,
            String code
    ) {
        return repository
                .findByVersion_IdAndCode(
                        sinapiTableVersionId,
                        code
                )
                .map(ItemMapper::toEntity);
    }

    @Override
    @Transactional
    public Item save(Item item) {

        SinapiTableVersionJpaEntity version =
                entityManager.getReference(
                        SinapiTableVersionJpaEntity.class,
                        item.getSinapiTableVersionId()
                );

        ItemJpaEntity entity =
                ItemMapper.toJpaEntity(
                        item,
                        version
                );

        entityManager.persist(entity);

        return ItemMapper.toEntity(entity);
    }

    @Override
    @Transactional
    public List<Item> saveAll(
            List<Item> items
    ) {

        List<Item> saved =
                new ArrayList<>(items.size());

        for (Item item : items) {

            SinapiTableVersionJpaEntity version =
                    entityManager.getReference(
                            SinapiTableVersionJpaEntity.class,
                            item.getSinapiTableVersionId()
                    );

            ItemJpaEntity entity =
                    ItemMapper.toJpaEntity(
                            item,
                            version
                    );

            entityManager.persist(entity);

            saved.add(
                    ItemMapper.toEntity(entity)
            );
        }

        return saved;
    }
}