package com.tomazbr9.buildprice.catalog.infrastructure.convert;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.YearMonth;

@Converter(autoApply = false)
public class YearMonthAttributeConvert
        implements AttributeConverter<YearMonth, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(
            YearMonth yearMonth
    ) {

        if (yearMonth == null) {
            return null;
        }

        return yearMonth.atDay(1);
    }

    @Override
    public YearMonth convertToEntityAttribute(
            LocalDate date
    ) {

        if (date == null) {
            return null;
        }

        return YearMonth.from(date);
    }
}