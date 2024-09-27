package com.example.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.model.Board;
import com.example.model.Game;
import com.example.model.Player;
import com.example.model.Game.GameState;
import com.example.model.Player.AiType;

@Service
public class GameService {

    private HashMap<String, Game> games;
    private int gameIndex = 0;

    public GameService() {
        games = new HashMap<>();
    }

}
