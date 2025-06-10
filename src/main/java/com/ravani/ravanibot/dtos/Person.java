package com.ravani.ravanibot.dtos;

public record Person(
        String surname,
        String name,
        String patronymic,
        String birth_date,
        String gender,
        String birth_place,
        String personal_number
) {}