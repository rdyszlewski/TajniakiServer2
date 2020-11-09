package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.game.AnswerCorrectness;
import com.parabbits.tajniakiserver.game.WordValidator;
import com.parabbits.tajniakiserver.game.parameters.QuestionParam;
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

    public void startGame(List<Player> playersList) throws IOException {
        playersList.forEach(players::addPlayer); // TODO: odkomentować to, po zakończeniu testowania
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

    public ClickResult click(int cardId, Player player){
        Card card = board.getCard(cardId);
        if(canClick(player, card)){
            board.getAnswerManager().setAnswer(card, player);
            if(allPlayersAnswer(card, player.getTeam())){
                history.addAnswer(card.getWord(), card.getColor());
                useCard(card);
                return prepareClickResult(player, card, true);
            } else {
                return prepareClickResult(player, card, false);
            }
        }
        return null;
    }

    private ClickResult prepareClickResult(Player player, Card card, boolean allAnswerd) {
        ClickResult.ClickCorrectness correctness = allAnswerd ? getCorrectness(card, player.getTeam()) : ClickResult.ClickCorrectness.NONE;
        List<Card> updatedCards = board.getAnswerManager().popCardsToUpdate(player);
        return new ClickResult(correctness, updatedCards, card);
    }

    private boolean canClick(Player player, Card card){
        return isPlayerTurn(player) && !card.isChecked();
    }

    private boolean isPlayerTurn(Player player){
        return player.getTeam() == state.getCurrentTeam()
                && player.getRole() == state.getCurrentStage();
    }

    private boolean allPlayersAnswer(Card card, Team team){
        int answers = board.getAnswerManager().getCounter(card);
        return answers == players.getTeamSize(team) - 1;
    }

    private ClickResult.ClickCorrectness getCorrectness(Card card, Team team){
        if((card.getColor() == CardColor.BLUE) && team == Team.BLUE || card.getColor() == CardColor.RED && team == Team.RED){
            return ClickResult.ClickCorrectness.CORRECT;
        } else if(card.getColor() == CardColor.KILLER){
            return ClickResult.ClickCorrectness.KILLER;
        } else if(card.getColor() == CardColor.NEUTRAL){
            return ClickResult.ClickCorrectness.INCORRECT;
        } else {
            return ClickResult.ClickCorrectness.INCORRECT;
        }
    }

    public Card flag(int cardId, Player player){
        Card card = board.getCard(cardId);
        if(canFlag(card)){
            board.getFlagsManager().addFlag(player, card); // TODO: przecież to może zwracać zmodyfikowane wartości. Dlaczego tego nie robi?
            return card;
        }
        return null;
    }

    private boolean canFlag(Card card){
        return !card.isChecked();
    }

    public boolean setQuestion(QuestionParam question, String sessionId){
        Player player = players.getPlayer(sessionId);
        if(isPlayerTurn(player)){
            if(WordValidator.validate(question.getQuestion())){
                state.setAnswerState(question.getQuestion(), question.getNumber());
                history.addQuestion(question.getQuestion(), question.getNumber(), player.getTeam());
                return true;
            }
        }
        return false;
    }

}
