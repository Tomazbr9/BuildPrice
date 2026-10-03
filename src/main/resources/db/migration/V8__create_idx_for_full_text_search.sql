CREATE INDEX idx_compositions_description_fts
    ON tb_compositions
        USING GIN (
                   to_tsvector('portuguese', coalesce(description, ''))
            );
