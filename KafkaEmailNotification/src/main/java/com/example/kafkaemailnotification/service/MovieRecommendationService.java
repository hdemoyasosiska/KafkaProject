package com.example.kafkaemailnotification.service;

import com.example.core.Movie;
import com.example.kafkaemailnotification.persistence.entity.UserPreferences;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class MovieRecommendationService {

    //private final MovieRepository movieRepository;
    private final KafkaTemplate<String, Movie> kafkaTemplate2;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public MovieRecommendationService(KafkaTemplate<String, Movie> kafkaTemplate2) {
        this.kafkaTemplate2 = kafkaTemplate2;
    }

    private static Map<String, List<String>> genreMovies = new HashMap<>();

    static {
        genreMovies.put("Romantic", Arrays.asList("Love Under the Stars", "Sweet Memories", "Romance in the Air",
                "Tears of Hope", "A Life to Live", "The Last Goodbye"));
        genreMovies.put("Action", Arrays.asList("Fast and Furious", "Warriors Unleashed", "The Last Stand",
                "Galactic Wars", "Time Traveler", "The Last Horizon"));
        genreMovies.put("Crime", Arrays.asList("The Silent Heist", "Criminal Intentions", "Shadows of Crime",
                "Laugh Out Loud", "Comedy Kings", "The Jesters"));
        genreMovies.put("Realism", Arrays.asList("A Quiet Life", "The Reality", "On the Edge of Truth"));
        //genreMovies.put("Drama", Arrays.asList("Tears of Hope", "A Life to Live", "The Last Goodbye"));
        //genreMovies.put("Sci-Fi", Arrays.asList("Galactic Wars", "Time Traveler", "The Last Horizon"));
        //genreMovies.put("Comedy", Arrays.asList("Laugh Out Loud", "Comedy Kings", "The Jesters"));
    }

//    @Autowired
//    public MovieRecommendationService(MovieRepository movieRepository) {
//        this.movieRepository = movieRepository;
//    }

    public String generateRecommendations(UserPreferences userPreferences) throws ExecutionException, InterruptedException {
        Map<String, Integer> genreWeights = getGenreWeights(userPreferences);

        // Нормализуем веса жанров для расчета вероятностей
        int totalWeight = genreWeights.values().stream().mapToInt(Integer::intValue).sum();
        if (totalWeight == 0) {
            totalWeight = 1;  // Чтобы избежать деления на 0
        }

        // Создаем список жанров с вероятностями
        List<String> genres = genreWeights.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .toList();

        // Выбираем жанр на основе вероятности
        Random random = new Random();
        String chosenGenre = genres.get(random.nextInt(genres.size()));

        // Выбираем фильм случайным образом из выбранного жанра
        List<String> movies = genreMovies.get(chosenGenre);
        String movieName = movies.get(random.nextInt(movies.size()));

        return sendRecommendations(new Movie(movieName, chosenGenre));
    }

    private String sendRecommendations(Movie recommendedMovie) throws ExecutionException, InterruptedException {
        String movieID = UUID.randomUUID().toString();
        ProducerRecord<String, Movie> record = new ProducerRecord<>(
                "recommended-movies-topic", movieID, recommendedMovie);

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, Movie> result
                = kafkaTemplate2.send(record).get(); // в синхронном режиме


        logger.info("topic: {}", result.getRecordMetadata().topic());
        logger.info("partition: {}", result.getRecordMetadata().partition());
        logger.info("offset: {}", result.getRecordMetadata().offset());
        logger.info("time: {}", result.getRecordMetadata().timestamp());
        logger.info("string: {}", result.getRecordMetadata().toString());
        return movieID;
    }

    private static Map<String, Integer> getGenreWeights(UserPreferences userPreferences) {
        Map<String, Integer> genreWeights = new HashMap<>();
        genreWeights.put("Romantic", userPreferences.getRomantic());
        genreWeights.put("Action", userPreferences.getAction());
        genreWeights.put("Crime", userPreferences.getCruel());
        genreWeights.put("Realism", userPreferences.getRealism());
        //genreWeights.put("Drama", userPreferences.getRomantic() + userPreferences.getAction() / 2);  // Пример расчета для других жанров
        //genreWeights.put("Sci-Fi", userPreferences.getAction() + userPreferences.getRealism() / 2); // Пример для фантастики
        //genreWeights.put("Comedy", userPreferences.getRomantic() + userPreferences.getCruel() / 3); // Пример для комедии
        return genreWeights;
    }
}

