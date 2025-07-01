package com.riya.game2048api;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/game")
public class GameController {

    private Map<String, Game2048> games = new HashMap<>();

    // 1. Start Game
    @PostMapping("/start")
    public String startGame() {
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, new Game2048());
        return gameId;
    }

    // 2. Make Move
    @PostMapping("/move")
    public Game2048 move(@RequestParam String gameId, @RequestParam String direction) {
        Game2048 game = games.get(gameId);
        if (game != null) {
            game.move(direction.toLowerCase());
        }
        return game;
    }

    // 3. Get Game State
    @GetMapping("/state")
    public Game2048 getState(@RequestParam String gameId) {
        return games.get(gameId);
    }
}
