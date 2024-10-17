package model.database.entity;

public class Player {

    private long id;
    private String name;
    private String surname;
    private int position;
    private int number;
    private long teamId;

    public Player(long id, String name, String surname, int position, long teamId) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.position = position;
        this.teamId = teamId;
    }

    public Player(String name, String surname, int position, int number, long teamId) {
        this.name = name;
        this.surname = surname;
        this.position = position;
        this.number = number;
        this.teamId = teamId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    @Override
    public String toString(){
        return name + " " + surname + " (#" + number + ")";
    }

    public String getFullName() {
        return name + " " + surname;
    }
}
