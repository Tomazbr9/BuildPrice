package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.dto.PageResult;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.SinapiTableVersionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.mapper.CompositionMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
                .map(CompositionMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Composition> findBySinapiTableVersionId(
            UUID sinapiTableVersionId
    ) {
        return repository
                .findByVersion_Id(sinapiTableVersionId)
                .stream()
                .map(CompositionMapper::toDomain)
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
                .map(CompositionMapper::toDomain);
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

        return CompositionMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public List<Composition> saveAll(
            List<Composition> compositions
    ) {

        List<Composition> saved =
                new ArrayList<>(compositions.size());

        for (Composition composition : compositions) {

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

            saved.add(
                    CompositionMapper.toDomain(entity)
            );
        }

        return saved;
    }

    @Override
    public PageResult<Composition> search(
            UUID versionId,
            String query,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        Page<CompositionJpaEntity> result =
                repository.search(
                        versionId,
                        query,
                        pageable
                );

        List<Composition> compositions =
                result.getContent()
                        .stream()
                        .map(CompositionMapper::toDomain)
                        .toList();

        return new PageResult<>(
                compositions,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Composition> findAllById(
            Iterable<UUID> ids
    ) {
        return repository
                .findAllById(ids)
                .stream()
                .map(CompositionMapper::toDomain)
                .toList();
    }
}