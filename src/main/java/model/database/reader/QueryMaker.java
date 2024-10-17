package model.database.reader;

import model.database.entity.Game;
import model.database.entity.Player;

import java.util.List;
import java.util.Map;

class QueryMaker {

    private StringBuilder query;
    private List<Game> selectedGames;
    private Attribute positionAttribute = null;
    private Attribute opponentPosAttribute = null;

    void selectGames(List<Game> selectedGames) {
        this.selectedGames = selectedGames;
    }

    String createQuery(Player player, List<Attribute> attributes, Map<String, String> filters) {
        query = new StringBuilder("Select ");
        boolean withPosition = attributes.stream().anyMatch(o -> o.getKey().equals("position"));
        boolean withOpponentPosition = attributes.stream().anyMatch(o -> o.getKey().equals("opponentPosition"));
        boolean withP1 = false;
        boolean withP2 = false;
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            if (attribute.getKey().equals("position")) {
                positionAttribute = attribute;
                continue;
            }
            if (attribute.getKey().equals("opponentPosition")) {
                opponentPosAttribute = attribute;
                continue;
            }
            if (i > 0) {
                query.append(",");
            } else {
                if (attributes.get(0).getKey().equals("quality")) {
                    boolean added = false;
                    for (Map.Entry<String, String> filter : filters.entrySet()) {
                        if (filter.getKey().equals("quality")) {
                            if (filter.getValue().length() == 1) {
                                addQualityFilter(Integer.valueOf(filter.getValue()));
                                query.append(" END '").append(attribute.getValue()).append("'");
                                added = true;
                                break;
                            }
                        }
                    }
                    if (added) {
                        continue;
                    }
                }
            }
            switch (attribute.getKey()) {
                case "attacker":
                    query.append(" p1.surname as '").append(attribute.getValue()).append("'");
                    withP1 = true;
                    break;
                case "receiver":
                case "def/free":
                    query.append(" p2.surname as '").append(attribute.getValue()).append("'");
                    withP2 = true;
                    break;
                default:
                    query.append(" a.").append(attribute.getKey()).append(" as '").append(attribute.getValue()).append("'");
            }
        }
        if (withPosition || withOpponentPosition) {
            if (withPosition) {
                addPositionAttribute(player);
                attributes.remove(positionAttribute);
                attributes.add(positionAttribute);
            }
            if (withOpponentPosition) {
                addOpponentPositionAttribute(player);
                attributes.remove(opponentPosAttribute);
                attributes.add(opponentPosAttribute);
            }
            addFromClauseWithGame();
        } else {
            query.append(" FROM Action a");
        }
        if (withP1) {
            query.append(" LEFT JOIN Player p1 ON a.setToWhichPlayerID = p1.id");
        }
        if (withP2) {
            query.append(" LEFT JOIN Player p2 ON a.recPlayerID = p2.id");
        }
        query.append(" WHERE a.playerID=").append(player.getId());
        addFilters(filters);
        if (selectedGames != null) {
            addSelectedGames();
        }
        return query.toString();
    }

    private void addSelectedGames() {
        if (selectedGames.size() > 0) {
            query.append(" AND gameID IN (");
            query.append(selectedGames.get(0).getId());
            for (int i = 1; i < selectedGames.size(); i++) {
                query.append(", ").append(selectedGames.get(i).getId());
            }
            query.append(")");
        } else {
            query.append(" AND gameID = 0");
        }
    }

    String createNrOfActionsQuery(long playerId) {
        query = new StringBuilder("SELECT COUNT(id) FROM Action WHERE playerID = ").append(playerId);
        return query.toString();
    }

    String createSetsZonesQuery(Player player, Map<String, String> filters) {
        query = new StringBuilder("SELECT a.setZone, COUNT(a.id)");
        if (!filters.containsKey("position")) {
            query.append(" FROM Action a");
        } else {
            addPositionAttribute(player);
        }
        query.append(" WHERE a.skill='E' AND a.playerID=").append(player.getId());
        addFilters(filters);
        if (filters.containsKey("attackAttempt")) {
            if (filters.get("attackAttempt").equals(">1")) {
                query.append(" AND setterCmb<>'KE'");
            }
        }
        query.append(" GROUP BY setZone ORDER BY setZone");
        return query.toString();
    }

    String createMostActionsQuery(String skill) {
        query = new StringBuilder("SELECT Player.id, Player.surname, count(Action.id) as Actions " +
                "FROM Action INNER JOIN Player ON Action.playerID=Player.id " +
                "WHERE Action.skill='" + skill + "'");
        if (selectedGames != null) {
            addSelectedGames();
        }
        query.append(" GROUP BY Action.playerID ORDER BY Actions DESC");
        return query.toString();
    }

    String createAttacksZonesQuery(Player player, String attackCmb) {
        query = new StringBuilder("SELECT endZone, COUNT(id) FROM Action WHERE skill='A' AND playerID=")
                .append(player.getId()).append(" AND attackCmb='").append(attackCmb).append("'").append(" GROUP BY endZone ORDER BY endZone");
        return query.toString();
    }

    String createAttacksSummaryQuery(Player player) {
        query = new StringBuilder("Select sum(case when (attackAttempt=1 AND actionSide<>serveSide) then 1 else 0 end) AfterReception, " +
                " sum(case when (attackAttempt=1 AND actionSide<>serveSide AND quality='#') then 1 else 0 end) AfterReceptionEnded, " +
                " sum(case when (attackAttempt>1 OR actionSide==serveSide) then 1 else 0 end) CounterAttacks, " +
                " sum(case when ((attackAttempt>1 OR actionSide==serveSide) AND quality='#') then 1 else 0 end) CounterAttacksEnded, " +
                " sum(case when (attackCmb LIKE '%X%') then 1 else 0 end) Quick, " +
                " sum(case when (attackCmb LIKE '%X%' AND quality='#') then 1 else 0 end) QuickEnded, " +
                " sum(case when (attackCmb LIKE '%V%') then 1 else 0 end) High, " +
                " sum(case when (attackCmb LIKE '%V%' AND quality='#') then 1 else 0 end) HighEnded " +
                "from Action WHERE skill='A' AND playerID = ").append(player.getId());
        return query.toString();
    }

    String createMiddleBlockersAttacksSummaryQuery(Player player) {
        query = new StringBuilder("Select sum(case when (attackCmb = 'X1' AND quality='#') then 1 else 0 end) X1Ended," +
                " sum(case when (attackCmb = 'X7' AND quality='#') then 1 else 0 end) X7Ended," +
                " sum(case when (attackAttempt=1 AND actionSide<>serveSide) then 1 else 0 end) AfterReception, " +
                " sum(case when (attackAttempt=1 AND actionSide<>serveSide AND quality='#') then 1 else 0 end) AfterReceptionEnded, " +
                " sum(case when (attackAttempt>1 OR actionSide==serveSide) then 1 else 0 end) CounterAttacks, " +
                " sum(case when ((attackAttempt>1 OR actionSide==serveSide) AND quality='#') then 1 else 0 end) CounterAttacksEnded " +
                "from Action WHERE skill='A' AND playerID = ").append(player.getId());
        return query.toString();
    }

    String createReceptionSummaryQuery(Player player) {
        query = new StringBuilder("Select COUNT(id) as 'All', sum(case when type='Q' then 1 else 0 end) Jump, " +
                " sum(case when (type='Q' AND (quality='#' OR quality='+')) then 1 else 0 end) JumpPosPerf, " +
                " sum(case when type<>'Q' then 1 else 0 end) Float, " +
                " sum(case when (type<>'Q' AND (quality='#' OR quality='+')) then 1 else 0 end) FloatPosPerf " +
                "from Action WHERE skill='R' AND playerID = ").append(player.getId());
        return query.toString();
    }

    String createServeSummaryQuery(Player player) {
        query = new StringBuilder("Select COUNT(id) as 'All', sum(case when type='Q' then 1 else 0 end) JumpServ, " +
                " sum(case when (type='Q' AND quality='#') then 1 else 0 end) JumpAces, " +
                " sum(case when (type='Q' AND quality='+') then 1 else 0 end) JumpPlus, " +
                " sum(case when type<>'Q' then 1 else 0 end) FloatServ, " +
                " sum(case when (type<>'Q' AND quality='#') then 1 else 0 end) FloatAces, " +
                " sum(case when (type<>'Q' AND quality='+') then 1 else 0 end) FloatPlus " +
                "from Action WHERE skill='S' AND playerID = ").append(player.getId());
        return query.toString();
    }

    private void addPositionAttribute(Player player) {
        query.append(", CASE g.hTeamID WHEN ").append(player.getTeamId())
                .append(" THEN a.hSetterPosition ELSE a.vSetterPosition END '").append(positionAttribute.getValue()).append("'");
    }

    private void addOpponentPositionAttribute(Player player) {
        query.append(", CASE g.hTeamID WHEN ").append(player.getTeamId())
                .append(" THEN a.vSetterPosition ELSE a.hSetterPosition END '").append(opponentPosAttribute.getValue()).append("'");
    }

    private void addFromClauseWithGame() {
        query.append(" FROM Action a INNER JOIN Game g ON a.gameID=g.id");
    }

    private void addFilters(Map<String, String> filters) {
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            if (filter.getKey().equals("attackAttempt")) {
                switch (filter.getValue()) {
                    case "=1":
                        query.append(" AND attackAttempt=1 AND actionSide<>serveSide");
                        break;
                    case ">1":
                        query.append(" AND (attackAttempt>1 OR (attackAttempt=1 AND actionSide=serveSide))");
                        break;
                    default:
                        query.append(" AND attackAttempt").append(filter.getValue());
                }
            } else if (filter.getKey().equals("quality")) {
                if (filter.getValue().length() > 1) {
                    String[] qualities = filter.getValue().split(",");
                    for (int i = 0; i < qualities.length; i++) {
                        if (i == 0) {
                            query.append(" AND (quality='").append(qualities[i]).append("'");
                        } else {
                            query.append(" OR quality='").append(qualities[i]).append("'");
                        }
                    }
                    query.append(")");
                }
            } else
                query.append(" AND ").append(filter.getKey()).append(filter.getValue());
        }
    }

    private void addQualityFilter(int option) {
        query.append("CASE WHEN a.quality IN ");
        switch (option) {
            case 0:
                query.append("('+','#') THEN '(+,#)' ELSE '(!,-,/,=)'");
                break;
            case 1:
                query.append("('+','#', '!') THEN '(+,#,!)' ELSE '(-,/,=)'");
                break;
            case 2:
                query.append("('+','#') THEN '(+,#)' ELSE CASE WHEN a.quality IN ('!','-') THEN '(!,-)' ELSE '(/,=)' END");
                break;
        }

    }
}
