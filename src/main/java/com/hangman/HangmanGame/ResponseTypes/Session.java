package com.hangman.HangmanGame.ResponseTypes;

public class Session {
    private long sessionID;
    private String token;

    public Session(long sessionID, String token) {
        this.sessionID = sessionID;
        this.token = token;
    }

    public long getSessionID() {
        return sessionID;
    }

    public String getToken() {
        return token;
    }
}
