package com.example.learningkafka.service;

import com.example.core.ProductCreatedEvent;
import com.example.learningkafka.service.dto.CreateProductDTO;
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

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public String createProduct(CreateProductDTO createProductDTO) throws ExecutionException, InterruptedException {
        String productID = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productID, createProductDTO.getTitle(),
                createProductDTO.getPrice(), createProductDTO.getQuantity());

        ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord<>(
                "product-created-events-topic", productID, productCreatedEvent);

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, ProductCreatedEvent> result
                = kafkaTemplate.send(record).get(); // в синхронном режиме


        logger.info("topic: {}", result.getRecordMetadata().topic());
        logger.info("partition: {}", result.getRecordMetadata().partition());
        logger.info("offset: {}", result.getRecordMetadata().offset());
        logger.info("time: {}", result.getRecordMetadata().timestamp());

        return productID;
    }
}
