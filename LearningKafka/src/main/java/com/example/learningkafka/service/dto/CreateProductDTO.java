package com.example.learningkafka.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
@Setter
public class CreateProductDTO {
    private final String title;
    private final BigDecimal price;
    private final Integer quantity;
}
