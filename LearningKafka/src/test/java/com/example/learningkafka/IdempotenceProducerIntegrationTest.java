package com.example.learningkafka;

import com.example.core.ActionCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IdempotenceProducerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, ActionCreatedEvent> kafkaTemplate;

    @MockitoBean //KafkaAdmin создает топики, а т.к. тест не работает с топиками, то можно ускорить его выполнение
    KafkaAdmin kafkaAdmin;

    @Test
    void ProducerConfigTest_is_idempotent(){
        ProducerFactory<String, ActionCreatedEvent> producerFactory = kafkaTemplate.getProducerFactory();


        Map<String, Object> config = producerFactory.getConfigurationProperties();


        assertEquals(true, config.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
        assertTrue("all".equalsIgnoreCase((String) config.get(ProducerConfig.ACKS_CONFIG)));

        if (config.containsKey(ProducerConfig.RETRIES_CONFIG)){
            assertTrue(
                    Integer.parseInt(config.get(ProducerConfig.RETRIES_CONFIG).toString()) > 0
            );
        }

        if (config.containsKey(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION)){
            assertTrue(
                    Integer.parseInt(config.get(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION).toString()) > 0
            );
        }
    }
}
