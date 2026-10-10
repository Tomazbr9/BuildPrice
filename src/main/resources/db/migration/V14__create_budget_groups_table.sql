CREATE TABLE tb_budget_groups (
                                  id UUID PRIMARY KEY,
                                  budget_id UUID NOT NULL,
                                  parent_group_id UUID,
                                  name VARCHAR(150) NOT NULL,
                                  sort_order INTEGER NOT NULL DEFAULT 0,

                                  CONSTRAINT fk_budget_groups_budget
                                      FOREIGN KEY (budget_id)
                                          REFERENCES tb_budgets(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_budget_groups_parent
                                      FOREIGN KEY (parent_group_id)
                                          REFERENCES tb_budget_groups(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT ck_budget_groups_sort_order
                                      CHECK (sort_order >= 0)
);

CREATE INDEX idx_budget_groups_budget_id
    ON tb_budget_groups(budget_id);

CREATE INDEX idx_budget_groups_parent_group_id
    ON tb_budget_groups(parent_group_id);