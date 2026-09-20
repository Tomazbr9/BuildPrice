package com.tomazbr9.buildprice.catalog.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "tb_states",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_state_state_abbreviation",
                        columnNames = "state_abbreviation"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 2, name = "state_abbreviation")
    private String stateAbbreviation;

    @Column(nullable = false, length = 100)
    private String name;
}
