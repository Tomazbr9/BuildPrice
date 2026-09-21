package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.command.CreateSinapiTableVersionCommand;
import com.tomazbr9.buildprice.catalog.application.exception.SinapiTableVersionAlreadyExistsException;
import com.tomazbr9.buildprice.catalog.application.exception.StateNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.in.CreateSinapiTableVersionUseCase;
import com.tomazbr9.buildprice.catalog.application.port.out.SinapiTableVersionRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.StateRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.SinapiTableVersion;
import com.tomazbr9.buildprice.catalog.domain.entity.State;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class CreateSinapiTableVersionUseCaseImpl implements CreateSinapiTableVersionUseCase {

    private StateRepository stateRepository;
    private SinapiTableVersionRepository sinapiTableVersionRepository;


    public CreateSinapiTableVersionUseCaseImpl(
            StateRepository stateRepository,
            SinapiTableVersionRepository sinapiTableVersionRepository
    ){
        this.stateRepository = stateRepository;
        this.sinapiTableVersionRepository = sinapiTableVersionRepository;
    }


    @Override
    @Transactional
    public UUID execute(CreateSinapiTableVersionCommand command) {

        State state = stateRepository.findById(command.stateId())
                .orElseThrow(StateNotFoundException::new);

        boolean exists = sinapiTableVersionRepository.existsByStateIdAndReferenceMonthAndTaxReliefRegime(
                state.getId(),
                command.referenceMonth(),
                command.taxReliefRegime()
        );

        if (exists){
            throw new SinapiTableVersionAlreadyExistsException();
        }

        SinapiTableVersion version = SinapiTableVersion.create(
                state.getId(),
                command.referenceMonth(),
                command.taxReliefRegime(),
                command.publicationDate()
        );

        SinapiTableVersion saved = sinapiTableVersionRepository.save(version);

        return version.getId();
    }
}
