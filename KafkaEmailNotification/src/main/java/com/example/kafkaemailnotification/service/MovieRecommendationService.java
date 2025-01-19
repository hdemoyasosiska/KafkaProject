package com.example.kafkaemailnotification.service;

import com.example.kafkaemailnotification.persistence.entity.UserPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieRecommendationService {

    //private final MovieRepository movieRepository;

    private static Map<String, List<String>> genreMovies = new HashMap<>();

    static {
        genreMovies.put("Romantic", Arrays.asList("Love Under the Stars", "Sweet Memories", "Romance in the Air"));
        genreMovies.put("Action", Arrays.asList("Fast and Furious", "Warriors Unleashed", "The Last Stand"));
        genreMovies.put("Crime", Arrays.asList("The Silent Heist", "Criminal Intentions", "Shadows of Crime"));
        genreMovies.put("Realism", Arrays.asList("A Quiet Life", "The Reality", "On the Edge of Truth"));
        genreMovies.put("Drama", Arrays.asList("Tears of Hope", "A Life to Live", "The Last Goodbye"));
        genreMovies.put("Sci-Fi", Arrays.asList("Galactic Wars", "Time Traveler", "The Last Horizon"));
        genreMovies.put("Comedy", Arrays.asList("Laugh Out Loud", "Comedy Kings", "The Jesters"));
    }

//    @Autowired
//    public MovieRecommendationService(MovieRepository movieRepository) {
//        this.movieRepository = movieRepository;
//    }

    public void generateRecommendations(UserPreferences userPreferences) {
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

        System.out.println("Film: " + movieName + ", Genre: " + chosenGenre);
    }

    private static Map<String, Integer> getGenreWeights(UserPreferences userPreferences) {
        Map<String, Integer> genreWeights = new HashMap<>();
        genreWeights.put("Romantic", userPreferences.getRomantic());
        genreWeights.put("Action", userPreferences.getAction());
        genreWeights.put("Crime", userPreferences.getCruel());
        genreWeights.put("Realism", userPreferences.getRealism());
        genreWeights.put("Drama", userPreferences.getRomantic() + userPreferences.getAction() / 2);  // Пример расчета для других жанров
        genreWeights.put("Sci-Fi", userPreferences.getAction() + userPreferences.getRealism() / 2); // Пример для фантастики
        genreWeights.put("Comedy", userPreferences.getRomantic() + userPreferences.getCruel() / 3); // Пример для комедии
        return genreWeights;
    }
}

