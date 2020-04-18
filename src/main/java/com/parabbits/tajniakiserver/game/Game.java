package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.boss.BossController;
import com.parabbits.tajniakiserver.boss.VotingService;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.game.models.WordColor;
import com.parabbits.words_utils.TeamsRandom;
import com.parabbits.words_utils.WordsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Game {

    private final String WORDS_PATH = "resources/words.txt";
    // TODO: pomyśleć, jak rozwiązać to inaczej
    @Autowired
    private BossController bossController;

    public static final int MAX_PLAYERS = 10;

    private final Map<String, Player> players = new HashMap<>();

    private final VotingService blueVoting = new VotingService(Team.BLUE);
    private final VotingService redVoting = new VotingService(Team.RED);

    private Team firstTeam;
    private String[] words;
    private WordColor[] wordsColor;

    private GameState state = new GameState();


    public GameState getState(){
        return state;
    }

    public Team getFirstTeam(){
        return firstTeam;
    }

    public String[] getWords(){
        return words;
    }

    public WordColor[] getWordsColors(){
        return wordsColor;
    }

    public Player addPlayer(String sessionId, String nickname){
        if (players.size() < MAX_PLAYERS){
            System.out.println("Dodanie gracza");
            Player player = new Player(sessionId, nickname);
            players.put(sessionId, player);
            return player;
        }
        return null;
    }

    public void addPlayer(Player player){
        players.put(player.getSessionId(), player);
    }

    public void testPrint(){
        for(Map.Entry<String, Player> entry: players.entrySet()){
            Player player = entry.getValue();
            System.out.println(player.getSessionId() + "  " + player.getNickname());
        }
    }

    public void removePlayer(String sessionId){
        System.out.println(players.size());
        if (players.containsKey(sessionId)){
            System.out.println("Taki gracz jest podłączony do gry");
            players.remove(sessionId);
        }
    }

    public List<Player> getPlayers(){
        return new ArrayList<>(players.values());
    }

    public Player getPlayer(String sessionId){
        if(players.containsKey(sessionId)){
            return players.get(sessionId);
        }
        return null;
    }

    public void startVoting(){
        if(blueVoting.getCandidates().isEmpty()){
            blueVoting.init(players);
            redVoting.init(players);
            // TODO: z uruchomieniem licznika można poczekać, aż wszyscy się załadują
            startTimer();
        }
    }

    private void startTimer(){
        // TODO: można zrobić klasę Timer, która będzie znajdować się w Game. Kontrolery będą mogły z niego korzystać.
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        bossController.endVoting();
                        // TODO: można zrobić wysyłanie czasu do klientów co sekunde
                    }
                },
                10000
        );
    }
    
    public VotingService getVotingService(Team team){
        return team==Team.BLUE?blueVoting: redVoting;
    }

    public List<Player> getPlayers(Team team){
        // TODO: można dodać 2 listy do zmiennych, graczy w niebieskiej i czerwonej
        return players.values().stream().filter(x->x.getTeam().equals(team)).collect(Collectors.toList());
    }

    public Player getPlayerById(long id){
        for(Player player: players.values()){
            if(player.getId() == id){
                return player;
            }
        }
        return null;
    }

    public void initializeGame() throws IOException {
        final int NUMBER_OF_WORDS = 25;
        final int WORDS_IN_FIRST_TEAM = 9;
        if(firstTeam == null){
            firstTeam = randomFirstGroup();
            words = getRandomWords();
            wordsColor = TeamsRandom.randomTeamsForWords(NUMBER_OF_WORDS, WORDS_IN_FIRST_TEAM, firstTeam);
            state.initState(firstTeam, WORDS_IN_FIRST_TEAM);
        }
    }

    private Team randomFirstGroup(){
        int randValue = new Random().nextInt(100);
        return randValue < 50 ? Team.BLUE : Team.RED;
    }

    private String[] getRandomWords() throws IOException {
        List<String> allWords = WordsHelper.readWords(WORDS_PATH);
        List<String> randomWords = WordsHelper.randomWords(allWords, 25);
        return randomWords.toArray(new String[0]);
    }

    public WordColor getWordColor(String word){
        int index = getWordIndex(word);
        WordColor color = wordsColor[index];
        return color;
    }

    private int getWordIndex(String word){
        for(int i=0; i<words.length; i++){
            if(words[i].equals(word)){
                return i;
            }
        }
        return -1;
    }






}
