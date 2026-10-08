CREATE TABLE tb_projects (
                             id UUID PRIMARY KEY,
                             user_id UUID NOT NULL,
                             client_id UUID,
                             name VARCHAR(150) NOT NULL,
                             state_id UUID NOT NULL,
                             tax_relief_regime VARCHAR(30) NOT NULL,
                             bdi_percentage NUMERIC(10, 4) NOT NULL DEFAULT 0,

                             CONSTRAINT fk_projects_user
                                 FOREIGN KEY (user_id)
                                     REFERENCES tb_users(id),

                             CONSTRAINT fk_projects_client
                                 FOREIGN KEY (client_id)
                                     REFERENCES tb_clients(id),

                             CONSTRAINT fk_projects_state
                                 FOREIGN KEY (state_id)
                                     REFERENCES tb_states(id),

                             CONSTRAINT ck_projects_bdi_percentage
                                 CHECK (bdi_percentage >= 0)
);

CREATE INDEX idx_projects_user_id
    ON tb_projects(user_id);

CREATE INDEX idx_projects_client_id
    ON tb_projects(client_id);

CREATE INDEX idx_projects_state_id
    ON tb_projects(state_id);