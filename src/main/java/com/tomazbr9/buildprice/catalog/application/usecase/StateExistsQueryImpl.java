package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.api.StateExistsQuery;
import com.tomazbr9.buildprice.catalog.application.port.out.StateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StateExistsQueryImpl
        implements StateExistsQuery {

    private final StateRepository stateRepository;

    public StateExistsQueryImpl(
            StateRepository stateRepository
    ) {
        this.stateRepository = stateRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID stateId) {

        return stateRepository
                .findById(stateId)
                .isPresent();
    }
}