package com.hangman.HangmanGame.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@EnableAsync
@EnableScheduling
public class HangmanGame {
    private static final String FILEPATH_INPUT = "input.txt";
    private static final int NUMBER_OF_GUESSES = 6;

    private static final int ROUND_LENGTH = 120;

    private int userCount;

    private long sessionID;

    private MultiLinkedList words;
    private int wordNumber;

    private DoubleLinkedList letters;

    // game status:
    //  * 2: waiting for people,
    //  * 1: win,
    //  * 0: neutral (still playing),
    //  * -1 lose,
    private int gameStatus;

    private CircularLinkedList CSLL1;
    private CircularLinkedList CSLL2;

    private int guessCounter;
    private int score;


    private ArrayList<String> tokens;
    private String hostToken;
    private ArrayList<String> votedTokens;


    private HashMap<String, Integer> letterVotes;


    public HangmanGame(long sessionID, String token) {
        this.sessionID = sessionID;
        init();
    }

    // initialize the game, variables are initialiazed in this function instead of the constructor,
    // in order to make the game replayable.
    public void init() {
        this.words = new MultiLinkedList();
        this.letters = new DoubleLinkedList();
        this.CSLL1 = new CircularLinkedList();
        this.CSLL2 = new CircularLinkedList();
        this.tokens = new ArrayList<>();
        this.letterVotes = new HashMap<>();
        votedTokens = new ArrayList<>();
        userCount = 0;
        wordNumber = 0;
        score = 0;
        guessCounter = 0;
        gameStatus = 2;
        addWordsFromFile(FILEPATH_INPUT);
        addLetters();
        pickARandomWord();
    }

    public static int getNumberOfGuesses() {
        return NUMBER_OF_GUESSES;
    }

    @JsonIgnore
    public int getGuessCounter() {
        return guessCounter;
    }

    public int getRemainingLife() {
        return NUMBER_OF_GUESSES - guessCounter;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public long getSessionID() {
        return sessionID;
    }

    public boolean isAuthorized(String token) {
        return tokens.contains(token);
    }

    public int getUserCount() {
        return tokens.size();
    }

    public String getLetters() {
        return letters.getTheLetters();
    }

    @JsonIgnore
    public ArrayList<String> getUsers() {
        return tokens;
    }

    public void addUser(String token) {
        tokens.add(token);
        if (tokens.size() == 1) {
            setHostToken(token);
        }
    }

    public void startTheGame() {

        restart();
        Timer gameLoop = new Timer();

        gameLoop.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gameStatus = checkWinStatus();
                if (gameStatus == 1 || gameStatus == -1) {
                    gameStatus = 2;
                    gameLoop.cancel();
                }
                System.out.println("loops");
                String selectedVote = countTheVotes();
                System.out.println(selectedVote);
                update(selectedVote);
                letterVotes.clear();
                votedTokens.clear();
            }
        }, ROUND_LENGTH * 1000, ROUND_LENGTH * 1000);

    }

    public void restart() {
        CSLL2 = new CircularLinkedList();
        CSLL1 = new CircularLinkedList();
        letters = new DoubleLinkedList();
        pickARandomWord();
        addLetters();
        gameStatus = 0;
        guessCounter = 0;
    }

    public String countTheVotes() {
        if (letterVotes.size() == 0) return "";
//        List sortedList = letterVotes.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.to);
//        sortedList.get(0).

        Set<Map.Entry<String, Integer>> entries = letterVotes.entrySet();

        Comparator<Map.Entry<String, Integer>> valueComparator = (e1, e2) -> {
            int v1 = e1.getValue();
            int v2 = e2.getValue();
            if (v1 == v2) return 0;
            if (v1 < v2) return 1;
            return -1;
        };

        List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(entries);

        entryList.sort(valueComparator);


        LinkedHashMap<String, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry :
                entryList) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        ArrayList<Map.Entry<String, Integer>> mostVotedOnes = new ArrayList<>();


        Iterator<Map.Entry<String, Integer>> itr = sortedHashMap.entrySet().iterator();

        if (sortedHashMap.size() == 1) return itr.next().getKey();

        Map.Entry<String, Integer> prev = itr.next();
        while (itr.hasNext()) {
            Map.Entry<String, Integer> voteEntry = itr.next();

            if (voteEntry.getValue() == (int) prev.getValue()) {
                mostVotedOnes.add(prev);
            } else {
                mostVotedOnes.add(prev);
                break;
            }
            prev = voteEntry;
        }

        System.out.println(mostVotedOnes);
        int randomLetter = new Random().nextInt(mostVotedOnes.size());
        return mostVotedOnes.get(randomLetter).getKey();


    }


    public void addVote(String vote, String token) {
        if (letterVotes.containsKey(vote)) {
            letterVotes.put(vote, letterVotes.get(vote) + 1);
        } else {
            letterVotes.put(vote, 1);
        }
        votedTokens.add(token);
    }

    public boolean hasVoted(String token) {
        return votedTokens.contains(token);
    }

    public void setHostToken(String token) {
        hostToken = token;
    }

    @JsonIgnore
    public String getHostToken() {
        return hostToken;
    }

    public void update(String letter) {
        // game status:
        //  * 1: win,
        //  * 0: neutral (still playing),
        //  * -1 lose,
        gameStatus = checkWinStatus();

        if (gameStatus != 0) return;
        char chosenChar = '\0';
        try {
            chosenChar = letter.toLowerCase(Locale.ENGLISH).charAt(0);
        } catch (StringIndexOutOfBoundsException ignored) {
        }


        boolean isFound = CSLL2.revealTheletters(CSLL1, chosenChar);

        if (!isFound) {
            guessCounter++;
        } else {
            if ("aeiou".contains(String.valueOf(chosenChar))) {
                score += 5;
            } else {
                score += 10;
            }
        }


    }


    public int checkWinStatus() {

        if (guessCounter == NUMBER_OF_GUESSES) {
            return -1;
        }
        if (!CSLL2.getTheWord().contains("-")) {
            return 1;
        }

        return 0;
    }


    private void addWordsFromFile(String path) {
        BufferedReader br;
        try {
            FileReader fr = new FileReader(path);
            br = new BufferedReader(fr);

            String s;
            while ((s = br.readLine()) != null) {
                wordNumber++;
                words.addWord(s.length(), s);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    private void addLetters() {
        for (int i = 'a'; i <= 'z'; i++) {
            letters.add((char) i);
        }
    }

    private void pickARandomWord() {
        Random rand = new Random();
        String word;
        int randNumber;
        do {
            randNumber = rand.nextInt(wordNumber - 1) + 1;
            word = (String) words.getWord(randNumber);
            word = word.toLowerCase(Locale.ENGLISH);
            System.out.println("Randomly generated number: " + randNumber);
            // theoritically not possible, but, in case the random number is bigger than the number of the words,
            // we will select a different random number.
        } while (word == null);

        // fill the circular linked lists.
        for (int i = word.length() - 1; i >= 0; i--) {
            CSLL1.add(word.charAt(i));
            CSLL2.add('-');
        }
        // uncomment the line above to see the chosen word
//        System.out.println("chosen word is: " + word);
    }


    public String getTheWord() {
        String word = CSLL2.getTheWord();
        System.out.println(word);
        return word == null ? "?" : word;
    }


    public HashMap<String, Integer> getLetterVotes() {
        return letterVotes;
    }

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }
}
