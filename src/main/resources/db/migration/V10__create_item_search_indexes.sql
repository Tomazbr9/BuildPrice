CREATE INDEX idx_items_version_code
    ON tb_items (
                 version_table_id,
                 code
        );

CREATE INDEX idx_items_description_fts
    ON tb_items
        USING GIN (
                   to_tsvector(
                           'portuguese',
                           immutable_unaccent(
                                   coalesce(description, '')
                           )
                   )
            );