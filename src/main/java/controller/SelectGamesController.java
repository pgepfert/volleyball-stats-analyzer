package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.database.dao.DaoManager;
import model.database.dao.GameDao;
import model.database.dao.TeamDao;
import model.database.entity.Game;
import model.database.entity.Team;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.IndexedCheckModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SelectGamesController implements Initializable {

    public Label selectGamesLabel;
    public CheckListView<Game> gamesListView;
    public CheckBox selectGamesCheckBox;
    public ChoiceBox<Team> teamsChoiceBox;
    public Button confirmButton;

    private ResourceBundle strings;
    private MainController mainController;

    private GameDao gameDao;
    private List<Team> teamsList;

    private boolean isItDeleting;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        strings = resources;
        DaoManager daoManager = DaoManager.getInstance();
        mainController = ControllersManager.getInstance().getMainController();
        isItDeleting = mainController.isItDeleting;
        gameDao = daoManager.getGameDao();
        TeamDao teamDao = daoManager.getTeamDao();
        teamsList = teamDao.getAllTeams();
        Team t0 = new Team(strings.getString("allTeams"), "2019/2020");
        t0.setId(-1);
        teamsList.add(0, t0);
        teamsChoiceBox.setItems(FXCollections.observableArrayList(teamsList));
        gamesListView.setCellFactory(listView -> new CheckBoxListCell<>(gamesListView::getItemBooleanProperty) {
            @Override
            public void updateItem(Game game, boolean empty) {
                super.updateItem(game, empty);
                setText(game == null ? "" : game.toString());
            }
        });
        setConverter();
        teamsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                getGames(newValue.getId());
            }
        });
        teamsChoiceBox.setValue(t0);
        selectGamesCheckBox.setSelected(!isItDeleting);
        selectGamesCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                checkAllGames();
            } else {
                clearCheckAllGames();
            }
        });
        if (isItDeleting) {
            selectGamesLabel.setText(strings.getString("removeGames"));
            confirmButton.setText(strings.getString("remove"));
            selectGamesCheckBox.setText(strings.getString("checkAll"));
        } else {
            selectGamesLabel.setText(strings.getString("selectGamesToAnalyze"));
            confirmButton.setText(strings.getString("confirm"));
            selectGamesCheckBox.setText(strings.getString("uncheckAll"));
            if (mainController.gamesToAnalyze != null && mainController.gamesToAnalyze.size() == 0) {
                selectGamesCheckBox.setSelected(false);
            }
        }
    }

    private void setConverter() {
        StringConverter<Team> stringConverter = new StringConverter<>() {
            @Override
            public String toString(Team object) {
                return object.getName();
            }

            @Override
            public Team fromString(String string) {
                for (Team team : teamsList) {
                    if (team.getName().equals(string)) {
                        return team;
                    }
                }
                return null;
            }
        };
        teamsChoiceBox.setConverter(stringConverter);
    }

    private void getGames(long teamID) {
        gamesListView.setItems(FXCollections.observableArrayList(gameDao.getAllGames(teamID)));
        if (!isItDeleting) {
            setGamesChecked();
        }
    }

    private void setGamesChecked() {
        if (mainController.gamesToAnalyze == null) {
            checkAllGames();
        } else {
            List<Game> gamesToAnalyze = mainController.gamesToAnalyze;
            IndexedCheckModel<Game> checkModel = gamesListView.getCheckModel();
            for (int i = 0; i < gamesListView.getItems().size(); i++) {
                int finalI = i;
                if (gamesToAnalyze.stream().anyMatch(o -> o.getId() == checkModel.getItem(finalI).getId())) {
                    checkModel.check(i);
                }
            }
        }
    }

    private void checkAllGames() {
        for (int i = 0; i < gamesListView.getItems().size(); i++) {
            gamesListView.getCheckModel().check(i);
        }
        selectGamesCheckBox.setText(strings.getString("uncheckAll"));
    }

    private void clearCheckAllGames() {
        for (int i = 0; i < gamesListView.getItems().size(); i++) {
            gamesListView.getCheckModel().clearCheck(i);
        }
        selectGamesCheckBox.setText(strings.getString("checkAll"));
    }

    @FXML
    private void endSelection() {
        if (isItDeleting) {
            if (gamesListView.getCheckModel().getCheckedItems().size() > 0) {
                gameDao.deleteGames(gamesListView.getCheckModel().getCheckedItems());
            }
        } else {
            mainController.gamesToAnalyze = new ArrayList<>(gamesListView.getCheckModel().getCheckedItems());
        }
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

}
