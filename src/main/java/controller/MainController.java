package controller;

import controller.utils.ReadFileTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.database.DatabaseCreator;
import model.database.dao.DaoManager;
import model.database.dao.PlayerDao;
import model.database.dao.TeamDao;
import model.database.entity.Game;
import model.database.entity.Player;
import model.database.entity.Team;
import model.database.reader.InstancesReader;
import model.database.writer.CSVGenerator;
import model.exploration.AssociationRulesBuilder;
import model.exploration.DecisionTreeBuilder;
import view.MainWindow;
import weka.associations.Apriori;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.prefs.Preferences;

public class MainController implements Initializable {

    public Button treeButton;
    public Button rulesButton;
    public Button dataButton;
    public Button csvButton;
    public GridPane buttonsPane;
    public ListView<String> teamsListView;
    public ListView<String> playersListView;

    public Label selectTeamLabel;
    public Label selectPlayerLabel;

    public ProgressBar progressBar;
    public AnchorPane predictionOptions;

    public AnchorPane predictionParameters;
    public Label leftStatus;
    public Label rightStatus;
    public MenuItem readNewGamesMenu;
    public MenuItem removeGamesMenu;
    public MenuItem selectGamesMenu;
    public MenuItem openDatabaseMenu;
    public MenuItem createDatabaseMenu;
    public MenuItem selectLanguageMenu;
    public Menu databaseMenu;
    public Menu settingsMenu;
    public Menu helpMenu;

    boolean isItDeleting;
    List<Game> gamesToAnalyze;
    private Stage stage;
    private TeamDao teamDao;
    private PlayerDao playerDao;
    private List<Team> teamsList;
    private List<Player> playersList;
    private Player analyzedPlayer;
    private ResourceBundle strings;
    private ControllersManager controllersManager;
    private DecisionTreeBuilder decisionTreeBuilder;
    private AssociationRulesBuilder associationRulesBuilder;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        DaoManager daoManager = DaoManager.getInstance();
        teamDao = daoManager.getTeamDao();
        playerDao = daoManager.getPlayerDao();
        decisionTreeBuilder = new DecisionTreeBuilder();
        associationRulesBuilder = new AssociationRulesBuilder();
        strings = resources;
        selectTeamLabel.setText(strings.getString("selectTeam"));
        selectPlayerLabel.setText(strings.getString("selectPlayer"));

        treeButton.setText(strings.getString("runTree"));
        dataButton.setText(strings.getString("showData"));
        rulesButton.setText(strings.getString("showRules"));
        csvButton.setText(strings.getString("generateCSV"));
        readNewGamesMenu.setText(strings.getString("readNewGames"));
        removeGamesMenu.setText(strings.getString("removeGames"));
        selectGamesMenu.setText(strings.getString("selectGamesToAnalyze"));
        openDatabaseMenu.setText(strings.getString("openDatabase"));
        createDatabaseMenu.setText(strings.getString("createDatabase"));
        databaseMenu.setText(strings.getString("database"));
        settingsMenu.setText(strings.getString("settings"));
        selectLanguageMenu.setText(strings.getString("selectLanguage"));
        helpMenu.setText(strings.getString("help"));

