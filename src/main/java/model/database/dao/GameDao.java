package model.database.dao;

import model.database.entity.Game;

import java.util.List;

public interface GameDao {

    long insertGame(Game game);

    long getGameId(Game game);

    List<Game> getAllGames(long teamID);

    void deleteGames(List<Game> gamesToDelete);
}
