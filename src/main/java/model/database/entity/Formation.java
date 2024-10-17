package model.database.entity;

public class Formation {

    private long id;
    private long gameID;
    private int actionNr;
    private int h1;
    private int h2;
    private int h3;
    private int h4;
    private int h5;
    private int h6;
    private int v1;
    private int v2;
    private int v3;
    private int v4;
    private int v5;
    private int v6;

    public Formation(long gameID, int actionNr, int h1, int h2, int h3, int h4, int h5, int h6,
                     int v1, int v2, int v3, int v4, int v5, int v6) {
        this.gameID = gameID;
        this.actionNr = actionNr;
        this.h1 = h1;
        this.h2 = h2;
        this.h3 = h3;
        this.h4 = h4;
        this.h5 = h5;
        this.h6 = h6;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        this.v5 = v5;
        this.v6 = v6;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGameID() {
        return gameID;
    }

    public void setGameID(long gameID) {
        this.gameID = gameID;
    }

    public int getActionNr() {
        return actionNr;
    }

    public void setActionNr(int actionNr) {
        this.actionNr = actionNr;
    }

    public int getH1() {
        return h1;
    }

    public void setH1(int h1) {
        this.h1 = h1;
    }

    public int getH2() {
        return h2;
    }

    public void setH2(int h2) {
        this.h2 = h2;
    }

    public int getH3() {
        return h3;
    }

    public void setH3(int h3) {
        this.h3 = h3;
    }

    public int getH4() {
        return h4;
    }

    public void setH4(int h4) {
        this.h4 = h4;
    }

    public int getH5() {
        return h5;
    }

    public void setH5(int h5) {
        this.h5 = h5;
    }

    public int getH6() {
        return h6;
    }

    public void setH6(int h6) {
        this.h6 = h6;
    }

    public int getV1() {
        return v1;
    }

    public void setV1(int v1) {
        this.v1 = v1;
    }

    public int getV2() {
        return v2;
    }

    public void setV2(int v2) {
        this.v2 = v2;
    }

    public int getV3() {
        return v3;
    }

    public void setV3(int v3) {
        this.v3 = v3;
    }

    public int getV4() {
        return v4;
    }

    public void setV4(int v4) {
        this.v4 = v4;
    }

    public int getV5() {
        return v5;
    }

    public void setV5(int v5) {
        this.v5 = v5;
    }

    public int getV6() {
        return v6;
    }

    public void setV6(int v6) {
        this.v6 = v6;
    }
}
