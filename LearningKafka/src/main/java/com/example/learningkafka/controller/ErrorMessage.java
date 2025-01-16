package com.example.learningkafka.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@RequiredArgsConstructor
@Setter
@Getter
public class ErrorMessage {
    private final Date timestamp;
    private final String message;
}
