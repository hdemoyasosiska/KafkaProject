package com.example.kafkaemailnotification.handler;


import com.example.core.ActionCreatedEvent;
import com.example.kafkaemailnotification.exception.NonRetryableException;
import com.example.kafkaemailnotification.exception.RetryableException;
import com.example.kafkaemailnotification.persistence.entity.ProcessedEventEntity;
import com.example.kafkaemailnotification.persistence.repository.ProcessedEventRepository;
import com.example.kafkaemailnotification.service.UserActionService;
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
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
@KafkaListener(topics = "user-actions-topic")
public class ProductCreatedEventHandler {

    //private RestTemplate restTemplate;
    private final String sessionID;

    private ProcessedEventRepository processedEventRepository;
    private UserActionService userActionService;

    public ProductCreatedEventHandler(ProcessedEventRepository processedEventRepository, UserActionService userActionService) {
        //this.restTemplate = restTemplate;
        this.processedEventRepository = processedEventRepository;
        this.userActionService = userActionService;
        this.sessionID = UUID.randomUUID().toString();
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Transactional
    @KafkaHandler //маппит по входным агрументам метода
    public void handle(@Payload ActionCreatedEvent actionCreatedEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) throws ExecutionException, InterruptedException {

        ProcessedEventEntity eventEntity = processedEventRepository.findByMessageId(messageId);
        userActionService.processUserAction(actionCreatedEvent, sessionID);

        if (eventEntity!= null){
            logger.info("Duplicate message id: {}", messageId);
            return;
        }

        try{
            logger.info("Received event: {}", actionCreatedEvent.getGenre());
        }catch (ResourceAccessException e){
            logger.error(e.getMessage());
            throw new RetryableException(e);
        } catch (HttpServerErrorException e){
            logger.error(e.getMessage());
            throw new NonRetryableException(e);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new NonRetryableException(e);
        }

        try {
            processedEventRepository.save(new ProcessedEventEntity(messageId, actionCreatedEvent.getActionID()));
        }catch (DataIntegrityViolationException e){
            logger.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }

}
