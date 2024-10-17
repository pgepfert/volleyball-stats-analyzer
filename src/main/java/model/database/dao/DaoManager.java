package model.database.dao;

public class DaoManager {

    private static DaoManager instance;

    private ActionDao actionDao;
    private FormationDao formationDao;
    private GameDao gameDao;
    private PlayerDao playerDao;
    private TeamDao teamDao;

    private DaoManager(){
        actionDao = new ActionDaoImpl();
        formationDao = new FormationDaoImpl();
        gameDao = new GameDaoImpl();
        playerDao = new PlayerDaoImpl();
        teamDao = new TeamDaoImpl();
    }

    public static DaoManager getInstance() {
        if (instance == null) {
            instance = new DaoManager();
        }
        return instance;
    }

    public ActionDao getActionDao() {
        return actionDao;
    }

    public FormationDao getFormationDao() {
        return formationDao;
    }

    public GameDao getGameDao() {
        return gameDao;
    }

    public PlayerDao getPlayerDao() {
        return playerDao;
    }

    public TeamDao getTeamDao() {
        return teamDao;
    }
}
