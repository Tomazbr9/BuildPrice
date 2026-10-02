package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.command.ImportSinapiCommand;
import com.tomazbr9.buildprice.catalog.application.dto.*;
import com.tomazbr9.buildprice.catalog.application.exception.SinapiTableVersionAlreadyExistsException;
import com.tomazbr9.buildprice.catalog.application.exception.StateNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.in.ImportSinapiUseCase;
import com.tomazbr9.buildprice.catalog.application.port.out.*;
import com.tomazbr9.buildprice.catalog.domain.entity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImportSinapiUseCaseImpl
        implements ImportSinapiUseCase {

    private final StateRepository stateRepository;
    private final SinapiTableVersionRepository versionRepository;
    private final CompositionRepository compositionRepository;
    private final ItemRepository itemRepository;
    private final CompositionItemRepository compositionItemRepository;
    private final SinapiFileParser parser;
    private final SinapiImportValidator validator;
    private final CompositionChildRepository compositionChildRepository;

    public ImportSinapiUseCaseImpl(
            StateRepository stateRepository,
            SinapiTableVersionRepository versionRepository,
            CompositionRepository compositionRepository,
            ItemRepository itemRepository,
            CompositionItemRepository compositionItemRepository,
            SinapiFileParser parser,
            SinapiImportValidator validator,
            CompositionChildRepository compositionChildRepository
    ) {
        this.stateRepository = stateRepository;
        this.versionRepository = versionRepository;
        this.compositionRepository = compositionRepository;
        this.itemRepository = itemRepository;
        this.compositionItemRepository = compositionItemRepository;
        this.parser = parser;
        this.validator = validator;
        this.compositionChildRepository = compositionChildRepository;
    }

    @Override
    @Transactional
    public UUID execute(ImportSinapiCommand command) {

        State state =
                stateRepository
                        .findById(command.stateId())
                        .orElseThrow(
                                StateNotFoundException::new
                        );

        boolean alreadyExists =
                versionRepository
                        .existsByStateIdAndReferenceMonthAndTaxReliefRegime(
                                state.getId(),
                                command.referenceMonth(),
                                command.taxReliefRegime()
                        );

        if (alreadyExists) {
            throw new SinapiTableVersionAlreadyExistsException();
        }

        SinapiImportData importData =
                parser.parse(
                        command.file(),
                        state.getStateAbbreviation(),
                        command.taxReliefRegime()
                );

        validator.validate(importData);

        SinapiTableVersion version =
                SinapiTableVersion.create(
                        state.getId(),
                        command.referenceMonth(),
                        command.taxReliefRegime(),
                        command.publicationDate()
                );

        SinapiTableVersion savedVersion =
                versionRepository.save(version);

        List<Composition> compositions =
                importData.compositions()
                        .stream()
                        .map(data ->
                                Composition.create(
                                        savedVersion.getId(),
                                        data.code(),
                                        data.description(),
                                        data.unit(),
                                        data.unitCost()
                                )
                        )
                        .toList();

        List<Item> items =
                importData.items()
                        .stream()
                        .map(data ->
                                Item.create(
                                        savedVersion.getId(),
                                        data.code(),
                                        data.description(),
                                        data.unit(),
                                        data.unitPrice()
                                )
                        )
                        .toList();

        List<Composition> savedCompositions =
                compositionRepository.saveAll(
                        compositions
                );

        List<Item> savedItems =
                itemRepository.saveAll(
                        items
                );

        Map<String, Composition> compositionsByCode =
                savedCompositions
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Composition::getCode,
                                        Function.identity()
                                )
                        );

        Map<String, Item> itemsByCode =
                savedItems
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Item::getCode,
                                        Function.identity()
                                )
                        );

        List<CompositionItem> relations =
                importData.compositionItems()
                        .stream()
                        .map(data -> {

                            Composition composition =
                                    compositionsByCode.get(
                                            data.compositionCode()
                                    );

                            Item item =
                                    itemsByCode.get(
                                            data.itemCode()
                                    );

                            return CompositionItem.create(
                                    composition.getId(),
                                    item.getId(),
                                    data.coefficient()
                            );
                        })
                        .toList();

        compositionItemRepository.saveAll(
                relations
        );

        List<CompositionChild> compositionChildren =
                importData.compositionChildren()
                        .stream()
                        .map(data -> {

                            Composition composition =
                                    compositionsByCode.get(
                                            data.compositionCode()
                                    );

                            Composition childComposition =
                                    compositionsByCode.get(
                                            data.childCompositionCode()
                                    );

                            return CompositionChild.create(
                                    composition.getId(),
                                    childComposition.getId(),
                                    data.coefficient()
                            );
                        })
                        .toList();

        compositionChildRepository.saveAll(
                compositionChildren
        );

        return savedVersion.getId();
    }
}