package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.api.SinapiTableVersionExistsQuery;
import com.tomazbr9.buildprice.catalog.application.port.out.SinapiTableVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SinapiTableVersionExistsQueryImpl
        implements SinapiTableVersionExistsQuery {

    private final SinapiTableVersionRepository repository;

    public SinapiTableVersionExistsQueryImpl(
            SinapiTableVersionRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID versionId) {

        return repository
                .findById(versionId)
                .isPresent();
    }
}