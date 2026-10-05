package com.tomazbr9.buildprice.catalog.infrastructure.importation;

import com.tomazbr9.buildprice.catalog.application.dto.import_sinapi.*;
import com.tomazbr9.buildprice.catalog.application.exception.InvalidSinapiFileException;
import com.tomazbr9.buildprice.catalog.application.port.out.SinapiFileParser;
import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SinapiExcelFileParser
        implements SinapiFileParser {

    private static final Pattern FORMULA_DISPLAY_CODE_PATTERN =
            Pattern.compile(",\\s*(\\d+)\\s*\\)$");

    private static final int HEADER_ROW_INDEX = 9;

    @Override
    public SinapiImportData parse(
            InputStream inputStream,
            String stateAbbreviation,
            TaxReliefRegime taxReliefRegime
    ) {

        if (inputStream == null) {
            throw new InvalidSinapiFileException(
                    "Arquivo SINAPI não informado"
            );
        }

        if (stateAbbreviation == null
                || stateAbbreviation.isBlank()) {

            throw new InvalidSinapiFileException(
                    "UF não informada"
            );
        }

        try (Workbook workbook =
                     new XSSFWorkbook(inputStream)) {

            String itemSheetName =
                    resolveItemSheet(
                            taxReliefRegime
                    );

            String compositionSheetName =
                    resolveCompositionSheet(
                            taxReliefRegime
                    );

            Sheet itemSheet =
                    getRequiredSheet(
                            workbook,
                            itemSheetName
                    );

            Sheet compositionSheet =
                    getRequiredSheet(
                            workbook,
                            compositionSheetName
                    );

            Sheet analyticalSheet =
                    getRequiredSheet(
                            workbook,
                            "Analítico"
                    );

            List<ImportedItemData> items =
                    parseItems(
                            itemSheet,
                            stateAbbreviation
                    );

            List<ImportedCompositionData> compositions =
                    parseCompositions(
                            compositionSheet,
                            stateAbbreviation
                    );

            AnalyticalRelations relations =
                    parseAnalyticalRelations(
                            analyticalSheet
                    );

            List<ImportedItemData> completedItems =
                    mergeItemsWithAnalyticalData(
                            items,
                            relations.analyticalItems()
                    );

            return new SinapiImportData(
                    compositions,
                    completedItems,
                    relations.compositionItems(),
                    relations.compositionChildren()
            );

        } catch (InvalidSinapiFileException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new InvalidSinapiFileException(
                    "Não foi possível processar o arquivo SINAPI",
                    exception
            );
        }


    }

    private String resolveItemSheet(
            TaxReliefRegime regime
    ) {

        if (regime == null) {
            throw new InvalidSinapiFileException(
                    "Regime de desoneração não informado"
            );
        }

        return switch (regime) {

            case NOT_EXEMPTED -> "ISD";

            case EXEMPTED -> "ICD";
        };
    }

    private String resolveCompositionSheet(
            TaxReliefRegime regime
    ) {

        if (regime == null) {
            throw new InvalidSinapiFileException(
                    "Regime de desoneração não informado"
            );
        }

        return switch (regime) {

            case NOT_EXEMPTED -> "CSD";

            case EXEMPTED -> "CCD";
        };
    }

    private Sheet getRequiredSheet(
            Workbook workbook,
            String sheetName
    ) {

        Sheet sheet =
                workbook.getSheet(sheetName);

        if (sheet == null) {
            throw new InvalidSinapiFileException(
                    "Aba obrigatória não encontrada: "
                            + sheetName
            );
        }

        return sheet;
    }

    private List<ImportedItemData> parseItems(
            Sheet sheet,
            String stateAbbreviation
    ) {

        Row header =
                sheet.getRow(
                        HEADER_ROW_INDEX
                );

        if (header == null) {
            throw new InvalidSinapiFileException(
                    "Cabeçalho de insumos não encontrado"
            );
        }

        int stateColumn =
                findColumnByValue(
                        header,
                        stateAbbreviation
                                .trim()
                                .toUpperCase()
                );

        List<ImportedItemData> items =
                new ArrayList<>();

        for (
                int rowIndex =
                HEADER_ROW_INDEX + 1;
                rowIndex <= sheet.getLastRowNum();
                rowIndex++
        ) {

            Row row =
                    sheet.getRow(rowIndex);

            if (row == null) {
                continue;
            }

            String code =
                    readCode(
                            row.getCell(1)
                    );

            if (code == null) {
                continue;
            }

            String description =
                    readString(
                            row.getCell(2)
                    );

            String unit =
                    readString(
                            row.getCell(3)
                    );

            BigDecimal price =
                    readBigDecimal(
                            row.getCell(stateColumn)
                    );

            if (price == null) {
                continue;
            }

            items.add(
                    new ImportedItemData(
                            code,
                            description,
                            unit,
                            price
                    )
            );
        }

        return items;
    }

    private List<ImportedCompositionData>
    parseCompositions(
            Sheet sheet,
            String stateAbbreviation
    ) {

        Row stateHeader =
                sheet.getRow(8);

        if (stateHeader == null) {
            throw new InvalidSinapiFileException(
                    "Cabeçalho de estados das composições não encontrado"
            );
        }

        int stateCostColumn =
                findColumnByValue(
                        stateHeader,
                        stateAbbreviation
                                .trim()
                                .toUpperCase()
                );

        List<ImportedCompositionData>
                compositions =
                new ArrayList<>();

        for (
                int rowIndex =
                HEADER_ROW_INDEX + 1;
                rowIndex <= sheet.getLastRowNum();
                rowIndex++
        ) {

            Row row =
                    sheet.getRow(rowIndex);

            if (row == null) {
                continue;
            }

            String code =
                    readCode(
                            row.getCell(1)
                    );

            if (code == null) {
                continue;
            }

            String description =
                    readString(
                            row.getCell(2)
                    );

            String unit =
                    readString(
                            row.getCell(3)
                    );

            BigDecimal unitCost =
                    readBigDecimal(
                            row.getCell(
                                    stateCostColumn
                            )
                    );

            if (unitCost == null) {
                continue;
            }

            compositions.add(
                    new ImportedCompositionData(
                            code,
                            description,
                            unit,
                            unitCost
                    )
            );
        }

        return compositions;
    }

    private int findColumnByValue(
            Row row,
            String expectedValue
    ) {

        for (
                int columnIndex =
                row.getFirstCellNum();
                columnIndex <
                        row.getLastCellNum();
                columnIndex++
        ) {

            Cell cell =
                    row.getCell(columnIndex);

            String value =
                    readString(cell);

            if (
                    value != null
                            && value
                            .trim()
                            .equalsIgnoreCase(
                                    expectedValue
                            )
            ) {

                return columnIndex;
            }
        }

        throw new InvalidSinapiFileException(
                "UF não encontrada no arquivo SINAPI: "
                        + expectedValue
        );
    }

    private String readCode(Cell cell) {

        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {

            case STRING -> {

                String value =
                        cell.getStringCellValue()
                                .trim();

                yield value.isBlank()
                        ? null
                        : value;
            }

            case NUMERIC -> {

                double value =
                        cell.getNumericCellValue();

                long integerValue =
                        (long) value;

                yield Long.toString(
                        integerValue
                );
            }

            case FORMULA ->
                    readCodeFromFormula(cell);

            default -> null;
        };
    }

    private String readCodeFromFormula(Cell cell) {

        String formula = cell.getCellFormula();

        Matcher matcher =
                FORMULA_DISPLAY_CODE_PATTERN.matcher(formula);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return switch (cell.getCachedFormulaResultType()) {

            case STRING -> {
                String value =
                        cell.getStringCellValue().trim();

                yield value.isBlank()
                        ? null
                        : value;
            }

            case NUMERIC -> {
                long value =
                        (long) cell.getNumericCellValue();

                yield value == 0
                        ? null
                        : Long.toString(value);
            }

            default -> null;
        };
    }

    private String readString(Cell cell) {

        if (cell == null) {
            return null;
        }

        DataFormatter formatter =
                new DataFormatter();

        String value =
                formatter
                        .formatCellValue(cell)
                        .trim();

        return value.isBlank()
                ? null
                : value;
    }

    private BigDecimal readBigDecimal(
            Cell cell
    ) {

        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {

            case NUMERIC ->
                    BigDecimal.valueOf(
                            cell.getNumericCellValue()
                    );

            case STRING -> {

                String value =
                        cell.getStringCellValue()
                                .trim();

                if (value.isBlank()
                        || value.equals("-")) {

                    yield null;
                }

                value =
                        value.replace(",", ".");

                yield new BigDecimal(value);
            }

            case FORMULA ->
                    readBigDecimalFromFormula(cell);

            default -> null;
        };
    }

    private BigDecimal readBigDecimalFromFormula(
            Cell cell
    ) {

        return switch (
                cell.getCachedFormulaResultType()
                ) {

            case NUMERIC ->
                    BigDecimal.valueOf(
                            cell.getNumericCellValue()
                    );

            case STRING -> {

                String value =
                        cell.getStringCellValue()
                                .trim();

                if (value.isBlank()
                        || value.equals("-")) {

                    yield null;
                }

                yield new BigDecimal(
                        value.replace(",", ".")
                );
            }

            default -> null;
        };
    }

    private AnalyticalRelations parseAnalyticalRelations(
            Sheet sheet
    ) {

        List<ImportedCompositionItemData> compositionItems =
                new ArrayList<>();

        List<ImportedCompositionChildData> compositionChildren =
                new ArrayList<>();

        List<ImportedItemData> analyticalItems =
                new ArrayList<>();

        for (
                int rowIndex = HEADER_ROW_INDEX + 1;
                rowIndex <= sheet.getLastRowNum();
                rowIndex++
        ) {

            Row row =
                    sheet.getRow(rowIndex);

            if (row == null) {
                continue;
            }

            String itemType =
                    readString(
                            row.getCell(2)
                    );

            /*
             * Linhas sem Tipo Item representam apenas
             * o cabeçalho/início de uma nova composição.
             */
            if (itemType == null) {
                continue;
            }

            String compositionCode =
                    readCode(
                            row.getCell(1)
                    );

            String referencedCode =
                    readCode(
                            row.getCell(3)
                    );

            BigDecimal coefficient =
                    readBigDecimal(
                            row.getCell(6)
                    );

            if (compositionCode == null
                    || referencedCode == null) {

                continue;
            }

            switch (itemType.trim().toUpperCase()) {

                case "INSUMO" -> {

                    compositionItems.add(
                            new ImportedCompositionItemData(
                                    compositionCode,
                                    referencedCode,
                                    coefficient
                            )
                    );

                    String description =
                            readString(
                                    row.getCell(4)
                            );

                    String unit =
                            readString(
                                    row.getCell(5)
                            );

                    analyticalItems.add(
                            new ImportedItemData(
                                    referencedCode,
                                    description,
                                    unit,
                                    null
                            )
                    );
                }

                case "COMPOSICAO" ->
                        compositionChildren.add(
                                new ImportedCompositionChildData(
                                        compositionCode,
                                        referencedCode,
                                        coefficient
                                )
                        );

                default ->
                        throw new InvalidSinapiFileException(
                                "Tipo de item desconhecido na aba Analítico: "
                                        + itemType
                                        + " na linha "
                                        + (rowIndex + 1)
                        );
            }
        }

        return new AnalyticalRelations(
                compositionItems,
                compositionChildren,
                analyticalItems
        );
    }

    private record AnalyticalRelations(
            List<ImportedCompositionItemData> compositionItems,
            List<ImportedCompositionChildData> compositionChildren,
            List<ImportedItemData> analyticalItems
    ) {
    }

    private List<ImportedItemData> mergeItemsWithAnalyticalData(
            List<ImportedItemData> pricedItems,
            List<ImportedItemData> analyticalItems
    ) {

        Map<String, ImportedItemData> itemsByCode =
                new LinkedHashMap<>();

        for (ImportedItemData item : pricedItems) {
            itemsByCode.put(
                    item.code(),
                    item
            );
        }

        for (ImportedItemData item : analyticalItems) {

            itemsByCode.putIfAbsent(
                    item.code(),
                    item
            );
        }

        return new ArrayList<>(
                itemsByCode.values()
        );
    }

}