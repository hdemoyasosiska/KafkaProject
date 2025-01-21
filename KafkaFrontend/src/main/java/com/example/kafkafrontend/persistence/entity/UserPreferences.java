package com.example.kafkafrontend.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserPreferences {
    @Id
    private String sessionId; // уникальная сессия пользователя
    Integer romantic=0;
    Integer action=0;
    Integer cruel=0;
    Integer realism=0;

    public UserPreferences(String sessionId, Integer romantic, Integer action, Integer cruel, Integer realism) {
        this.sessionId = sessionId;
        this.romantic = romantic;
        this.action = action;
        this.cruel = cruel;
        this.realism = realism;
    }

    public UserPreferences(String sessionId) {
        this.sessionId = sessionId;
    }

    public UserPreferences() {

    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setRomantic(Integer romantic) {
        this.romantic = romantic;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public void setCruel(Integer cruel) {
        this.cruel = cruel;
    }

    public void setRealism(Integer realism) {
        this.realism = realism;
    }
}

