package model.database.dao;

import model.database.DatabaseConnector;
import model.database.entity.Formation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FormationDaoImpl implements FormationDao {

    private Connection connection;

    @Override
    public void insertFormation(Formation formation) {
        String sql = "INSERT INTO Formation(gameID, actionNr, h1, h2, h3, h4, h5, h6, v1, v2, v3, v4, v5, v6) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            connection = DatabaseConnector.connect();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, formation.getGameID());
            pstmt.setInt(2, formation.getActionNr());
            pstmt.setInt(3, formation.getH1());
            pstmt.setInt(4, formation.getH2());
            pstmt.setInt(5, formation.getH3());
            pstmt.setInt(6, formation.getH4());
            pstmt.setInt(7, formation.getH5());
            pstmt.setInt(8, formation.getH6());
            pstmt.setInt(9, formation.getV1());
            pstmt.setInt(10, formation.getV2());
            pstmt.setInt(11, formation.getV3());
            pstmt.setInt(12, formation.getV4());
            pstmt.setInt(13, formation.getV5());
            pstmt.setInt(14, formation.getV6());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DatabaseConnector.disconnect(connection);
    }
}
