package model.database.dao;

import model.database.entity.Player;

import java.util.List;

public interface PlayerDao {

    long insertPlayer(Player player);

    long getPlayerId(long teamID, int number, String surname);

    Player getPlayer(long id);

    List<Player> getAllPlayersFromTeam(long teamID);
}
