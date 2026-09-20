package com.tomazbr9.buildprice.catalog.infrastructure.mapper;

import com.tomazbr9.buildprice.catalog.domain.entity.State;
import com.tomazbr9.buildprice.catalog.infrastructure.entity.StateJpaEntity;

public final class StateMapper {

    private StateMapper(){

    }

    public static StateJpaEntity toJpaEntity(State state){
        return StateJpaEntity.builder()
                .id(state.getId())
                .stateAbbreviation(state.getStateAbbreviation())
                .name(state.getName())
                .build();
    }

    public static State toEntity(StateJpaEntity state){
        return State.restore(
                state.getId(),
                state.getStateAbbreviation(),
                state.getName()
        );
    }
}
