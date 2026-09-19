package com.tomazbr9.buildprice.catalog.domain.entity;

import java.util.UUID;

public class State {

    private UUID id;
    private String stateAbbreviation;
    private String name;

    private State(){

    }

    public static State create(String stateAbbreviation, String name){
        State state = new State();

        state.id = UUID.randomUUID();
        state.stateAbbreviation = stateAbbreviation.trim().toUpperCase();
        state.name = name;

        return state;
    }

    public static State restore(UUID id, String stateAbbreviation, String name){
        State state = new State();

        state.id = id;
        state.stateAbbreviation = stateAbbreviation;
        state.name = name;

        return state;
    }

    public UUID getId() {
        return id;
    }

    public String getStateAbbreviation() {
        return stateAbbreviation;
    }

    public String getName() {
        return name;
    }
}
