package model.database;

import model.database.reader.InstancesReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.prefs.Preferences;

public class DatabaseCreator {

    public static void createNewDatabase(String filePath) throws Exception {
        String databasePath = "jdbc:sqlite:" + filePath;
        Connection conn = DriverManager.getConnection(databasePath);
        if (conn != null) {
            Preferences preferences = Preferences.userRoot().node("volleyball-analyzer");
            preferences.put("database", databasePath);
            createTable(DatabaseCreator.class.getResourceAsStream("tables/Team.schema"));
            createTable(DatabaseCreator.class.getResourceAsStream("tables/Player.schema"));
            createTable(DatabaseCreator.class.getResourceAsStream("tables/Game.schema"));
            createTable(DatabaseCreator.class.getResourceAsStream("tables/Formation.schema"));
            createTable(DatabaseCreator.class.getResourceAsStream("tables/Action.schema"));
            InstancesReader.getInstance().setURL(databasePath);
            System.out.println("A new database has been created.");
        }
    }

    private static void createTable(InputStream inputStream) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String sql = sb.toString();
            Connection connection = DatabaseConnector.connect();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Created table " + inputStream.toString());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
