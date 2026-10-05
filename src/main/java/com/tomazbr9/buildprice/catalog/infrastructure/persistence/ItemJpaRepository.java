package com.tomazbr9.buildprice.catalog.infrastructure.persistence;

import com.tomazbr9.buildprice.catalog.infrastructure.entity.ItemJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemJpaRepository
        extends JpaRepository<ItemJpaEntity, UUID> {

    List<ItemJpaEntity> findByVersion_Id(
            UUID sinapiTableVersionId
    );

    Optional<ItemJpaEntity> findByVersion_IdAndCode(
            UUID sinapiTableVersionId,
            String code
    );

    @Query(
            value = """
                    SELECT i.*
                    FROM tb_items i
                    WHERE i.version_table_id = :versionId
                      AND (
                            i.code LIKE CONCAT(:query, '%')
                            OR to_tsvector(
                                'portuguese',
                                immutable_unaccent(
                                    coalesce(i.description, '')
                                )
                            )
                            @@ plainto_tsquery(
                                'portuguese',
                                immutable_unaccent(:query)
                            )
                      )
                    ORDER BY
                        CASE
                            WHEN i.code = :query THEN 0
                            WHEN i.code LIKE CONCAT(:query, '%') THEN 1
                            ELSE 2
                        END,
                        i.code
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM tb_items i
                    WHERE i.version_table_id = :versionId
                      AND (
                            i.code LIKE CONCAT(:query, '%')
                            OR to_tsvector(
                                'portuguese',
                                immutable_unaccent(
                                    coalesce(i.description, '')
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
    Page<ItemJpaEntity> search(
            @Param("versionId")
            UUID versionId,

            @Param("query")
            String query,

            Pageable pageable
    );
}