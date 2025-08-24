package com.ravani.ravanibot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record PersonDto(
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String surname,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String surname_in_eng,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String name,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String given_names,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String middle_name,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String name_in_eng,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String patronymic,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String patronymic_in_eng,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String birth_date,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String gender,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String birth_place,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String personal_number,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String nationality
) {}