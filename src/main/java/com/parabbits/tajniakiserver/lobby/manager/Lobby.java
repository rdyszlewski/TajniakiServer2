package com.parabbits.tajniakiserver.lobby.manager;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameSettings;
import com.parabbits.tajniakiserver.shared.game.GameStep;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Lobby {

    private Map<String, LobbyPlayer> players = new HashMap<>();
    private int currentId = 0;
    private Game game;

    public Lobby(Game game){
        this.game = game;
    }

    public LobbyPlayer addPlayer(LobbyPlayer player, String sessionId){
        players.put(sessionId, player);
        return player;
    }

    public LobbyPlayer addPlayer(String nickname, String sessionId){
        if(!players.containsKey(sessionId)){
            LobbyPlayer player = new LobbyPlayer();
            player.setId(currentId);
            player.setSessionId(sessionId);
            player.setNickname(nickname);
            currentId++;
            return addPlayer(player, sessionId);
        }
        return null;
    }

    public LobbyPlayer getPlayer(String sessionId){
        if (players.containsKey(sessionId)) {
            return players.get(sessionId);
        }
        return null;
    }

    public int getPlayersCount(){
        return players.size();
    }

    public boolean isFull(){
        return getPlayersCount() == game.getSettings().getMaxTeamSize() * 2;
    }

    public Collection<LobbyPlayer> getPlayers(){
        return players.values();
    }

    public boolean changeTeam(LobbyPlayer player, Team team){
        if(canChangeTeam(team)){
            player.setTeam(team);
            return true;
        }
        return false;
    }

    private boolean canChangeTeam(Team team){
        if(team == Team.LACK){
            return true;
        }
        return getTeamSize(team) < game.getSettings().getMaxTeamSize();
    }

    private long getTeamSize(Team team){
        return players.values().stream().filter(x->x.getTeam() == team).count();
    }

    public boolean setReady(LobbyPlayer player){
        if(player.isReady()){
            player.setReady(false);
            return true;
        } else {
            if(canSetReady(player)){
                player.setReady(true);
                return true;
            }
        }
        return false;
    }

    private boolean canSetReady(LobbyPlayer player){
        return (player.getTeam() == Team.BLUE || player.getTeam() == Team.RED);
    }

    public boolean autoChangeTeam(LobbyPlayer player){
        Team team = getSmallerTeam();
        if(team != player.getTeam()){
            return changeTeam(player, team);
        }
        return false;
    }

    private Team getSmallerTeam(){
        long blueSize = getTeamSize(Team.BLUE);
        long redSize = getTeamSize(Team.RED);
        return blueSize <= redSize? Team.BLUE: Team.RED;
    }

    public boolean canStart(){
        return isAllReady() && isMinimumPlayers();
    }

    private boolean isAllReady(){
        return players.values().stream().allMatch(LobbyPlayer::isReady);
    }

    private boolean isMinimumPlayers(){
        long blueSize = getTeamSize(Team.BLUE);
        long redSize = getTeamSize(Team.RED);
        int minSize = game.getSettings().getMinTeamSize();
        return blueSize >= minSize && redSize >= minSize;
    }

    public GameStep getGameState(){
        return game.getState().getCurrentStep();
    }

    public GameSettings getSettings(){
        return game.getSettings();
    }

    public UUID getID(){
        return game.getID();
    }

    public void startGame() throws IOException {
        // TODO: wstawić wszystkie funkcje
        List<Player> playersList = players.values().stream().map(LobbyPlayerAdapter::createPlayer).collect(Collectors.toList());
        game.startGame(playersList);
    }
}
