package model.database.dao;

import model.database.DatabaseConnector;
import model.database.entity.Game;
import model.database.entity.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDaoImpl implements GameDao {

    private Connection connection;

    @Override
    public long insertGame(Game game) {
        String sql = "INSERT INTO Game(date, hTeamID, vTeamID, result, season, category, subcategory, winner) VALUES (?,?,?,?,?,?,?,?)";
        int id = -1;
        try {
            connection = DatabaseConnector.connect();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, game.getDate());
            pstmt.setLong(2, game.getHomeTeam().getId());
            pstmt.setLong(3, game.getOpponentTeam().getId());
            pstmt.setString(4, game.getResult());
            pstmt.setString(5, game.getSeason());
            pstmt.setString(6, game.getCategory());
            pstmt.setString(7, game.getSubcategory());
            pstmt.setInt(8, game.getWinner());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DatabaseConnector.disconnect(connection);
        return id;
    }

    @Override
    public long getGameId(Game game) {
        String sql = "SELECT id FROM Game WHERE hTeamID=? AND vTeamID=? AND result=?";
        long id = -1;
        try {
            connection = DatabaseConnector.connect();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, game.getHomeTeam().getId());
            pstmt.setLong(2, game.getOpponentTeam().getId());
            pstmt.setString(3, game.getResult());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong("id");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DatabaseConnector.disconnect(connection);
        return id;
    }

    @Override
    public List<Game> getAllGames(long teamID) {
        String sql = "SELECT * FROM Game";
        if (teamID != -1) {
            sql += " WHERE hTeamID=" + teamID + " OR vTeamID=" + teamID;
        }
        sql += " ORDER BY date";
        List<Game> games = new ArrayList<>();
        try {
            connection = DatabaseConnector.connect();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                long hTeamId = rs.getLong("hTeamID");
                long vTeamId = rs.getLong("vTeamID");
                TeamDao teamDao = DaoManager.getInstance().getTeamDao();
                Team homeTeam = teamDao.getTeam(hTeamId);
                Team opponentTeam = teamDao.getTeam(vTeamId);
                Game game = new Game(rs.getString("date"), homeTeam, opponentTeam, rs.getString("result"));
                game.setId(rs.getLong("id"));
                games.add(game);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DatabaseConnector.disconnect(connection);
        return games;
    }

    @Override
    public void deleteGames(List<Game> gamesToDelete) {
        StringBuilder sql = new StringBuilder("DELETE FROM Game WHERE id IN (");
        sql.append(gamesToDelete.get(0).getId());
        for (int i = 1; i < gamesToDelete.size(); i++) {
            sql.append(", ").append(gamesToDelete.get(i).getId());
        }
        sql.append(")");
        try {
            connection = DatabaseConnector.connect();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql.toString());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DatabaseConnector.disconnect(connection);
    }
}
