package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.application.port.out.StateRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.State;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.StateJpaEntity;
import com.tomazbr9.buildprice.catalog.infrastructure.mapper.StateMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StateJpaRepositoryAdapter
        implements StateRepository {

    private final StateJpaRepository stateJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Optional<State> findById(UUID id) {

        return stateJpaRepository
                .findById(id)
                .map(StateMapper::toEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<State> findByStateAbbreviation(
            String stateAbbreviation
    ) {

        return stateJpaRepository
                .findByStateAbbreviation(
                        stateAbbreviation
                                .trim()
                                .toUpperCase()
                )
                .map(StateMapper::toEntity);
    }

    @Override
    @Transactional
    public State save(State state) {

        StateJpaEntity entity =
                StateMapper.toJpaEntity(state);

        entityManager.persist(entity);

        return StateMapper.toEntity(entity);
    }
}