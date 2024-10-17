package model.database;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class DatabaseConnector {

    public static Connection connect() throws SQLException {
        Preferences preferences = Preferences.userRoot().node("volleyball-analyzer");
        String databasePath = preferences.get("database", "jdbc:sqlite::resource:database.db");
        try {
            Connection conn = DriverManager.getConnection(databasePath);
            System.out.println("Connection to database: " + databasePath + " has been established.");
            return conn;
        } catch (SQLException exception) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Can't connect to database");
            alert.setHeaderText("Invalid database file");
            alert.setContentText("Try changing database!");
            alert.showAndWait();
            throw exception;
        }
    }

    public static void disconnect(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Connection to SQLite closed.");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}