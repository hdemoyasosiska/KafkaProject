package com.example.kafkaemailnotification.handler;

import com.example.core.ActionCreatedEvent;
import com.example.kafkaemailnotification.persistence.entity.ProcessedEventEntity;
import com.example.kafkaemailnotification.persistence.repository.ProcessedEventRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;


import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@EmbeddedKafka
@SpringBootTest(properties = "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class ActionCreatedEventHandlerIntegrationTest {

    @MockBean // мок чтобы не ходить в бд
    ProcessedEventRepository repository;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    ProductCreatedEventHandler handler;

    @Test
    void handle_event_handled_successfully() throws ExecutionException, InterruptedException {
        ActionCreatedEvent actionCreatedEvent = new ActionCreatedEvent();
        actionCreatedEvent.setLiked(true);
        actionCreatedEvent.setActionID(UUID.randomUUID().toString());
        actionCreatedEvent.setGenre("Test product");

        String messageID = UUID.randomUUID().toString();
        String messageKey = actionCreatedEvent.getActionID();

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                "product-created-events-topic",
                messageKey,
                actionCreatedEvent
        );

        record.headers().add("messageID", messageID.getBytes());
        record.headers().add(KafkaHeaders.RECEIVED_KEY, messageKey.getBytes());

        ProcessedEventEntity processedEventEntity = new ProcessedEventEntity();
        when(repository.findByMessageId(anyString())).thenReturn(processedEventEntity);
        when(repository.save(any(ProcessedEventEntity.class))).thenReturn(null);


        kafkaTemplate.send(record).get();


        ArgumentCaptor<ActionCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ActionCreatedEvent.class);
        ArgumentCaptor<String> messageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageKeyCaptor = ArgumentCaptor.forClass(String.class);

        verify(handler, timeout(5000).times(1)).handle(
                eventCaptor.capture(),
                messageIdCaptor.capture(),
                messageKeyCaptor.capture());

        assertEquals(messageID, messageIdCaptor.getValue());
        assertEquals(messageKey, messageKeyCaptor.getValue());
        assertEquals(actionCreatedEvent.getActionID(), eventCaptor.getValue().getActionID());
    }
}
