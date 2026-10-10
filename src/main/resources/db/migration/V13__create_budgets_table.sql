CREATE TABLE tb_budgets (
                            id UUID PRIMARY KEY,
                            project_id UUID NOT NULL,
                            sinapi_table_version_id UUID NOT NULL,
                            status VARCHAR(30) NOT NULL,
                            bdi_percentage NUMERIC(10, 4) NOT NULL DEFAULT 0,

                            CONSTRAINT fk_budgets_project
                                FOREIGN KEY (project_id)
                                    REFERENCES tb_projects(id),

                            CONSTRAINT fk_budgets_sinapi_version
                                FOREIGN KEY (sinapi_table_version_id)
                                    REFERENCES tb_sinapi_table_versions(id),

                            CONSTRAINT ck_budgets_bdi_percentage
                                CHECK (bdi_percentage >= 0)
);

CREATE INDEX idx_budgets_project_id
    ON tb_budgets(project_id);

CREATE INDEX idx_budgets_sinapi_version_id
    ON tb_budgets(sinapi_table_version_id);