package com.parabbits.tajniakiserver.game;

import com.google.gson.Gson;

import com.parabbits.tajniakiserver.game.messages.AnswerResult;
import com.parabbits.tajniakiserver.game.messages.BossMessage;
import com.parabbits.tajniakiserver.game.messages.ClickMessage;
import com.parabbits.tajniakiserver.game.messages.StartGameMessage;
import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class GameController{

    private final String START_MESSAGE_RESPONSE = "/queue/game/start";
    private final String ANSWER_MESSAGE_RESPONSE = "/queue/game/answer";
    private final String CLICK_MESSAGE_RESPONSE = "/queue/game/click";
    private final String QUESTION_MESSAGE_RESPONSE = "/queue/game/question";
    @Autowired
    private Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    public void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    @MessageMapping("/game/start")
    public void startGame(@Payload  String nickname, SimpMessageHeaderAccessor headerAccessor) throws Exception{
        game.initializeGame();

        // TODO: w jakiś sposób kontrolować, że wszyscy pobrali wiadomośc i można rozpocząć grę
        Player player = game.getPlayer(headerAccessor.getSessionId());
        if(player.getRole() == Role.BOSS){
            StartGameMessage bossMessage = createStartGameMessage(Role.BOSS, player.getTeam());
            messageManager.send(bossMessage, player.getSessionId(), START_MESSAGE_RESPONSE);
        } else {
            StartGameMessage playersMessage = createStartGameMessage(Role.PLAYER, player.getTeam());
            messageManager.send(playersMessage, player.getSessionId(), START_MESSAGE_RESPONSE);
        }
    }

    // TODO: można przenieść do oodzielnej klasy
    private StartGameMessage createStartGameMessage(Role role, Team team){
        StartGameMessage message = new StartGameMessage();
        message.setPlayerRole(role);
        message.setGameState(game.getState());
        List<ClientCard> cards = ClientCardCreator.createCards(game.getBoard().getCards(role), game, role, team);
        message.setCards(cards);
        return message;
    }

    @MessageMapping("/game/click")
    public void servePlayersAnswer(@Payload String word, SimpMessageHeaderAccessor headerAccessor){
        // TODO: przerobić to na kliknięcie
        Player player = game.getPlayer(headerAccessor.getSessionId());
//        if(!isPlayerTurn(player)){ // TODO: odkomentować to
//            return;
//        }
        Card card = game.getBoard().getCard(word);
        game.getBoard().getAnswerManager().setAnswer(card, player);
        // TODO: trzeba to skrócić. Czy wszyscy odpowiedzieli tak samo
        int answerForCard = game.getBoard().getAnswerManager().getCounter(card);
        System.out.println(answerForCard);
        if(answerForCard == game.getTeamSize(player.getTeam())- 1){
            handleAnswerMessage(player, card);
        } else {
            handleClickMessage(player);
        }
//        if(game.getBoard().getAnswerManager().isValidAnswer(game.getTeamSize(player.getTeam())-1)){
//            handleAnswerMessage(player, card);
//        } else {
//            handleClickMessage(player);
//        }
    }

    private void handleAnswerMessage(Player player, Card card) {
        AnswerCorrectness.Correctness correctness = AnswerCorrectness.checkCorrectness(card.getColor(), player.getTeam());
        AnswerResult answerResult;
        switch(correctness){
            case CORRECT:
                answerResult = buildAnswerMessage(card, true);
                messageManager.sendToAll(answerResult, ANSWER_MESSAGE_RESPONSE, game);
            case INCORRECT:
                // TODO: poinformowanie gry o błędnym wyborze
                game.getState().nextTeam(); // TODO: to raczej nie powinno być w tym miejscu
                answerResult = buildAnswerMessage(card, false);
                messageManager.sendToAll(answerResult, ANSWER_MESSAGE_RESPONSE, game);
                break;
            case KILLER:
                // TODO: wysłać komunikat o końcu gry
                break;

        }
    }

    private void handleClickMessage(Player player){
        System.out.println("Wysyłanie informacji o kliknięciu");
        List<Card> editedCards = game.getBoard().getAnswerManager().popCardsToUpdate();
        List<ClientCard> clientCards = prepareClientCards(editedCards, player);
        ClickMessage message = new ClickMessage(clientCards);
        messageManager.sendToRoleFromTeam(message, Role.PLAYER, player.getTeam(), CLICK_MESSAGE_RESPONSE, game);
    }

    private List<ClientCard> prepareClientCards(List<Card> cards, Player player){
        List<ClientCard> clientCards = new ArrayList<>();
        for(Card card: cards){
            ClientCard clientCard = ClientCardCreator.createCard(card, game, player.getRole(), player.getTeam());
            clientCards.add(clientCard);
        }
        return clientCards;
    }

    private AnswerResult buildAnswerMessage(Card card, boolean correct) {
        AnswerResult result = new AnswerResult(card.getWord(), card.getColor());
        result.setCorrect(correct);
        game.getState().useCard(card.getColor());
        card.setChecked(true);

        result.setGameState(game.getState());
        return result;
    }

    private boolean isPlayerTurn(Player player){
        return player.getTeam() == game.getState().getCurrentTeam() && player.getRole() == game.getState().getCurrentStage();
    }

    @MessageMapping("/game/question")
    public void setQuestion(@Payload String messsageText, SimpMessageHeaderAccessor headerAccessor){
        // TODO: można dodać jakąś historię podawanych haseł
        // TODO: pomyśleć, co będzie w przypadku, jeżeli szef nie zdąży podać hasła w wyznaczonym terminie
        Player player = game.getPlayer(headerAccessor.getSessionId());
        if(player.getRole()==Role.PLAYER || player.getTeam() != game.getState().getCurrentTeam()){
            return;
        }
        Gson gson = new Gson();
        BossMessage message = buildBossMessage(messsageText, gson);
        messageManager.sendToAll(message, QUESTION_MESSAGE_RESPONSE, game);

    }

    private BossMessage buildBossMessage(@Payload String messsageText, Gson gson) {
        BossMessage message = gson.fromJson(messsageText, BossMessage.class);
        game.getState().setAnswerState(message.getWord(),message.getNumber());
        message.setGameState(game.getState());
        return message;
    }

    // TODO: zaznaczanie słowa
    // TODO: wybieranie słowa

    @MessageMapping("/game/select")
    public void selectCard(@Payload String word, SimpMessageHeaderAccessor headerAccessor){
        Player player = game.getPlayer(headerAccessor.getSessionId());
        // TODO: daj gre, daj plansze, zaznacz
    }

}