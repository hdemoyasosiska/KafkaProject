package com.example.kafkaemailnotification.handler;


import com.example.core.ProductCreatedEvent;
import com.example.kafkaemailnotification.exception.NonRetryableException;
import com.example.kafkaemailnotification.exception.RetryableException;
import com.example.kafkaemailnotification.persistence.entity.ProcessedEventEntity;
import com.example.kafkaemailnotification.persistence.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    //private RestTemplate restTemplate;

    private ProcessedEventRepository processedEventRepository;

    public ProductCreatedEventHandler(RestTemplate restTemplate, ProcessedEventRepository processedEventRepository) {
        //this.restTemplate = restTemplate;
        this.processedEventRepository = processedEventRepository;
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Transactional
    @KafkaHandler //маппит по входным агрументам метода
    public void handle(@Payload ProductCreatedEvent productCreatedEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey){

        ProcessedEventEntity eventEntity = processedEventRepository.findByMessageId(messageId);

        if (eventEntity!= null){
            logger.info("Duplicate message id: {}", messageId);
            return;
        }

        try{
            logger.info("Received event: {}", productCreatedEvent.getTitle());
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
            processedEventRepository.save(new ProcessedEventEntity(messageId, productCreatedEvent.getProductID()));
        }catch (DataIntegrityViolationException e){
            logger.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }

}
