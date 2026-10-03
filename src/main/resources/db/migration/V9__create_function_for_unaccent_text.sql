CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE OR REPLACE FUNCTION immutable_unaccent(text)
    RETURNS text
    LANGUAGE sql
    IMMUTABLE
    PARALLEL SAFE
    STRICT
AS $$
SELECT unaccent('unaccent', $1)
$$;

DROP INDEX IF EXISTS idx_compositions_description_fts;

CREATE INDEX idx_compositions_description_fts
    ON tb_compositions
        USING GIN (
                   to_tsvector(
                           'portuguese',
                           immutable_unaccent(
                                   coalesce(description, '')
                           )
                   )
            );