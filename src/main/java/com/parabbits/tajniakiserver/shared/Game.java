package com.parabbits.tajniakiserver.shared;

import com.parabbits.tajniakiserver.summary.GameHistory;
import com.parabbits.tajniakiserver.voting.VotingController;
import com.parabbits.tajniakiserver.voting.VotingService;
import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Game {

    // TODO: pomyśleć, jak rozwiązać to inaczej
    @Autowired
    private VotingController bossController;

    private final Map<String, Player> players = new HashMap<>();

    private  VotingService blueVoting = new VotingService(Team.BLUE);
    private  VotingService redVoting = new VotingService(Team.RED);
    private  GameHistory history = new GameHistory();
    private boolean votingTimerStared = false; // TODO: przemyśleć, czy to powinno być w tym miejscu

    private Team firstTeam;
    private  GameState state = new GameState();
    private  GameSettings settings = new GameSettings();
    private  Board board = new Board();
    private int playerCounter = 0;


    // =====================================================
    //  zbiór, który może być wykorzystywany np. do zliczania, ilu graczy wykonało jakąś akcję. Np. ile osób wyświetliło podsumowanie
    // TODO: będzie można przenieść to do klasy zarządzającej graczami
    private final Set<Long> usedPlayers = new HashSet<>();

    public void usePlayer(Player player){
        usedPlayers.add(player.getId());
    }

    public void removeUsePlayer(Player player){
        usedPlayers.remove(player.getId());
    }

    public boolean areAllPlayerUsed(){
        return usedPlayers.size() == players.size();
    }
    // =====================================================

    // TODO: dodać oddzielną klasę do zarządzania graczami w grze

    private boolean started = false;

    public boolean isStarted(){
        return started;
    }

    public boolean isVotingTimerStarted(){
        return votingTimerStared;
    }

    public void setVotingTimerStarted(boolean started){
        votingTimerStared = started;
    }

    public void reset(){
        players.clear();
        blueVoting = new VotingService(Team.BLUE);
        redVoting = new VotingService(Team.RED);
        history = new GameHistory();
        firstTeam = null;
        state = new GameState();
        settings = new GameSettings();
        board = new Board();
        playerCounter = 0;
        started = false;
    }

    public GameState getState(){
        return state;
    }

    public Team getFirstTeam(){
        return firstTeam;
    }

    public Board getBoard(){
        return board;
    }


    // TODO: zobaczyć, czym różnią się te 2 metody
    public Player addPlayer(String sessionId, String nickname){
        if (players.size() < settings.getMaxTeamSize()*2){
            Player player = new Player(sessionId, nickname);
            addPlayer(player);
            return player;
        }
        return null;
    }

    public synchronized void addPlayer(Player player){
        player.setId(playerCounter);
        System.out.println("Id gracza " + player.getId());
        playerCounter++;
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
            Player player = players.get(sessionId);
            removeUsePlayer(player);
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
//            startTimer();
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
            board.init(firstTeam, settings);
            state.initState(firstTeam, settings.getFirstTeamWords());
            state.setCurrentStep(GameStep.MAIN); // TODO: sprawdzić, czy ten stan na początek jest ok
        }
        started = true;
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
