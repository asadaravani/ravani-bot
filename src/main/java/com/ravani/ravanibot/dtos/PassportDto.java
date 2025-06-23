package com.ravani.ravanibot.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class PassportDto extends DocumentDto {
    private final boolean isPassport;

    @JsonCreator
    public PassportDto(
            @JsonProperty("isPassport") boolean isPassport,
            @JsonProperty("country") String country,
            @JsonProperty("number") String number,
            @JsonProperty("issueDate") String issueDate,
            @JsonProperty("expiryDate") String expiryDate,
            @JsonProperty("issueAuthority") String issueAuthority,
            @JsonProperty("person") PersonDto person

    ) {
        super(country, number, issueDate, expiryDate, issueAuthority, person);
        this.isPassport = isPassport;
    }
}
