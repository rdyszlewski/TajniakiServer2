package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.game.*;
import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.tajniakiserver.game.parameters.QuestionParam;
import com.parabbits.tajniakiserver.summary.GameHistory;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Game {

    private final UUID id;
    private final GamePlayersManager players;
    private GameHistory history = new GameHistory();
    private GameSettings settings;
    private GameState state;
    private Board board;

    private Team firstTeam;

    public final UUID getID() {
        return id;
    }

    public final GamePlayersManager getPlayers() {
        return players;
    }

    public Game(UUID id) {
        this.id = id;
        players = new GamePlayersManager(settings);
        reset();
    }

    private boolean started = false;

    public boolean isStarted() {
        return started;
    }

    public void reset() {
        history = new GameHistory();
        firstTeam = null;
        state = new GameState();
        settings = new GameSettings();
        board = new Board();
        started = false;

        getState().setCurrentStep(GameStep.LOBBY);
    }

    public GameState getState() {
        return state;
    }

    /**
     * Starts new game. All values are reset. Initializes all values.
     *
     * @param playersList players, who will participate in the game
     * @throws IOException can be throws, during reading words
     */
    public void startGame(List<Player> playersList) throws IOException {
        playersList.forEach(players::addPlayer);
        if (firstTeam == null) {
            firstTeam = randomFirstGroup();
            board.init(firstTeam, settings);
            state.initState(firstTeam, settings.getFirstTeamWords());
        }
        initHistory();
        started = true;
    }

    private void initHistory() {
        history.setWords(getWords(CardColor.BLUE), Team.BLUE);
        history.setWords(getWords(CardColor.RED), Team.RED);
        history.setKiller(getWords(CardColor.KILLER).get(0));
    }

    private List<String> getWords(CardColor cardColor) {
        return board.getCards().stream().filter(card -> card.getColor() == cardColor).map(Card::getWord).collect(Collectors.toList());
    }

    private Team randomFirstGroup() {
        int randValue = new Random().nextInt(100);
        return randValue < 50 ? Team.BLUE : Team.RED;
    }

    public UseCardResult useCard(Card card) {
        board.getAnswerManager().reset();
        board.getFlagsManager().removeFlags(card);
        return state.useCard(card);
    }

    public GameSettings getSettings() {
        return settings;
    }

    public GameHistory getHistory() {
        return history;
    }

    public ClickResult click(int cardId, Player player) {
        Card card = board.getCard(cardId);
        if (canClick(player, card) && state.isGameActive()) {
            List<Card> editedCards = board.getAnswerManager().setAnswer(card, player);
            if (allPlayersAnswer(card, player.getTeam())) {
                history.addAnswer(card.getWord(), card.getColor());
                UseCardResult result = useCard(card);
                UseCardType type = isEndGame(result) ? UseCardType.END_GAME : UseCardType.ANSWER;
                return prepareClickResult(card, editedCards, type, result);
            } else {
                return prepareClickResult(card, editedCards, UseCardType.CLICK, null);
            }
        }
        return null;
    }

    private boolean isEndGame(UseCardResult result) {
        return result == UseCardResult.LAST_CORRECT || result == UseCardResult.LAST_INCORRECT || result == UseCardResult.KILLER;
    }

    private ClickResult prepareClickResult(Card card, List<Card> updatedCards, UseCardType type, UseCardResult result) {
        ClickCorrectness correctness = type != UseCardType.CLICK && result != null
                ? UseCardResult.getCorrectness(result) : null;
        // TODO: czy to musi być tutaj
        return new ClickResult(type, correctness, updatedCards, card);
    }

    private boolean canClick(Player player, Card card) {
        return isPlayerTurn(player) && !card.isChecked();
    }

    private boolean isPlayerTurn(Player player) {
        return player.getTeam() == state.getCurrentTeam()
                && player.getRole() == state.getCurrentStage();
    }

    private boolean allPlayersAnswer(Card card, Team team) {
        int answers = board.getAnswerManager().getCounter(card);
        return answers == players.getTeamSize(team) - 1;
    }

    public Card flag(int cardId, Player player) {
        Card card = board.getCard(cardId);
        if (canFlag(card)) {
            board.getFlagsManager().addFlag(player, card);
            return card;
        }
        return null;
    }

    private boolean canFlag(Card card) {
        return !card.isChecked();
    }

    public boolean setQuestion(QuestionParam question, String sessionId) {
        Player player = players.getPlayer(sessionId);
        if (isPlayerTurn(player)) {
            if (WordValidator.validate(question.getQuestion())) {
                state.setAnswerState(question.getQuestion(), question.getNumber());
                history.addQuestion(question.getQuestion(), question.getNumber(), player.getTeam());
                return true;
            }
        }
        return false;
    }

    public List<Card> getCards() {
        return board.getCards();
    }

    public List<Card> getCards(boolean withoutPass) {
        if (withoutPass) {
            return board.getCards().stream().filter(x -> x.getId() >= 0).collect(Collectors.toList());
        }
        return getCards();
    }

    public Set<Player> getPlayersWhoClicked(Card card) {
        return board.getAnswerManager().getPlayers(card);
    }

    public Set<Player> getFlagsOwner(Card card, Team team) {
        return board.getFlagsManager().getFlagsOwners(card, team);
    }
}
