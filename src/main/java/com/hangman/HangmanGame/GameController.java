package com.hangman.HangmanGame;

import com.hangman.HangmanGame.ResponseTypes.GameState;
import com.hangman.HangmanGame.game.HangmanGame;
import com.hangman.HangmanGame.ResponseTypes.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/hangman")
@EnableAsync
public class GameController {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    private static final int MAX_NUMBER_OF_USERS = 10;

    private static AtomicLong sessionID = new AtomicLong();


    private HashMap<String, HangmanGame> gameSessions = new HashMap<>();

    private TaskScheduler taskScheduler;


    @GetMapping("/sessions")
    public Collection<HangmanGame> getSessions() {
        ArrayList<GameState> sessions = new ArrayList<>();


        return gameSessions.values();
    }

    @PostMapping("/sessions")
    public Session openNewSession(HttpServletResponse res) {
        String token = generateNewToken();
        HangmanGame newSession = new HangmanGame(sessionID.incrementAndGet(), token);
        gameSessions.put((String.valueOf(newSession.getSessionID())), newSession);
        res.addCookie(new Cookie("token", token));
        res.setStatus(HttpStatus.CREATED.value());
        newSession.addUser(token);
        return new Session(newSession.getSessionID(), token);
    }

    @GetMapping("/sessions/{sessionID}")
    public ResponseEntity<HangmanGame> getSessionByID(@PathVariable("sessionID") String sessionID, @CookieValue(value = "token", defaultValue = "") String token, HttpServletResponse res) {
        if (!gameSessions.containsKey(sessionID)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);//  "the session with the id:" + sessionID + " does not exist.");
        }
        HangmanGame hangmanGame = gameSessions.get(sessionID);
        if (!hangmanGame.isAuthorized(token) && hangmanGame.getUserCount() >= MAX_NUMBER_OF_USERS) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        System.out.println("what: " + token);
        System.out.println(gameSessions.get(sessionID).getUsers());
        if ((token.equals("") || !hangmanGame.isAuthorized(token)) && hangmanGame.getUserCount() < MAX_NUMBER_OF_USERS) {
            token = generateNewToken();
            hangmanGame.addUser(token);
            res.addCookie(new Cookie("token", token));
        }

        return new ResponseEntity<>(hangmanGame, HttpStatus.OK);
    }

    @GetMapping("/sessions/{sessionID}/users")
    public ArrayList<String> getSessionUsersByID(@PathVariable("sessionID") String sessionID) {
        return gameSessions.get(sessionID).getUsers();
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    @PostMapping("/sessions/{sessionID}/{letter}")
    public ResponseEntity<HangmanGame> suggestAletter(@PathVariable("sessionID") String sessionID, @PathVariable("letter") String letter, @CookieValue("token") String token) {
        if (!gameSessions.containsKey(sessionID)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);//  "the session with the id:" + sessionID + " does not exist.");
        }
        HangmanGame game = gameSessions.get(sessionID);
        if (!game.isAuthorized(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (game.hasVoted(token)) {
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED);
        }
        if (game.getGameStatus() == 2 || game.getGameStatus() == 1 || game.getGameStatus() == -1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        game.addVote(letter, token);
        return ResponseEntity.status(HttpStatus.OK).body(game);
    }


    @PostMapping("/sessions/{sessionID}/start")
    public ResponseEntity<HangmanGame> startTheGame(@PathVariable("sessionID") String sessionID,  @CookieValue("token") String token) {
        if (!gameSessions.containsKey(sessionID)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);//  "the session with the id:" + sessionID + " does not exist.");
        }
        HangmanGame game = gameSessions.get(sessionID);
        if (!game.isAuthorized(token) || !game.getHostToken().equals(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }


        if (game.getGameStatus() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        game.startTheGame();



        return ResponseEntity.status(HttpStatus.OK).body(game);
    }


}
