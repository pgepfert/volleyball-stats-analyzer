package model.database.entity;

public class Action {

    private long id;
    private long gameId;
    private long playerId;
    private int nr;
    private int setNr;
    private int homeTeamPoints;
    private int opponentTeamPoints;
    private String setPhase;
    private String serveSide;
    private String actionSide;
    private String skill;
    private String type;
    private String quality;
    private String setterCmb;
    private String attackCmb;
    private String trg;
    private String startZone;
    private String endZone;
    private String endZoneExt;
    private String skillType;
    private String players;
    private String special;
    private String time;
    private String homeSetterPosition;
    private String opponentSetterPosition;
    private String recDefQ;
    private String setQ;
    private String setPosition;
    private String setZone;
    private String attackDirection;
    private String lastSetTrg;
    private String lastSetZone;
    private long recPlayerId;
    private long setToWhichPlayerId;
    private int attackAttempt;
    private int playerAttackAttempt;
    private String sideOutAttempt;
    private String point;

    public Action() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public int getSetNr() {
        return setNr;
    }

    public void setSetNr(int setNr) {
        this.setNr = setNr;
    }

    public int getHomeTeamPoints() {
        return homeTeamPoints;
    }

    public void setHomeTeamPoints(int homeTeamPoints) {
        this.homeTeamPoints = homeTeamPoints;
    }

    public int getOpponentTeamPoints() {
        return opponentTeamPoints;
    }

    public void setOpponentTeamPoints(int opponentTeamPoints) {
        this.opponentTeamPoints = opponentTeamPoints;
    }

    public String getSetPhase() {
        return setPhase;
    }

    public void setSetPhase(String setPhase) {
        this.setPhase = setPhase;
    }

    public String getServeSide() {
        return serveSide;
    }

    public void setServeSide(String serveSide) {
        this.serveSide = serveSide;
    }

    public String getActionSide() {
        return actionSide;
    }

    public void setActionSide(String actionSide) {
        this.actionSide = actionSide;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSetterCmb() {
        return setterCmb;
    }

    public void setSetterCmb(String setterCmb) {
        this.setterCmb = setterCmb;
    }

    public String getAttackCmb() {
        return attackCmb;
    }

    public void setAttackCmb(String attackCmb) {
        this.attackCmb = attackCmb;
    }

    public String getTrg() {
        return trg;
    }

    public void setTrg(String trg) {
        this.trg = trg;
    }

    public String getStartZone() {
        return startZone;
    }

    public void setStartZone(String startZone) {
        this.startZone = startZone;
    }

    public String getEndZone() {
        return endZone;
    }

    public void setEndZone(String endZone) {
        this.endZone = endZone;
    }

    public String getEndZoneExt() {
        return endZoneExt;
    }

    public void setEndZoneExt(String endZoneExt) {
        this.endZoneExt = endZoneExt;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHomeSetterPosition() {
        return homeSetterPosition;
    }

    public void setHomeSetterPosition(String homeSetterPosition) {
        this.homeSetterPosition = homeSetterPosition;
    }

    public String getOpponentSetterPosition() {
        return opponentSetterPosition;
    }

    public void setOpponentSetterPosition(String opponentSetterPosition) {
        this.opponentSetterPosition = opponentSetterPosition;
    }

    public String getRecDefQ() {
        return recDefQ;
    }

    public void setRecDefQ(String recDefQ) {
        this.recDefQ = recDefQ;
    }

    public String getSetQ() {
        return setQ;
    }

    public void setSetQ(String setQ) {
        this.setQ = setQ;
    }

    public String getSetPosition() {
        return setPosition;
    }

    public void setSetPosition(String setPosition) {
        this.setPosition = setPosition;
    }

    public String getSetZone() {
        return setZone;
    }

    public void setSetZone(String setZone) {
        this.setZone = setZone;
    }

    public String getAttackDirection() {
        return attackDirection;
    }

    public void setAttackDirection(String attackDirection) {
        this.attackDirection = attackDirection;
    }

    public String getLastSetTrg() {
        return lastSetTrg;
    }

    public void setLastSetTrg(String lastSetTrg) {
        this.lastSetTrg = lastSetTrg;
    }

    public String getLastSetZone() {
        return lastSetZone;
    }

    public void setLastSetZone(String lastSetZone) {
        this.lastSetZone = lastSetZone;
    }

    public long getRecPlayerId() {
        return recPlayerId;
    }

    public void setRecPlayerId(long recPlayerId) {
        this.recPlayerId = recPlayerId;
    }

    public long getSetToWhichPlayerId() {
        return setToWhichPlayerId;
    }

    public void setSetToWhichPlayerId(long setToWhichPlayerId) {
        this.setToWhichPlayerId = setToWhichPlayerId;
    }

    public int getAttackAttempt() {
        return attackAttempt;
    }

    public void setAttackAttempt(int attackAttempt) {
        this.attackAttempt = attackAttempt;
    }

    public int getPlayerAttackAttempt() {
        return playerAttackAttempt;
    }

    public void setPlayerAttackAttempt(int playerAttackAttempt) {
        this.playerAttackAttempt = playerAttackAttempt;
    }

    public String getSideOutAttempt() {
        return sideOutAttempt;
    }

    public void setSideOutAttempt(String sideOutAttempt) {
        this.sideOutAttempt = sideOutAttempt;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
