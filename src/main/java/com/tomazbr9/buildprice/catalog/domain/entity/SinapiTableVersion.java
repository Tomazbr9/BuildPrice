package com.tomazbr9.buildprice.catalog.domain.entity;

import com.tomazbr9.buildprice.catalog.domain.enums.TaxReliefRegime;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

public class SinapiTableVersion {

    private UUID id;
    private UUID stateId;
    private YearMonth referenceMonth;
    private TaxReliefRegime taxReliefRegime;
    private LocalDate publicationDate;

    private SinapiTableVersion(){

    }

    public static SinapiTableVersion create(
            UUID state,
            YearMonth referenceMonth,
            TaxReliefRegime taxReliefRegime,
            LocalDate publicationDate
    ){
        SinapiTableVersion sinapiTableVersion = new SinapiTableVersion();

        sinapiTableVersion.id = UUID.randomUUID();
        sinapiTableVersion.stateId = state;
        sinapiTableVersion.referenceMonth = referenceMonth;
        sinapiTableVersion.taxReliefRegime = taxReliefRegime;
        sinapiTableVersion.publicationDate = publicationDate;

        return sinapiTableVersion;
    }

    public static SinapiTableVersion restore(
            UUID id,
            UUID state,
            YearMonth referenceMonth,
            TaxReliefRegime taxReliefRegime,
            LocalDate publicationDate
    ){
        SinapiTableVersion sinapiTableVersion = new SinapiTableVersion();

        sinapiTableVersion.id = id;
        sinapiTableVersion.stateId = state;
        sinapiTableVersion.referenceMonth = referenceMonth;
        sinapiTableVersion.taxReliefRegime = taxReliefRegime;
        sinapiTableVersion.publicationDate = publicationDate;

        return sinapiTableVersion;
    }

    public UUID getId() {
        return id;
    }

    public UUID getStateId() {
        return stateId;
    }

    public YearMonth getReferenceMonth() {
        return referenceMonth;
    }

    public TaxReliefRegime getTaxReliefRegime() {
        return taxReliefRegime;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }
}
