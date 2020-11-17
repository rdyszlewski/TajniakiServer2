package com.parabbits.tajniakiserver.lobby.manager;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameSettings;
import com.parabbits.tajniakiserver.shared.game.GameStep;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class Lobby {

    /** Map of all players in lobby. The key is session id of player.**/
    private Map<String, LobbyPlayer> players = new ConcurrentHashMap<>();
    /** Game, with witch this lobby is connected. Game can have only one lobby. Every lobby must have assigned game.**/
    final private Game game;
    /** Counter of players ids in this lobby. When is added new players to this lobby, this value is increase
     * Variable has type LongAdder, to prevent issues related to multithreading**/
    private LongAdder currentPlayerId = new LongAdder();

    public Lobby(Game game){
        this.game = game;
    }

    /**
     * Adds player to the lobby. Method assigns id to player, and places it on the list.
     * @param player the player to be added
     * @param sessionId session id of added player
     * @return added player, with assigned id
     */
    public LobbyPlayer addPlayer(final LobbyPlayer player,final String sessionId){
        player.setId(currentPlayerId.intValue());
        currentPlayerId.increment();
        players.put(sessionId, player);
        return player;
    }

    /**
     * Adds player to the lobby. Method creates new player with given nickname, and places it in lobby.
     * @param nickname name of created player
     * @param sessionId session id of created player
     * @return created player
     */
    public LobbyPlayer addPlayer(String nickname, String sessionId){
        if(!players.containsKey(sessionId)){
            LobbyPlayer player = new LobbyPlayer(sessionId, nickname);
            player.setId(currentPlayerId.intValue());
            currentPlayerId.increment();
            player.setTeam(Team.LACK);
            players.put(sessionId, player);
            return player;
        }
        return null;
    }

    public void removePlayer(String sessionId){
        if(players.containsKey(sessionId)){
            players.remove(sessionId);
            game.getPlayers().removePlayer(sessionId);
        }
    }

    public boolean containPlayer(String sessionId){
        return players.containsKey(sessionId);
    }

    public LobbyPlayer getPlayer(String sessionId){
        if (players.containsKey(sessionId)) {
            return players.get(sessionId);
        }
        return null;
    }

    /**
     * Return number of players in lobby
     * @return number of players in lobby
     */
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

    public void reset(){
        players.values().forEach(x->x.setReady(false));
    }
}
