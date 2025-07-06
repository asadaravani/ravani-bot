package com.ravani.ravanibot.dtos;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;

@Getter
public sealed abstract class DocumentDto permits PassportDto, DriverLicenseDto{

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private final String country_code;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private final String number;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private final String issueDate;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private final String expiryDate;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private final String issueAuthority;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private final String place_of_issue;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private final String issued_by;

    private final PersonDto person;

    public DocumentDto(String country_code, String number, String issueDate, String expiryDate, String issueAuthority, String place_of_issue, String issued_by, PersonDto person) {
        this.country_code = validateField(country_code);
        this.number = validateField(number);
        this.issueDate = validateField(issueDate);
        this.expiryDate = validateField(expiryDate);
        this.issueAuthority = validateField(issueAuthority);
        this.place_of_issue = validateField(place_of_issue);
        this.issued_by = validateField(issued_by);
        this.person = person;
    }

    public PersonDto getPerson() {
        return new PersonDto.PersonDtoBuilder()
                .surname(validateField(person.surname()))
                .surname_in_eng(validateField(person.surname_in_eng()))
                .name(validateField(person.name()))
                .name_in_eng(validateField(person.name_in_eng()))
                .patronymic(validateField(person.patronymic()))
                .patronymic_in_eng(validateField(person.patronymic_in_eng()))
                .birth_date(validateField(person.birth_date()))
                .gender(validateField(person.gender()))
                .birth_place(validateField(person.birth_place()))
                .personal_number(validateField(person.personal_number()))
                .nationality(validateField(person.nationality()))
                .build();
    }

    protected String validateField(String field) {
        return field == null ? "" : field.toUpperCase();
    }
    public boolean isDocument(){
        DocumentDto dto = this;
        if (dto instanceof PassportDto) {
            return ((PassportDto) dto).isPassport();
        }
        else if (dto instanceof DriverLicenseDto) {
            return ((DriverLicenseDto) dto).isDriverLicense();
        }
        return false;
    }

    @Override
    public String toString() {
        return "DocumentDto{" +
                "country_code='" + country_code + '\'' +
                ", number='" + number + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", issueAuthority='" + issueAuthority + '\'' +
                ", place_of_issue='" + place_of_issue + '\'' +
                ", issued_by='" + issued_by + '\'' +
                ", person=" + person +
                '}';
    }
}
