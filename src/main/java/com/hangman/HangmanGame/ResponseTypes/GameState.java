package com.hangman.HangmanGame.ResponseTypes;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GameState {
    private AtomicLong sessionID;
    private String word;
    private int remainingLife;
    private HashMap<String, Integer> letterVotes;

    public GameState(AtomicLong sessionID, String word, int remainingLife, HashMap<String, Integer> letterVotes) {
        this.sessionID = sessionID;
        this.word = word;
        this.remainingLife = remainingLife;
        this.letterVotes = letterVotes;
    }

    public AtomicLong getSessionID() {
        return sessionID;
    }

    public String getWord() {
        return word;
    }

    public int getRemainingLife() {
        return remainingLife;
    }

    public HashMap<String, Integer> getLetterVotes() {
        return letterVotes;
    }
}
