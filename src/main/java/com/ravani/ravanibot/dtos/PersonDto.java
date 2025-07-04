package com.ravani.ravanibot.dtos;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Builder;

@Builder
public record PersonDto(
        String surname,

        String name,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String patronymic,

        String birth_date,

        String gender,

        String birth_place,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String personal_number,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        String nationality
) {}