package com.example.kafkaemailnotification.service;

import com.example.core.ActionCreatedEvent;
import com.example.kafkaemailnotification.persistence.entity.UserPreferences;
import com.example.kafkaemailnotification.persistence.repository.UserPreferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

@Service
public class UserActionService {

    private final UserPreferencesRepository userPreferenceRepository;
    private final MovieRecommendationService movieRecommendationService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public UserActionService(UserPreferencesRepository userPreferenceRepository,
                             MovieRecommendationService movieRecommendationService) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.movieRecommendationService = movieRecommendationService;

    }

    public void processUserAction(ActionCreatedEvent actionCreatedEvent, String sessionId) throws ExecutionException, InterruptedException {
        UserPreferences userPreferences = userPreferenceRepository.findBySessionId(sessionId)
                .orElse(new UserPreferences(sessionId));

        // 2. Обновляем предпочтения на основе действия пользователя
        String genre = actionCreatedEvent.getGenre();
        boolean liked = actionCreatedEvent.getLiked();

        Integer ratingChange = liked ? 1 : -1;

        try {
            Method method = userPreferences.getClass().getMethod("get"+genre);
            Integer curr = (Integer) method.invoke(userPreferences);
            if (curr==null) curr = 0;
            method = userPreferences.getClass().getMethod("set"+genre, Integer.class);
            method.invoke(userPreferences, curr+ratingChange);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        // 3. Сохраняем обновленные предпочтения
        userPreferenceRepository.save(userPreferences);
        String recommendedMovie = movieRecommendationService.generateRecommendations(userPreferences);




        logger.info("Updated preferences for session {}", sessionId);

        // 4. Генерируем рекомендации на основе текущих предпочтений
//        List<Movie> recommendedMovies = movieRecommendationService.generateRecommendations(userPreferences);
//        logger.info("Generated recommendations for session {}: {}", sessionId, recommendedMovies);
    }

    private String getUserIdFromAction(ActionCreatedEvent actionCreatedEvent) {
        // Извлекаем userId из события, например, из контекста или метаданных
        return "someUserId"; // Это должно быть настроено в зависимости от структуры вашего события
    }
}

