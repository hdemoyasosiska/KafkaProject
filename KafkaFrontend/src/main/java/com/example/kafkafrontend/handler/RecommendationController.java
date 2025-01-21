package com.example.kafkafrontend.handler;

import com.example.core.Movie;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RecommendationController {

    private final SimpMessagingTemplate messagingTemplate;

    public RecommendationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendRecommendation(Movie movie) {
        messagingTemplate.convertAndSend("/topic/recommendations", movie);
    }
}

