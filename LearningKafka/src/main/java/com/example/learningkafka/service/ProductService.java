package com.example.learningkafka.service;

import com.example.learningkafka.service.dto.UserActionDTO;

import java.util.concurrent.ExecutionException;

public interface ProductService {
    String createAction(UserActionDTO userActionDTO) throws ExecutionException, InterruptedException;
}
