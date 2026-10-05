package com.tomazbr9.buildprice.catalog.application.usecase;

import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionChildDetailResult;
import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionDetailResult;
import com.tomazbr9.buildprice.catalog.application.dto.composition.CompositionItemDetailResult;
import com.tomazbr9.buildprice.catalog.application.exception.CompositionNotFoundException;
import com.tomazbr9.buildprice.catalog.application.port.in.get.GetCompositionDetailUseCase;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionChildRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionItemRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.CompositionRepository;
import com.tomazbr9.buildprice.catalog.application.port.out.ItemRepository;
import com.tomazbr9.buildprice.catalog.domain.entity.Composition;
import com.tomazbr9.buildprice.catalog.domain.entity.CompositionChild;
import com.tomazbr9.buildprice.catalog.domain.entity.CompositionItem;
import com.tomazbr9.buildprice.catalog.domain.entity.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetCompositionDetailUseCaseImpl implements GetCompositionDetailUseCase {

    private final CompositionRepository compositionRepository;
    private final CompositionItemRepository compositionItemRepository;
    private final CompositionChildRepository compositionChildRepository;
    private final ItemRepository itemRepository;

    public GetCompositionDetailUseCaseImpl(
            CompositionRepository compositionRepository,
            CompositionItemRepository compositionItemRepository,
            CompositionChildRepository compositionChildRepository,
            ItemRepository itemRepository
    ) {
        this.compositionRepository = compositionRepository;
        this.compositionItemRepository = compositionItemRepository;
        this.compositionChildRepository = compositionChildRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CompositionDetailResult execute(
            UUID versionId,
            String code
    ) {

        Composition composition =
                compositionRepository
                        .findBySinapiTableVersionIdAndCode(
                                versionId,
                                code
                        )
                        .orElseThrow(
                                () -> new CompositionNotFoundException(
                                        code
                                )
                        );

        List<CompositionItemDetailResult> items =
                buildItems(
                        composition.getId()
                );

        List<CompositionChildDetailResult> childCompositions =
                buildChildCompositions(
                        composition.getId()
                );

        return new CompositionDetailResult(
                composition.getId(),
                composition.getSinapiTableVersionId(),
                composition.getCode(),
                composition.getDescription(),
                composition.getUnit(),
                composition.getUnitCost(),
                items,
                childCompositions
        );
    }

    private List<CompositionItemDetailResult> buildItems(
            UUID compositionId
    ) {

        List<CompositionItem> relations =
                compositionItemRepository
                        .findByCompositionId(
                                compositionId
                        );

        return relations
                .stream()
                .map(this::toItemDetail)
                .toList();
    }

    private CompositionItemDetailResult toItemDetail(
            CompositionItem relation
    ) {

        Item item =
                itemRepository
                        .findById(
                                relation.getItemId()
                        )
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Insumo da composição não encontrado: "
                                                + relation.getItemId()
                                )
                        );

        return new CompositionItemDetailResult(
                item.getId(),
                item.getCode(),
                item.getDescription(),
                item.getUnit(),
                relation.getCoefficient(),
                item.getUnitPrice()
        );
    }

    private List<CompositionChildDetailResult> buildChildCompositions(
            UUID compositionId
    ) {

        List<CompositionChild> relations =
                compositionChildRepository
                        .findByCompositionId(
                                compositionId
                        );

        return relations
                .stream()
                .map(this::toChildDetail)
                .toList();
    }

    private CompositionChildDetailResult toChildDetail(
            CompositionChild relation
    ) {

        Composition child =
                compositionRepository
                        .findById(
                                relation.getChildCompositionId()
                        )
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Subcomposição não encontrada: "
                                                + relation.getChildCompositionId()
                                )
                        );

        return new CompositionChildDetailResult(
                child.getId(),
                child.getCode(),
                child.getDescription(),
                child.getUnit(),
                relation.getCoefficient(),
                child.getUnitCost()
        );
    }
}