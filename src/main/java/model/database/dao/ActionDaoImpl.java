package model.database.dao;

import model.database.DatabaseConnector;
import model.database.entity.Action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActionDaoImpl implements ActionDao {

    private Connection connection;

    @Override
    public void insertAction(Action action) {
        String sql = "INSERT INTO Action(gameID, playerID, nr, setNr, hPoints, vPoints, setPhase, serveSide, actionSide, skill, type," +
                "quality, setterCmb, attackCmb, trg, startZone, endZone, endZoneExt, skillType, players, special, time, hSetterPosition, vSetterPosition, " +
                "recDefQ, setQ, setPosition, setZone, attackDirection, lastSetTrg, lastSetZone, recPlayerID, setToWhichPlayerID, attackAttempt, playerAttackAttempt, sideOutAttempt, point) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            connection = DatabaseConnector.connect();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            //Mandatory
            pstmt.setLong(1, action.getGameId());
            pstmt.setLong(2, action.getPlayerId());
            pstmt.setLong(3, action.getNr());
            pstmt.setInt(4, action.getSetNr());
            pstmt.setInt(5, action.getHomeTeamPoints());
            pstmt.setInt(6, action.getOpponentTeamPoints());
            pstmt.setString(7, action.getSetPhase());
            pstmt.setString(8, action.getServeSide());
            pstmt.setString(9, action.getActionSide());
            pstmt.setString(10, action.getSkill());
            pstmt.setString(11, action.getType());
            pstmt.setString(12, action.getQuality());
            //Non-mandatory
            pstmt.setString(13, action.getSetterCmb());
            pstmt.setString(14, action.getAttackCmb());
            pstmt.setString(15, action.getTrg());
            pstmt.setString(16, action.getStartZone());
            pstmt.setString(17, action.getEndZone());
            pstmt.setString(18, action.getEndZoneExt());
            pstmt.setString(19, action.getSkillType());
            pstmt.setString(20, action.getPlayers());
            pstmt.setString(21, action.getSpecial());
            pstmt.setString(22, action.getTime());
            //Mandatory
            pstmt.setString(23, action.getHomeSetterPosition());
            pstmt.setString(24, action.getOpponentSetterPosition());
            //Non-mandatory
            pstmt.setString(25, action.getRecDefQ());
            pstmt.setString(26, action.getSetQ());
            pstmt.setString(27, action.getSetPosition());
            pstmt.setString(28, action.getSetZone());
            pstmt.setString(29, action.getAttackDirection());
            pstmt.setString(30, action.getLastSetTrg());
            pstmt.setString(31, action.getLastSetZone());
            pstmt.setLong(32, action.getRecPlayerId());
            pstmt.setLong(33, action.getSetToWhichPlayerId());
            //Mandatory
            pstmt.setInt(34, action.getAttackAttempt());
            pstmt.setInt(35, action.getPlayerAttackAttempt());
            pstmt.setString(36, action.getSideOutAttempt());
            pstmt.setString(37, action.getPoint());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DatabaseConnector.disconnect(connection);
    }
}
