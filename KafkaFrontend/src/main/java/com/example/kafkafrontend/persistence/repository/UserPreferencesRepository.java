package com.example.kafkafrontend.persistence.repository;

import com.example.kafkafrontend.persistence.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, String> {
    Optional<UserPreferences> findBySessionId(String sessionId);
}

