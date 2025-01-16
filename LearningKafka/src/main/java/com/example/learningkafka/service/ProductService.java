package com.example.learningkafka.service;

import com.example.learningkafka.service.dto.CreateProductDTO;

import java.util.concurrent.ExecutionException;

public interface ProductService {
    String createProduct(CreateProductDTO createProductDTO) throws ExecutionException, InterruptedException;
}
