package com.ravani.ravanibot.dtos;

import lombok.Getter;

@Getter
public sealed abstract class DocumentDto permits PassportDto, DriverLicenseDto {
    private final String country;
    private final String number;
    private final String issueDate;
    private final String expiryDate;
    private final String issueAuthority;
    private final PersonDto person;

    public DocumentDto(String country, String number, String issueDate, String expiryDate, String issueAuthority, PersonDto person) {
        this.country = validateField(country);
        this.number = validateField(number);
        this.issueDate = validateField(issueDate);
        this.expiryDate = validateField(expiryDate);
        this.issueAuthority = validateField(issueAuthority);
        this.person = person;
    }

    public PersonDto getPerson() {
        return new PersonDto.PersonDtoBuilder()
                .surname(validateField(person.surname()))
                .name(validateField(person.name()))
                .patronymic(validateField(person.patronymic()))
                .birth_date(validateField(person.birth_date()))
                .gender(validateField(person.gender()))
                .birth_place(validateField(person.birth_place()))
                .personal_number(validateField(person.personal_number()))
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
}
