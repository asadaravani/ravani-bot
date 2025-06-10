package com.ravani.ravanibot.dtos;

public record Passport(
        Boolean isPassport,
        String country,
        String number,
        String issueDate,
        String expiryDate,
        String issueAuthority,
        Person person
) {}
