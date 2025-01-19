package com.example.learningkafka.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
@Setter
public class UserActionDTO {
    private final String genre;
    private final Boolean liked;
}
