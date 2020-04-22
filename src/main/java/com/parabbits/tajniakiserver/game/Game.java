package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.boss.BossController;
import com.parabbits.tajniakiserver.boss.VotingService;
import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.words_utils.WordColorRandom;
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
    private final GameState state = new GameState();
    private final Board board = new Board();
    // TODO: dodać oddzielną klasę do zarządzania graczami w grze

    public GameState getState(){
        return state;
    }

    public Team getFirstTeam(){
        return firstTeam;
    }

    public Board getBoard(){
        return board;
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

    public int getTeamSize(Team team){
        return getPlayers(team).size();
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
        if(firstTeam == null){
            firstTeam = randomFirstGroup();
            board.init(firstTeam);
            state.initState(firstTeam, BoardCreator.WORDS_IN_FIRST_TEAM);
        }
    }

    private Team randomFirstGroup(){
        int randValue = new Random().nextInt(100);
        return randValue < 50 ? Team.BLUE : Team.RED;
    }
}
