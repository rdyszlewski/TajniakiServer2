package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.lobby.manager.LobbyPlayer;
import com.parabbits.tajniakiserver.summary.GameHistory;
import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Game {

    private UUID id;
    private final GamePlayersManager players;
    private  GameHistory history = new GameHistory();
    private GameSettings settings;
    private  GameState state;
    private  Board board;

    private Team firstTeam;


    public UUID getID(){
        return id;
    }
    public GamePlayersManager getPlayers(){
        return players;
    }

    public Game(UUID id){
        this.id = id;
        state = new GameState();
        getState().setCurrentStep(GameStep.MAIN);
        settings = new GameSettings();
        players = new GamePlayersManager(settings);
        board = new Board();
    }

    private boolean started = false;

    public boolean isStarted(){
        return started;
    }

    public void reset(){
        history = new GameHistory();
        firstTeam = null;
        state = new GameState();
        settings = new GameSettings();
        board = new Board();
        started = false;

        // TODO: przenieść to w jakieś inne miejsce. Może w GameManager zrobić reset graczy
        // TODO: zresetować jakoś lobby
        getState().setCurrentStep(GameStep.LOBBY);
    }

    public GameState getState(){
        return state;
    }

    public Board getBoard(){
        return board;
    }

    public void initializeGame(List<Player> playersList) throws IOException {
        playersList.forEach(players::addPlayer);
        if(firstTeam == null){
            firstTeam = randomFirstGroup();
            board.init(firstTeam, settings);
            state.initState(firstTeam, settings.getFirstTeamWords());
        }
        initHistory();
        started = true;
    }

    private void initHistory(){
        history.setWords(getWords(CardColor.BLUE), Team.BLUE);
        history.setWords(getWords(CardColor.RED), Team.RED);
        history.setKiller(getWords(CardColor.KILLER).get(0));
    }

    private List<String> getWords(CardColor cardColor){
        return board.getCards().stream().filter(card->card.getColor()==cardColor).map(Card::getWord).collect(Collectors.toList());
    }

    private Team randomFirstGroup(){
        int randValue = new Random().nextInt(100);
        return randValue < 50 ? Team.BLUE : Team.RED;
    }

    public void useCard(Card card){
        // TODO: prawdopodobnie tutaj wystarczy word
        board.getAnswerManager().reset();
        board.getFlagsManager().removeFlags(card);
        state.useCard(card);
    }

    public GameSettings getSettings() {
        return settings;
    }

    public GameHistory getHistory(){
        return history;
    }
}
