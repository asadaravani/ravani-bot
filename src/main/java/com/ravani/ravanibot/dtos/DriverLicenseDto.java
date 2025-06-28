package com.ravani.ravanibot.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class DriverLicenseDto extends DocumentDto {
    private final boolean isDriverLicense;
    private final String place_of_residence;
    private final String special_marks;

    public DriverLicenseDto(
            @JsonProperty("isDriverLicense") boolean isDriverLicense,
            @JsonProperty("place_of_residence")  String place_of_residence,
            @JsonProperty("special_marks")  String special_marks,
            @JsonProperty("country") String country,
            @JsonProperty("number") String number,
            @JsonProperty("issueDate") String issueDate,
            @JsonProperty("expiryDate") String expiryDate,
            @JsonProperty("issueAuthority") String issueAuthority,
            @JsonProperty("place_of_issue")  String place_of_issue,
            @JsonProperty("person") PersonDto person
    ) {
        super(country, number, issueDate, expiryDate, issueAuthority,place_of_issue, person);
        this.place_of_residence = validateField(place_of_residence);
        this.special_marks = validateField(special_marks);
        this.isDriverLicense = isDriverLicense;
    }
}
