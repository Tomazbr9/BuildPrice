package com.tomazbr9.buildprice.catalog.application.service;

import com.tomazbr9.buildprice.catalog.application.dto.*;
import com.tomazbr9.buildprice.catalog.application.exception.InvalidSinapiImportDataException;
import com.tomazbr9.buildprice.catalog.application.port.out.SinapiImportValidator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class SinapiImportValidatorImpl
        implements SinapiImportValidator {

    @Override
    public void validate(SinapiImportData data) {

        List<String> errors = new ArrayList<>();

        if (data == null) {
            throw new InvalidSinapiImportDataException(
                    List.of("Dados da importação não podem ser nulos")
            );
        }

        validateCompositions(
                data.compositions(),
                errors
        );

        validateItems(
                data.items(),
                errors
        );

        validateCompositionItems(
                data.compositionItems(),
                data.compositions(),
                data.items(),
                errors
        );

        if (!errors.isEmpty()) {
            throw new InvalidSinapiImportDataException(
                    errors
            );
        }
    }

    private void validateCompositions(
            List<ImportedCompositionData> compositions,
            List<String> errors
    ) {

        if (compositions == null || compositions.isEmpty()) {
            errors.add(
                    "Nenhuma composição encontrada"
            );
            return;
        }

        Set<String> codes = new HashSet<>();

        for (int i = 0; i < compositions.size(); i++) {

            ImportedCompositionData composition =
                    compositions.get(i);

            if (composition == null) {
                errors.add(
                        "Composição na posição " + i
                                + " é nula"
                );
                continue;
            }

            if (isBlank(composition.code())) {
                errors.add(
                        "Composição na posição " + i
                                + " está sem código"
                );
            } else {

                String code =
                        composition.code().trim();

                if (!codes.add(code)) {
                    errors.add(
                            "Código de composição duplicado: "
                                    + code
                    );
                }
            }

            if (isBlank(composition.description())) {
                errors.add(
                        "Composição "
                                + safeCode(composition.code())
                                + " está sem descrição"
                );
            }

            if (isBlank(composition.unit())) {
                errors.add(
                        "Composição "
                                + safeCode(composition.code())
                                + " está sem unidade"
                );
            }
        }
    }

    private void validateItems(
            List<ImportedItemData> items,
            List<String> errors
    ) {

        if (items == null || items.isEmpty()) {
            errors.add(
                    "Nenhum insumo encontrado"
            );
            return;
        }

        Set<String> codes = new HashSet<>();

        for (int i = 0; i < items.size(); i++) {

            ImportedItemData item =
                    items.get(i);

            if (item == null) {
                errors.add(
                        "Insumo na posição " + i
                                + " é nulo"
                );
                continue;
            }

            if (isBlank(item.code())) {
                errors.add(
                        "Insumo na posição " + i
                                + " está sem código"
                );
            } else {

                String code =
                        item.code().trim();

                if (!codes.add(code)) {
                    errors.add(
                            "Código de insumo duplicado: "
                                    + code
                    );
                }
            }

            if (isBlank(item.description())) {
                errors.add(
                        "Insumo "
                                + safeCode(item.code())
                                + " está sem descrição"
                );
            }

            BigDecimal unitPrice =
                    item.unitPrice();

            if (unitPrice == null) {
                errors.add(
                        "Insumo "
                                + safeCode(item.code())
                                + " está sem preço unitário"
                );

            } else if (unitPrice.signum() < 0) {

                errors.add(
                        "Insumo "
                                + safeCode(item.code())
                                + " possui preço unitário negativo"
                );
            }
        }
    }

    private void validateCompositionItems(
            List<ImportedCompositionItemData> relations,
            List<ImportedCompositionData> compositions,
            List<ImportedItemData> items,
            List<String> errors
    ) {

        if (relations == null) {
            errors.add(
                    "Relações composição-insumo não podem ser nulas"
            );
            return;
        }

        Set<String> compositionCodes =
                extractCompositionCodes(compositions);

        Set<String> itemCodes =
                extractItemCodes(items);

        Set<String> relationsFound =
                new HashSet<>();

        for (int i = 0; i < relations.size(); i++) {

            ImportedCompositionItemData relation =
                    relations.get(i);

            if (relation == null) {
                errors.add(
                        "Relação composição-insumo na posição "
                                + i
                                + " é nula"
                );
                continue;
            }

            String compositionCode =
                    normalize(relation.compositionCode());

            String itemCode =
                    normalize(relation.itemCode());

            if (compositionCode == null) {
                errors.add(
                        "Relação na posição " + i
                                + " está sem código de composição"
                );
            } else if (!compositionCodes.contains(
                    compositionCode
            )) {
                errors.add(
                        "Composição referenciada não existe: "
                                + compositionCode
                );
            }

            if (itemCode == null) {
                errors.add(
                        "Relação na posição " + i
                                + " está sem código de insumo"
                );
            } else if (!itemCodes.contains(itemCode)) {
                errors.add(
                        "Insumo referenciado não existe: "
                                + itemCode
                );
            }

            BigDecimal coefficient =
                    relation.coefficient();

            if (coefficient == null) {
                errors.add(
                        "Coeficiente não informado para composição "
                                + safeCode(compositionCode)
                                + " e insumo "
                                + safeCode(itemCode)
                );

            } else if (coefficient.signum() <= 0) {

                errors.add(
                        "Coeficiente deve ser maior que zero para composição "
                                + safeCode(compositionCode)
                                + " e insumo "
                                + safeCode(itemCode)
                );
            }

            if (compositionCode != null
                    && itemCode != null) {

                String relationKey =
                        compositionCode + "::" + itemCode;

                if (!relationsFound.add(relationKey)) {
                    errors.add(
                            "Relação duplicada entre composição "
                                    + compositionCode
                                    + " e insumo "
                                    + itemCode
                    );
                }
            }
        }
    }

    private Set<String> extractCompositionCodes(
            List<ImportedCompositionData> compositions
    ) {

        if (compositions == null) {
            return Set.of();
        }

        Set<String> result =
                new HashSet<>();

        for (ImportedCompositionData composition
                : compositions) {

            if (composition != null) {

                String code =
                        normalize(composition.code());

                if (code != null) {
                    result.add(code);
                }
            }
        }

        return result;
    }

    private Set<String> extractItemCodes(
            List<ImportedItemData> items
    ) {

        if (items == null) {
            return Set.of();
        }

        Set<String> result =
                new HashSet<>();

        for (ImportedItemData item : items) {

            if (item != null) {

                String code =
                        normalize(item.code());

                if (code != null) {
                    result.add(code);
                }
            }
        }

        return result;
    }

    private String normalize(String value) {

        if (isBlank(value)) {
            return null;
        }

        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null
                || value.isBlank();
    }

    private String safeCode(String value) {

        if (value == null || value.isBlank()) {
            return "<sem código>";
        }

        return value.trim();
    }
}