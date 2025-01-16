package com.example.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreatedEvent {
    private  String productID;
    private  String title;
    private  BigDecimal price;
    private  Integer quantity;


    public ProductCreatedEvent(String productID, String title, BigDecimal price, Integer quantity) {
        this.productID = productID;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public ProductCreatedEvent() {
    }
}
