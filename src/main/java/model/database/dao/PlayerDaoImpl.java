package model.database.dao;

import model.database.DatabaseConnector;
import model.database.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDaoImpl implements PlayerDao {

    private Connection connection;

    @Override
    public long insertPlayer(Player player) {
        String sql = "INSERT INTO Player(name, surname, position, number, teamID) VALUES (?,?,?,?,?)";
        int id = -1;
        try {
            connection = DatabaseConnector.connect();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, player.getName());
            pstmt.setString(2, player.getSurname());
            pstmt.setInt(3, player.getPosition());
            pstmt.setInt(4, player.getNumber());
            pstmt.setLong(5, player.getTeamId());
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
    public long getPlayerId(long teamID, int number, String surname) {
        String sql = "SELECT id FROM Player WHERE teamID=? AND number=? AND surname=?";
        long id = -1;
        try {
            connection = DatabaseConnector.connect();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, teamID);
            pstmt.setInt(2, number);
            pstmt.setString(3, surname);
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
    public Player getPlayer(long id) {
        String sql = "SELECT * FROM Player WHERE id=" + id;
        try {
            connection = DatabaseConnector.connect();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                Player player = new Player(rs.getLong("id"), rs.getString("name"),
                        rs.getString("surname"), rs.getInt("position"), rs.getLong("teamID"));
                rs.close();
                DatabaseConnector.disconnect(connection);
                return player;
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DatabaseConnector.disconnect(connection);
        return null;
    }

    @Override
    public List<Player> getAllPlayersFromTeam(long teamID) {
        String sql = "SELECT * FROM Player WHERE teamID=?";
        List<Player> players = new ArrayList<>();
        try {
            connection = DatabaseConnector.connect();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, teamID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Player player = new Player(rs.getString("name"), rs.getString("surname"),
                        rs.getInt("position"), rs.getInt("number"), teamID);
                player.setId(rs.getLong("id"));
                players.add(player);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DatabaseConnector.disconnect(connection);
        return players;
    }

}
