package com.example.kafkafrontend.handler;


import com.example.kafkafrontend.exception.NonRetryableException;
import com.example.kafkafrontend.exception.RetryableException;
import com.example.core.Movie;
import com.example.kafkafrontend.persistence.entity.ProcessedEventEntity;
import com.example.kafkafrontend.persistence.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@KafkaListener(topics = "recommended-movies-topic")
public class RecommendationsHandler {

    private final ProcessedEventRepository processedEventRepository;
    private final RecommendationController recommendationController;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public RecommendationsHandler(ProcessedEventRepository processedEventRepository, RecommendationController recommendationController) {
        this.processedEventRepository = processedEventRepository;
        this.recommendationController = recommendationController;
    }

    @Transactional
    @KafkaHandler
    public void handle(@Payload Movie recommendedMovie,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) throws ExecutionException, InterruptedException {

        ProcessedEventEntity eventEntity = processedEventRepository.findByMessageId(messageId);

        if (eventEntity != null) {
            logger.info("Duplicate message id: {}", messageId);
            return;
        }

        try {
            logger.info("Received event: {}, {}", recommendedMovie.getName(), recommendedMovie.getGenre());
            recommendationController.sendRecommendation(recommendedMovie);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new NonRetryableException(e);
        }

        try {
            processedEventRepository.save(new ProcessedEventEntity(messageId, messageId));
        } catch (DataIntegrityViolationException e) {
            logger.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }
}
