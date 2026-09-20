CREATE TABLE tb_states (
    id UUID PRIMARY KEY ,
    state_abbreviation VARCHAR(2) NOT NULL,
    name VARCHAR(100) NOT NULL,

    CONSTRAINT uk_state_state_abbreviation UNIQUE (state_abbreviation)
);

CREATE TABLE tb_sinapi_table_versions (
    id UUID PRIMARY KEY,
    state_id UUID NOT NULL,
    reference_month DATE NOT NULL,
    tax_relief_regime VARCHAR(30) NOT NULL,
    publication_date DATE NOT NULL,

    CONSTRAINT fk_version_table_state
        FOREIGN KEY (state_id)
            REFERENCES tb_states(id),

    CONSTRAINT uk_sinapi_table_state_month_regime
        UNIQUE (state_id, reference_month, tax_relief_regime)
);

CREATE TABLE tb_compositions (
    id UUID PRIMARY KEY,
    version_table_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    unit VARCHAR(30) NOT NULL,

    CONSTRAINT fk_composition_version_table
        FOREIGN KEY (version_table_id)
            REFERENCES tb_sinapi_table_versions(id),

    CONSTRAINT uk_composition_version_code
        UNIQUE (version_table_id, code)
);

CREATE TABLE tb_items (
    id UUID PRIMARY KEY,
    version_table_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    unit_price NUMERIC(19, 6) NOT NULL,

    CONSTRAINT fk_item_version_table
        FOREIGN KEY (version_table_id)
            REFERENCES tb_sinapi_table_versions,

    CONSTRAINT uk_items_version_code
        UNIQUE (version_table_id, code)
);

CREATE TABLE tb_composition_items
(
    id UUID PRIMARY KEY,
    composition_id UUID NOT NULL,
    item_id UUID NOT NULL,
    coefficient NUMERIC(19, 8) NOT NULL,

    CONSTRAINT fk_composition_item_composition
        FOREIGN KEY (composition_id)
            REFERENCES tb_compositions (id),

    CONSTRAINT fk_composition_item_item
        FOREIGN KEY (item_id)
            REFERENCES tb_items (id),

    CONSTRAINT uk_composition_item
        UNIQUE (composition_id, item_id)
);

CREATE INDEX idx_sinapi_table_state ON tb_sinapi_table_versions(state_id);
CREATE INDEX idx_compositions_version_table ON tb_compositions(version_table_id);
CREATE INDEX idx_compositions_code ON tb_compositions(code);
CREATE INDEX idx_items_version_table ON tb_items(version_table_id);
CREATE INDEX idx_items_code ON tb_items(code);
CREATE INDEX idx_composition_item_item ON tb_composition_items(item_id);
CREATE INDEX idx_composition_item_composition ON tb_composition_items(composition_id);


