package com.example.learningkafka.service;

import com.example.core.ActionCreatedEvent;
import com.example.learningkafka.service.dto.UserActionDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final KafkaTemplate<String, ActionCreatedEvent> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public String createAction(UserActionDTO userActionDTO) throws ExecutionException, InterruptedException {
        String actionID = UUID.randomUUID().toString();

        ActionCreatedEvent actionCreatedEvent = new ActionCreatedEvent(
                actionID, userActionDTO.getGenre(),
                userActionDTO.getLiked());

        ProducerRecord<String, ActionCreatedEvent> record = new ProducerRecord<>(
                "user-actions-topic", actionID, actionCreatedEvent);

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, ActionCreatedEvent> result
                = kafkaTemplate.send(record).get(); // в синхронном режиме


        logger.info("topic: {}", result.getRecordMetadata().topic());
        logger.info("partition: {}", result.getRecordMetadata().partition());
        logger.info("offset: {}", result.getRecordMetadata().offset());
        logger.info("time: {}", result.getRecordMetadata().timestamp());

        return actionID;
    }
}
