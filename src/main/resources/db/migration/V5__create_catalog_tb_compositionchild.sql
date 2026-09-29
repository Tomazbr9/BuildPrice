CREATE TABLE tb_composition_children (
    id UUID PRIMARY KEY,
    composition_id UUID NOT NULL,
    child_composition_id UUID NOT NULL,
    coefficient NUMERIC(19, 8) NOT NULL,

    CONSTRAINT fk_composition_child_parent
        FOREIGN KEY (composition_id)
            REFERENCES tb_compositions(id),

    CONSTRAINT fk_composition_child_child
        FOREIGN KEY (child_composition_id)
            REFERENCES tb_compositions(id),

    CONSTRAINT uk_composition_child
        UNIQUE (
            composition_id,
            child_composition_id
        )
);

CREATE INDEX idx_composition_children_composition
    ON tb_composition_children(composition_id);

CREATE INDEX idx_composition_children_child
    ON tb_composition_children(child_composition_id);