        controllersManager = ControllersManager.getInstance();
        controllersManager.setMainController(this);
        getTeams();
    }

    Player getAnalyzedPlayer() {
        return analyzedPlayer;
    }

    List<Player> getPlayersList() {
        return playersList;
    }

    private Stage getStage() {
        if (stage != null) {
            return stage;
        } else {
            return (Stage) buttonsPane.getScene().getWindow();
        }
    }

    private void getTeams() {
        teamsList = teamDao.getAllTeams();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Team t : teamsList) {
            items.add(t.getName());
        }
        teamsListView.setItems(items);
        teamsListView.setOnMouseClicked(event -> {
            disablePredictions();
            csvButton.setDisable(false);
            getPlayers(teamsListView.getSelectionModel().getSelectedIndex());
        });
    }

    private void getPlayers(int selectedIndex) {
        if (selectedIndex > -1) {
            long teamID = teamsList.get(selectedIndex).getId();
            playersList = playerDao.getAllPlayersFromTeam(teamID);
            ObservableList<String> items = FXCollections.observableArrayList();
            for (Player p : playersList) {
                items.add(p.toString() + " - " + getPositionName(p.getPosition()));
            }
            playersListView.setItems(items);
            playersListView.setOnMouseClicked(event -> enablePredictions());
        }
    }

    private String getPositionName(int position) {
        switch (position) {
            case 1:
                return strings.getString("libero");
            case 2:
                return strings.getString("outsideHitter");
            case 3:
                return strings.getString("opposite");
            case 4:
                return strings.getString("middleBlocker");
            case 5:
                return strings.getString("setter");
            default:
                return "?";
        }
    }

    private void disablePredictions() {
        analyzedPlayer = null;
        controllersManager.getPredictionOptionsController().disablePredictions();
        enableDisableButtons(true);
    }

    private void enablePredictions() {
        int index = playersListView.getSelectionModel().getSelectedIndex();
        if (index > -1 && index < playersList.size()) {
            analyzedPlayer = playersList.get(playersListView.getSelectionModel().getSelectedIndex());
            controllersManager.getPredictionOptionsController().enablePredicions();
            enableDisableButtons(false);
        }
    }

    private void enableDisableButtons(boolean disable) {
        treeButton.setDisable(disable);
        dataButton.setDisable(disable);
        rulesButton.setDisable(disable);
    }

    @FXML
    private void readFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(strings.getString("selectGameFiles"));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Data Volley files (*.dvw)", "*.dvw");
        fileChooser.getExtensionFilters().add(extFilter);
        List<File> files = fileChooser.showOpenMultipleDialog(getStage());
        if (files != null) {
            ReadFileTask readFileTask = new ReadFileTask(files, strings);
            progressBar.progressProperty().bind(readFileTask.progressProperty());

            leftStatus.textProperty().bind(readFileTask.titleProperty());
            rightStatus.textProperty().bind(readFileTask.messageProperty());
            readFileTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, //
                    t -> {
                        leftStatus.textProperty().unbind();
                        leftStatus.setText(strings.getString("ready"));
                        rightStatus.textProperty().unbind();
                        rightStatus.setText("");
                        reloadTeams();
                    });
            new Thread(readFileTask).start();
        }
    }

    @FXML
    private void showData() {
        try {
            Instances instances = InstancesReader.getInstance().getData(analyzedPlayer,
                    controllersManager.getPredictionParametersController().getAllAttributes(),
                    controllersManager.getPredictionOptionsController().getFilters(), false);
            VisualizeController.visualizeData(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showRules() {
        try {
            if (true) {
                long time = System.currentTimeMillis();
                Apriori apriori = associationRulesBuilder.buildRules(analyzedPlayer,
                        controllersManager.getPredictionParametersController().getAllAttributes(),
                        controllersManager.getPredictionOptionsController().getFilters(),
                        controllersManager.getPredictionParametersController().getSupport(),
                        controllersManager.getPredictionParametersController().getConfidence(),
                        controllersManager.getPredictionParametersController().getAutoSelection());
                System.out.println("Time = " + (System.currentTimeMillis() - time));
                if (apriori != null) {
                    VisualizeController.visualizeRules(apriori.toString());
                }
            } else {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(getStage());
                CSVGenerator csvGenerator = new CSVGenerator(selectedDirectory.getAbsolutePath());
                String skill = "E";
                Instances playersWithMostActions = InstancesReader.getInstance().getPlayersWithMostActions(skill);
                List<Long> playersIds = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    playersIds.add((long) playersWithMostActions.get(i).value(0));
                }
                long startTime = System.currentTimeMillis();
                for (long playerId : playersIds) {
                    long playerTime = System.currentTimeMillis();
                    Player player = playerDao.getPlayer(playerId);
                    System.out.println(player.getFullName());
                    try {
                        double[][] data = associationRulesBuilder.collectResults(player,
                                controllersManager.getPredictionParametersController().getAllAttributes(),
                                controllersManager.getPredictionOptionsController().getFilters(),
                                controllersManager.getPredictionParametersController().getAutoSelection());
                        csvGenerator.generateResultsCSV("10folds", "", data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    playerTime = System.currentTimeMillis() - playerTime;
                    System.out.println("Player time = " + playerTime);
                }
                long estimatedTime = System.currentTimeMillis() - startTime;
                System.out.println("Full time = " + estimatedTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showTree() {
        try {
            if (true) {
                J48 decisionTree = decisionTreeBuilder.buildTree(analyzedPlayer,
                        controllersManager.getPredictionParametersController().getAllAttributes(),
                        controllersManager.getPredictionOptionsController().getFilters(),
                        controllersManager.getPredictionParametersController().getSupport(),
                        controllersManager.getPredictionParametersController().getConfidence(),
                        controllersManager.getPredictionParametersController().getAutoSelection());
                VisualizeController.visualizeTree(decisionTree);
            } else {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(getStage());
                CSVGenerator csvGenerator = new CSVGenerator(selectedDirectory.getAbsolutePath());
                String skill = "E";
                Instances playersWithMostActions = InstancesReader.getInstance().getPlayersWithMostActions(skill);
                List<Long> playersIds = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    playersIds.add((long) playersWithMostActions.get(i).value(0));
                }
                if (true) {
                    long startTime = System.currentTimeMillis();
                    for (long playerId : playersIds) {
                        Player player = playerDao.getPlayer(playerId);
                        System.out.println(player.getFullName());
                        try {
                            double[][] data = decisionTreeBuilder.collectResults(player,
                                    controllersManager.getPredictionParametersController().getAllAttributes(),
                                    controllersManager.getPredictionOptionsController().getFilters(),
                                    controllersManager.getPredictionParametersController().getAutoSelection());
                            csvGenerator.generateResultsCSV("10foldsTT2", "", data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    long estimatedTime = System.currentTimeMillis() - startTime;
                    System.out.println("Full time = " + estimatedTime);
                } else {
                    double[][] data = null;
                    for (int i = 0; i < playersIds.size(); i++) {
                        Player player = playerDao.getPlayer(playersIds.get(i));
                        try {
                            double[][] playerData = InstancesReader.getInstance().saveAttributesRanking(player,
                                    controllersManager.getPredictionParametersController().getAllAttributes(),
                                    controllersManager.getPredictionOptionsController().getFilters());
                            Arrays.sort(playerData, Comparator.comparingDouble(a -> a[0]));
                            int j = 0;
                            for (double[] rows : playerData) {
                                if (data == null) {
                                    data = new double[playerData.length][playersIds.size()];
                                }
                                data[j][i] = rows[1];
                                j++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    csvGenerator.generateResultsCSV(skill, "-test", data);
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Can't build tree");
            alert.setContentText("Too less data!");
            alert.showAndWait();
        }
    }

    @FXML
    private void generateCSV() {
        try {
            Parent root = FXMLLoader.load(MainWindow.class.getResource("generateCSVWindow.fxml"), strings);
            Stage stage = new Stage();
            stage.setTitle(strings.getString("generateCSV"));
            stage.setScene(new Scene(root, 600, 380));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void selectGames() {
        isItDeleting = false;
        try {
            Parent root = FXMLLoader.load(MainWindow.class.getResource("selectGames.fxml"), strings);
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 700, 700));
            stage.setResizable(false);
            stage.showAndWait();
            InstancesReader.getInstance().setGamesToAnalyze(gamesToAnalyze);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeGames() {
        isItDeleting = true;
        try {
            Parent root = FXMLLoader.load(MainWindow.class.getResource("selectGames.fxml"), strings);
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 700, 700));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void selectLanguage() {
        ButtonType polishButton = new ButtonType("POLISH/POLSKI", ButtonBar.ButtonData.LEFT);
        ButtonType englishButton = new ButtonType("ENGLISH/ANGIELSKI", ButtonBar.ButtonData.RIGHT);
        Alert alert = new Alert(Alert.AlertType.NONE,
                "Select language/Wybierz język", polishButton, englishButton);
        ButtonType result = alert.showAndWait().orElse(null);

        if (result != null) {
            try {
                Preferences preferences = Preferences.userRoot().node("volleyball-analyzer");
                if (result == polishButton) {
                    Locale polishLocale = new Locale("pl", "PL");
                    preferences.put("language", "pl");
                    Locale.setDefault(polishLocale);
                    MainWindow.load(getStage());
                } else {
                    preferences.put("language", "en");
                    Locale.setDefault(Locale.ENGLISH);
                    MainWindow.load(getStage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void selectDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.db", "*.db"));
        File selectedFile = fileChooser.showOpenDialog(getStage());
        if (selectedFile != null) {
            Preferences preferences = Preferences.userRoot().node("volleyball-analyzer");
            String databaseURL = "jdbc:sqlite:" + selectedFile.getAbsolutePath();
            preferences.put("database", databaseURL);
            try {
                InstancesReader.getInstance().setURL(databaseURL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            reloadTeams();
        }
    }

    @FXML
    private void createNewDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.db", "*.db"));
        File selectedFile = fileChooser.showSaveDialog(getStage());
        if (selectedFile != null) {
            try {
                DatabaseCreator.createNewDatabase(selectedFile.getAbsolutePath());
                reloadTeams();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void showAboutInfo() {
        Alert alert = new Alert(Alert.AlertType.NONE, strings.getString("appInfo"), ButtonType.OK);
        alert.setTitle("Volleyball Stats Analyzer");
        alert.showAndWait();
    }

    private void reloadTeams() {
        getTeams();
        playersListView.setItems(FXCollections.emptyObservableList());
        disablePredictions();
        csvButton.setDisable(true);
    }
}
