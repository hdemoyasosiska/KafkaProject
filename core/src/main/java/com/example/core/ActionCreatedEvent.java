package com.example.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionCreatedEvent {
    private String actionID;
    private String genre;
    private Boolean liked;


    public ActionCreatedEvent(String actionID, String genre, boolean liked) {
        this.actionID = actionID;
        this.genre = genre;
        this.liked = liked;
    }

    public ActionCreatedEvent() {
    }
}
