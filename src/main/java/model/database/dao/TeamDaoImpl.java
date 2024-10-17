package model.database.dao;

import model.database.DatabaseConnector;
import model.database.DatabaseCreator;
import model.database.entity.Team;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class TeamDaoImpl implements TeamDao {

    private Connection connection;

    @Override
    public long insertTeam(Team team) {
        String sql = "INSERT INTO Team(name, season) VALUES (?,?)";
        long id = -1;
        try {
            connection = DatabaseConnector.connect();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, team.getName());
            pstmt.setString(2, team.getSeason());
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
    public long getTeamId(String teamName, String season) {
        String sql = "SELECT id FROM Team WHERE name=? AND season=?";
        long id = -1;
        try {
            connection = DatabaseConnector.connect();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, teamName);
            pstmt.setString(2, season);
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
    public Team getTeam(long id) {
        String sql = "SELECT * FROM Team WHERE id=" + id;
        try {
            connection = DatabaseConnector.connect();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                Team team = new Team(rs.getString("name"), rs.getString("season"));
                team.setId(rs.getLong("id"));
                rs.close();
                DatabaseConnector.disconnect(connection);
                return team;
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DatabaseConnector.disconnect(connection);
        return null;
    }

    @Override
    public List<Team> getAllTeams() {
        String sql = "SELECT * FROM Team";
        List<Team> teams = new ArrayList<>();
        try {
            connection = DatabaseConnector.connect();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Team team = new Team(rs.getString("name"), rs.getString("season"));
                team.setId(rs.getLong("id"));
                teams.add(team);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().contains("no such table")) {
                Preferences preferences = Preferences.userRoot().node("volleyball-analyzer");
                String databasePath = preferences.get("database", "jdbc:sqlite::resource:database.db");
                DatabaseConnector.disconnect(connection);
                new File(databasePath.substring(12)).delete();
                try {
                    DatabaseCreator.createNewDatabase(databasePath.substring(12));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        DatabaseConnector.disconnect(connection);
        return teams;
    }
}
