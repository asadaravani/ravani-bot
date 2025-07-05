package com.ravani.ravanibot.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PassportDto extends DocumentDto {
    private final boolean isPassport;

    @JsonCreator
    public PassportDto(
            @JsonProperty("isPassport") boolean isPassport,
            @JsonProperty("country_code") String country_code,
            @JsonProperty("number") String number,
            @JsonProperty("issueDate") String issueDate,
            @JsonProperty("expiryDate") String expiryDate,
            @JsonProperty("issueAuthority") String issueAuthority,
            @JsonProperty("place_of_issue")  String place_of_issue,
            @JsonProperty("issued_by") String issued_by,
            @JsonProperty("person") PersonDto person

    ) {
        super(country_code, number, issueDate, expiryDate, issueAuthority, place_of_issue, issued_by, person);
        this.isPassport = isPassport;
    }

    @Override
    public String toString() {
        return "PassportDto{" +
                "isPassport=" + isPassport +
                "} " + super.toString();
    }
}
