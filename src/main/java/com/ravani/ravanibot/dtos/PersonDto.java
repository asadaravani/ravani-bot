package com.ravani.ravanibot.dtos;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Builder;

@Builder
public record PersonDto(
        String surname,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String surname_in_eng,

        String name,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String name_in_eng,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String patronymic,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String patronymic_in_eng,

        String birth_date,

        String gender,

        String birth_place,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String personal_number,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String nationality
) {}