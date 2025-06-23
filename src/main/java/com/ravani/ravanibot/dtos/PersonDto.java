package com.ravani.ravanibot.dtos;

import lombok.Builder;

@Builder
public record PersonDto(
        String surname,
        String name,
        String patronymic,
        String birth_date,
        String gender,
        String birth_place,
        String personal_number
) {}