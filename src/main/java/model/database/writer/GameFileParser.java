package model.database.writer;

import model.database.dao.*;
import model.database.entity.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GameFileParser {

    private List<String> fileStages;
    private List<String> lineTokens;
    private int stage;
    private int linesReadedAtStage;

    private Game game;
    private Team homeTeam;
    private Team opponentTeam;
    private Formation formation;
    private List<Player> homeTeamPlayers;
    private Map<Integer, Long> homePlayersNumberIndexMap;
    private List<Player> opponentTeamPlayers;
    private Map<Integer, Long> opponentPlayersNumberIndexMap;
    private List<Action> preparedActions;
    private Map<Long, Integer> playersIdAttackAttemptMap;

    private GameDao gameDao;
    private TeamDao teamDao;
    private PlayerDao playerDao;
    private ActionDao actionDao;
    private FormationDao formationDao;

    private String gameResult;
    private int actionNr;
    private int setNr;
    private int homeTeamPoints;
    private int opponentTeamPoints;
    private String serveSide;
    private String recDefQ;
    private String setQ;
    private String lastSetTrg;
    private String lastSetZone;
    private long recPlayerId;
    private int attackAttempt;
    private int sideOutAttempt;
    private int firstActionTime;

    public GameFileParser() {
        this.fileStages = new ArrayList<>();
        fileStages.add("MATCH");
        fileStages.add("TEAMS");
        fileStages.add("SET");
        fileStages.add("PLAYERS-H");
        fileStages.add("PLAYERS-V");
        fileStages.add("ATTACKCOMBINATION");
        fileStages.add("SCOUT");
        fileStages.add("END");

        DaoManager daoManager = DaoManager.getInstance();
        this.gameDao = daoManager.getGameDao();
        this.teamDao = daoManager.getTeamDao();
        this.playerDao = daoManager.getPlayerDao();
        this.actionDao = daoManager.getActionDao();
        this.formationDao = daoManager.getFormationDao();
    }

    public void readFile(String fileName) {
        this.homeTeamPlayers = new ArrayList<>();
        this.opponentTeamPlayers = new ArrayList<>();
        this.homePlayersNumberIndexMap = new HashMap<>();
        this.opponentPlayersNumberIndexMap = new HashMap<>();
        this.playersIdAttackAttemptMap = new HashMap<>();
        stage = 0;
        linesReadedAtStage = 0;
        actionNr = 1;
        setNr = 1;
        attackAttempt = 1;
        sideOutAttempt = 1;
        lastSetTrg = "first";
        lastSetZone = "first";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Charset.forName("windows-1250")));
            String line;
            while ((line = br.readLine()) != null) {
                process(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CloneNotSupportedException ex) {
            System.out.print(ex.getMessage());
        }
    }

    private void process(String line) throws CloneNotSupportedException {
        if (line.contains(fileStages.get(stage))) {
            stage++;
            linesReadedAtStage = 0;
            if (stage == 4) {
                gameResult += ")";
                game.setResult(gameResult);
            } else if (stage == 7) {
                preparedActions = new ArrayList<>();
            }
        } else {
            switch (stage) {
                case 1:
                    if (linesReadedAtStage == 0) {
                        lineTokens = getLineTokens(line);
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = sdf.parse(lineTokens.get(0));
                            sdf.applyPattern("yyyy-MM-dd");
                            String dateString = sdf.format(date);
                            game = new Game(dateString, "2019/2020", lineTokens.get(3), lineTokens.get(4));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        linesReadedAtStage++;
                    }
                    break;
                case 2:
                    if (linesReadedAtStage == 0) {
                        lineTokens = getLineTokens(line);
                        homeTeam = new Team(lineTokens.get(1), game.getSeason());
                        gameResult = lineTokens.get(2);
                        homeTeam.setId(teamDao.getTeamId(homeTeam.getName(), homeTeam.getSeason()));
                        if (homeTeam.getId() == -1) {
                            homeTeam.setId(teamDao.insertTeam(homeTeam));
                        }
                    } else if (linesReadedAtStage == 1) {
                        lineTokens = getLineTokens(line);
                        opponentTeam = new Team(lineTokens.get(1), game.getSeason());
                        String opponentTeamSets = lineTokens.get(2);
                        int winner = 2;
                        if (Integer.parseInt(gameResult) > Integer.parseInt(opponentTeamSets)) {
                            winner = 0;
                        } else if (Integer.parseInt(gameResult) < Integer.parseInt(opponentTeamSets)) {
                            winner = 1;
                        }
                        game.setWinner(winner);
                        gameResult += ":" + opponentTeamSets;
                        opponentTeam.setId(teamDao.getTeamId(opponentTeam.getName(), opponentTeam.getSeason()));
                        if (opponentTeam.getId() == -1) {
                            opponentTeam.setId(teamDao.insertTeam(opponentTeam));
                        }
                    }
                    linesReadedAtStage++;
                    break;
                case 3:
                    lineTokens = getLineTokens(line);
                    if (linesReadedAtStage == 0) {
                        gameResult += " (" + lineTokens.get(4);
                    } else {
                        if (lineTokens.size() > 4 && lineTokens.get(4) != null) {
                            gameResult += ", " + lineTokens.get(4);
                        }
                    }
                    linesReadedAtStage++;
                    break;
                case 4:
                case 5:
                    lineTokens = getLineTokens(line);
                    String surname = lineTokens.get(9);
                    String name = lineTokens.get(10);
                    int position = 0;
                    int number = -1;
                    try {
                        position = Integer.parseInt(lineTokens.get(13));
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        number = Integer.parseInt(lineTokens.get(1));
                    } catch (NumberFormatException ignored) {
                    }
                    if (stage == 4) {
                        Player player = new Player(name, surname, position, number, homeTeam.getId());
                        homeTeamPlayers.add(player);
                    } else {
                        Player player = new Player(name, surname, position, number, opponentTeam.getId());
                        opponentTeamPlayers.add(player);
                    }
                    break;
                case 7:
                    if (linesReadedAtStage < 4) {
                        if (linesReadedAtStage == 0 && setNr == 1) {
                            for (Player player : homeTeamPlayers) {
                                player.setId(playerDao.getPlayerId(homeTeam.getId(), player.getNumber(), player.getSurname()));
                                if (player.getId() == -1) {
                                    player.setId(playerDao.insertPlayer(player));
                                }
                                homePlayersNumberIndexMap.put(player.getNumber(), player.getId());
                                playersIdAttackAttemptMap.put(player.getId(), 0);
                            }
                            for (Player player : opponentTeamPlayers) {
                                player.setId(playerDao.getPlayerId(opponentTeam.getId(), player.getNumber(), player.getSurname()));
                                if (player.getId() == -1) {
                                    player.setId(playerDao.insertPlayer(player));
                                }
                                opponentPlayersNumberIndexMap.put(player.getNumber(), player.getId());
                                playersIdAttackAttemptMap.put(player.getId(), 0);
                            }
                            homeTeam.setPlayerList(homeTeamPlayers);
                            opponentTeam.setPlayerList(opponentTeamPlayers);
                            game.setHomeTeam(homeTeam);
                            game.setOpponentTeam(opponentTeam);
                            if (gameDao.getGameId(game) != -1) {
                                throw new CloneNotSupportedException("This game has already been read");
                            }
                            game.setId(gameDao.insertGame(game));
                        }
                        linesReadedAtStage++;
                    } else {
                        lineTokens = getLineTokens(line);
                        String actionCode = lineTokens.get(0);
                        if (!actionCode.contains(":")) {
                            if (!actionCode.contains("$$") && !actionCode.contains(">") && actionCode.length() > 5 && !actionCode.contains("set")) {
                                Action action = setActionFromCode(actionCode);
                                action.setGameId(game.getId());
                                action.setNr(actionNr);
                                action.setSetNr(setNr);
                                action.setHomeTeamPoints(homeTeamPoints);
                                action.setOpponentTeamPoints(opponentTeamPoints);
                                if (Math.abs(homeTeamPoints - opponentTeamPoints) > 6) {
                                    action.setSetPhase("0");
                                } else {
                                    if (setNr < 5) {
                                        if (homeTeamPoints < 9 && opponentTeamPoints < 9) {
                                            action.setSetPhase("1");
                                        } else if (homeTeamPoints < 20 && opponentTeamPoints < 20) {
                                            action.setSetPhase("2");
                                        } else if (homeTeamPoints < 24 && opponentTeamPoints < 24) {
                                            action.setSetPhase("3");
                                        } else {
                                            action.setSetPhase("4");
                                        }
                                    } else if (homeTeamPoints < 12 && opponentTeamPoints < 12) {
                                        action.setSetPhase("3");
                                    } else {
                                        action.setSetPhase("4");
                                    }
                                }
                                action.setServeSide(serveSide);
                                try {
                                    int currentTime = Integer.parseInt(lineTokens.get(lineTokens.size() - 14));
                                    if (actionNr == 1 && preparedActions.size() == 0) {
                                        firstActionTime = currentTime;
                                    }
                                    int gameTime = currentTime - firstActionTime;
                                    String time;
                                    if (gameTime < 900) {
                                        time = "<15min";
                                    } else if (gameTime < 3600) {
                                        time = "15min-1h";
                                    } else if (gameTime < 7200) {
                                        time = "1h-2h";
                                    } else {
                                        time = ">2h";
                                    }
                                    action.setTime(time);
                                } catch (NumberFormatException ex) {
                                    ex.getCause();
                                }
                                action.setHomeSetterPosition(lineTokens.get(lineTokens.size() - 17));
                                action.setOpponentSetterPosition(lineTokens.get(lineTokens.size() - 16));
                                String sideOutAttemptString = ">2";
                                switch (sideOutAttempt) {
                                    case 1:
                                        sideOutAttemptString = "1";
                                        break;
                                    case 2:
                                        sideOutAttemptString = "2";
                                        break;
                                }
                                action.setSideOutAttempt(sideOutAttemptString);
                                action.setRecDefQ(recDefQ);
                                action.setSetQ(setQ);
                                preparedActions.add(action);
                                if (formation == null) {
                                    List<Integer> intPositionValues = new ArrayList<>();
                                    for (int i = 0; i < 12; i++) {
                                        intPositionValues.add(Integer.valueOf(lineTokens.get(lineTokens.size() - 12 + i)));
                                    }
                                    formation = new Formation(game.getId(), actionNr, intPositionValues.get(0), intPositionValues.get(1),
                                            intPositionValues.get(2), intPositionValues.get(3), intPositionValues.get(4),
                                            intPositionValues.get(5), intPositionValues.get(6), intPositionValues.get(7),
                                            intPositionValues.get(8), intPositionValues.get(9), intPositionValues.get(10),
                                            intPositionValues.get(11));
                                    formationDao.insertFormation(formation);
                                }
                            } else if (actionCode.contains("set")) {
                                setNr++;
                                linesReadedAtStage = 0;
                                homeTeamPoints = 0;
                                opponentTeamPoints = 0;
                            }
                        } else if (actionCode.charAt(1) == 'p') {
                            String point = Character.toString(actionCode.charAt(0));
                            if (point.equals("*")) {
                                homeTeamPoints++;
                            } else {
                                opponentTeamPoints++;
                            }
                            for (Action action : preparedActions) {
                                action.setPoint(point);
                                actionDao.insertAction(action);
                            }
                            formation = null;
                            recDefQ = null;
                            setQ = null;
                            preparedActions.clear();
                            if (point.equals(serveSide)) {
                                sideOutAttempt++;
                            } else {
                                sideOutAttempt = 1;
                            }
                            attackAttempt = 1;
                            actionNr++;
                        }
                    }
                    break;
            }
        }
    }

    private List<String> getLineTokens(String line) {
        String[] stringArr = line.split(";");
        return Arrays.asList(stringArr);
    }

    private Action setActionFromCode(String actionCode) {
        Action action = new Action();
        boolean homeTeamAction = true;
        char actionSide = actionCode.charAt(0);
        String quality = Character.toString(actionCode.charAt(5));
        for (int i = 0; i < 15 && i < actionCode.length(); i++) {
            switch (i) {
                case 0:
                    homeTeamAction = (actionSide == '*');
                    action.setActionSide(Character.toString(actionSide));
                    break;
                case 1:
                    try {
                        int number = Integer.parseInt(actionCode.substring(1, 3));
                        if (homeTeamAction) {
                            action.setPlayerId(homePlayersNumberIndexMap.getOrDefault(number, -1L));
                        } else {
                            action.setPlayerId(opponentPlayersNumberIndexMap.getOrDefault(number, -1L));
                        }
                    } catch (NumberFormatException ex) {
                        ex.getCause();
                        if (homeTeamAction) {
                            action.setPlayerId(-1);
                        } else {
                            action.setPlayerId(-1);
                        }
                    }
                    break;
                case 3:
                    String skill = Character.toString(actionCode.charAt(3));
                    switch (skill) {
                        case "S":
                            serveSide = Character.toString(actionSide);
                            attackAttempt = 1;
                            action.setAttackAttempt(attackAttempt);
                            break;
                        case "R":
                            if (preparedActions.size() > 0) {
                                Action lastAction = preparedActions.get(preparedActions.size() - 1);
                                if (lastAction.getSkill().equals("S")) {
                                    action.setSpecial(lastAction.getSpecial());
                                }
                            }
                            recPlayerId = action.getPlayerId();
                            recDefQ = quality;
                            action.setAttackAttempt(attackAttempt);
                            if (quality.equals("/")) {
                                attackAttempt++;
                            }
                            break;
                        case "D":
                            if (preparedActions.size() > 0) {
                                Action lastAction = preparedActions.get(preparedActions.size() - 1);
                                action.setAttackCmb(lastAction.getAttackCmb());
                                action.setSkillType(lastAction.getSkillType());
                                if (lastAction.getSkill().equals("A")) {
                                    action.setStartZone(lastAction.getStartZone());
                                }
                            }
                            recPlayerId = action.getPlayerId();
                            recDefQ = quality;
                            action.setAttackAttempt(attackAttempt - 1);
                            break;
                        case "F":
                            recPlayerId = action.getPlayerId();
                            recDefQ = quality;
                            action.setAttackAttempt(attackAttempt);
                            if (quality.equals("/")) {
                                attackAttempt++;
                            }
                            break;
                        case "E":
                            setQ = quality;
                            action.setLastSetTrg(lastSetTrg);
                            action.setLastSetZone(lastSetZone);
                            action.setRecPlayerId(recPlayerId);
                            if (preparedActions.size() > 0) {
                                action.setStartZone(preparedActions.get(preparedActions.size() - 1).getEndZone());
                            }
                            action.setAttackAttempt(attackAttempt);
                            break;
                        case "A":
                            if (action.getPlayerId() != -1) {
                                int playerAttackAttempt = playersIdAttackAttemptMap.get(action.getPlayerId());
                                playersIdAttackAttemptMap.put(action.getPlayerId(), playerAttackAttempt + 1);
                                action.setPlayerAttackAttempt(playerAttackAttempt + 1);
                            } else {
                                action.setPlayerAttackAttempt(0);
                            }
                            action.setRecPlayerId(recPlayerId);
                            action.setAttackAttempt(attackAttempt);
                            attackAttempt++;
                            break;
                        case "B":
                            if (preparedActions.size() > 0) {
                                Action lastAction = preparedActions.get(preparedActions.size() - 1);
                                action.setSetterCmb(lastAction.getSetterCmb());
                                action.setAttackCmb(lastAction.getAttackCmb());
                            }
                            action.setAttackAttempt(attackAttempt - 1);
                            break;
                    }
                    action.setSkill(skill);
                    break;
                case 4:
                    action.setType(Character.toString(actionCode.charAt(4)));
                    break;
                case 5:
                    action.setQuality(quality);
                    break;
                case 6:
                    String cmb = Character.toString(actionCode.charAt(6));
                    if (!cmb.equals("~")) {
                        cmb += Character.toString(actionCode.charAt(7));
                        if (cmb.equals("KK")) {
                            if (actionCode.length() >= 17) {
                                cmb = actionCode.substring(15, 17);
                            }
                        }
                        if (action.getSkill().equals("A")) {
                            action.setAttackCmb(cmb);
                            if (preparedActions.size() > 0) {
                                Action lastAction = preparedActions.get(preparedActions.size() - 1);
                                if (lastAction.getSkill().equals("E") && !lastAction.getQuality().equals("\\")) {
                                    action.setSetterCmb(lastAction.getSetterCmb());
                                    action.setSetPosition(lastAction.getEndZone());
                                    setSetterActionInfoFromCmb(lastAction, action);
                                    lastAction.setSetToWhichPlayerId(action.getPlayerId());
                                }
                            }
                        } else {
                            action.setSetterCmb(cmb);
                        }
                    }
                    action.setQuality(quality);
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                    String sign = Character.toString(actionCode.charAt(i));
                    if (!sign.equals("~")) {
                        switch (i) {
                            case 8:
                                action.setTrg(sign);
                                break;
                            case 9:
                                action.setStartZone(sign);
                                if (action.getSkill().equals("A")) {
                                    if (preparedActions.size() > 0) {
                                        Action lastAction = preparedActions.get(preparedActions.size() - 1);
                                        if (lastAction.getSkill().equals("E") && !lastAction.getQuality().equals("\\")) {
                                            lastAction.setSetZone(action.getStartZone());
                                            lastSetZone = lastAction.getSetZone();
                                        }
                                    }
                                }
                                break;
                            case 10:
                                action.setEndZone(sign);
                                if (action.getSkill().equals("A")) {
                                    switch (sign) {
                                        case "1":
                                        case "2":
                                        case "9":
                                            action.setAttackDirection("L");
                                            break;
                                        case "3":
                                        case "8":
                                        case "6":
                                            action.setAttackDirection("C");
                                            break;
                                        case "4":
                                        case "5":
                                        case "7":
                                            action.setAttackDirection("R");
                                            break;
                                    }
                                }
                                break;
                            case 11:
                                action.setEndZoneExt(sign);
                                break;
                            case 12:
                                action.setSkillType(sign);
                                break;
                            case 13:
                                action.setPlayers(sign);
                                break;
                            case 14:
                                action.setSpecial(sign);
                                break;
                        }
                    }
                    break;
            }
        }
        return action;
    }

    private void setSetterActionInfoFromCmb(Action setterAction, Action attackAction) {
        setterAction.setAttackCmb(attackAction.getAttackCmb());
        setterAction.setSetZone(attackAction.getStartZone());
        switch (attackAction.getAttackCmb()) {
            case "X5":
            case "V5":
            case "X9":
                setterAction.setTrg("F");
                break;
            case "X6":
            case "V6":
            case "X4":
            case "X8":
            case "V8":
                setterAction.setTrg("B");
                break;
            case "X1":
            case "XC":
            case "XD":
            case "X7":
            case "XM":
            case "X2":
            case "XF":
                setterAction.setTrg("C");
                break;
            case "XP":
            case "VP":
                setterAction.setTrg("P");
                break;
        }
        lastSetTrg = setterAction.getTrg();
    }
}
