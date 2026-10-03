package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.infrastructure.entity.CompositionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompositionJpaRepository
        extends JpaRepository<CompositionJpaEntity, UUID> {

    List<CompositionJpaEntity> findByVersion_Id(
            UUID sinapiTableVersionId
    );

    Optional<CompositionJpaEntity> findByVersion_IdAndCode(
            UUID sinapiTableVersionId,
            String code
    );

    @Query(
            value = """
                SELECT c.*
                FROM tb_compositions c
                WHERE c.version_table_id = :versionId
                  AND (
                        c.code LIKE CONCAT(:query, '%')
                        OR to_tsvector(
                                'portuguese',
                                immutable_unaccent(
                                    coalesce(c.description, '')
                                )
                           )
                           @@ plainto_tsquery(
                                'portuguese',
                                immutable_unaccent(:query)
                           )
                  )
                ORDER BY
                    CASE
                        WHEN c.code = :query THEN 0
                        WHEN c.code LIKE CONCAT(:query, '%') THEN 1
                        ELSE 2
                    END,
                    c.code
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM tb_compositions c
                WHERE c.version_table_id = :versionId
                  AND (
                        c.code LIKE CONCAT(:query, '%')
                        OR to_tsvector(
                                'portuguese',
                                immutable_unaccent(
                                    coalesce(c.description, '')
                                )
                           )
                           @@ plainto_tsquery(
                                'portuguese',
                                immutable_unaccent(:query)
                           )
                  )
                """,
            nativeQuery = true
    )
    Page<CompositionJpaEntity> search(
            @Param("versionId") UUID versionId,
            @Param("query") String query,
            Pageable pageable
    );
}