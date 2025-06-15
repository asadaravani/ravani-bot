package com.ravani.ravanibot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigInteger;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BotUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long chatId;
    String name;
    BigInteger requestAmount;
}
