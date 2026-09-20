package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.infrastructure.entity.StateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StateJpaRepository extends JpaRepository<StateJpaEntity, UUID> {

    Optional<StateJpaEntity> findByStateAbbreviation(String stateAbbreviation);

}
