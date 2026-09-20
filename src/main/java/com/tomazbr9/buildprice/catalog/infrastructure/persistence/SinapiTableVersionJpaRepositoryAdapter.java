package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.port.out.SinapiTableVersionRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.SinapiTableVersion;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.SinapiTableVersionJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.StateJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.mapper.SinapiTableVersionMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SinapiTableVersionJpaRepositoryAdapter
        implements SinapiTableVersionRepository {

    private final SinapiTableVersionJpaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Optional<SinapiTableVersion> findById(UUID id) {

        return repository
                .findById(id)
                .map(SinapiTableVersionMapper::toEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStateIdAndReferenceMonthAndTaxReliefRegime(
            UUID stateId,
            YearMonth referenceMonth,
            TaxReliefRegime taxReliefRegime
    ) {

        return repository
                .existsByState_IdAndReferenceMonthAndTaxReliefRegime(
                        stateId,
                        referenceMonth,
                        taxReliefRegime
                );
    }

    @Override
    @Transactional
    public SinapiTableVersion save(
            SinapiTableVersion version
    ) {

        StateJpaEntity state =
                entityManager.getReference(
                        StateJpaEntity.class,
                        version.getStateId()
                );

        SinapiTableVersionJpaEntity entity =
                SinapiTableVersionMapper.toJpaEntity(
                        version,
                        state
                );

        entityManager.persist(entity);

        return SinapiTableVersionMapper.toEntity(entity);
    }
}