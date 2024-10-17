package model.database.entity;

public class Game {

    private long id;
    private String date;
    private String season;
    private String category;
    private String subcategory;
    private Team homeTeam;
    private Team opponentTeam;
    private String result;
    private int winner;

    public Game(String date, String season, String category, String subcategory) {
        this.date = date; //YYYY-MM-DD
        this.season = season;
        this.category = category;
        this.subcategory = subcategory;
    }

    public Game(String date, Team homeTeam, Team opponentTeam, String result) {
        this.date = date; //YYYY-MM-DD
        this.homeTeam = homeTeam;
        this.opponentTeam = opponentTeam;
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getOpponentTeam() {
        return opponentTeam;
    }

    public void setOpponentTeam(Team opponentTeam) {
        this.opponentTeam = opponentTeam;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return date + " " + homeTeam.getName() + " - " + opponentTeam.getName() + ": " + result;
    }
}
