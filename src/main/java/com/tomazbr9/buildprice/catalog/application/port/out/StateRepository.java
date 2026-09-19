package com.tomazbr9.buildprice.catalog.application.port.out;

import com.tomazbr9.buildprice.catalog.domain.entity.State;

import java.util.Optional;
import java.util.UUID;

public interface StateRepository {

    Optional<State> findById(UUID id);

    Optional<State> findByStateAbbreviation(String stateAbbreviation);

    State save(State state);
}
