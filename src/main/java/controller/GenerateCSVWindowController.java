package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.database.entity.Player;
import model.database.reader.InstancesReader;
import model.database.writer.CSVGenerator;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GenerateCSVWindowController implements Initializable {

    public Label setterLabel;
    public Label outsideHitterLabel;
    public Label oppositeLabel;
    public Label middleBlockerLabel;

    public ChoiceBox<Player> setter1Box;
    public ChoiceBox<Player> setter2Box;
    public ChoiceBox<Player> outside1Box;
    public ChoiceBox<Player> outside2Box;
    public ChoiceBox<Player> outside3Box;
    public ChoiceBox<Player> outside4Box;
    public ChoiceBox<Player> opposite1Box;
    public ChoiceBox<Player> opposite2Box;
    public ChoiceBox<Player> middle1Box;
    public ChoiceBox<Player> middle2Box;
    public ChoiceBox<Player> middle3Box;
    public ChoiceBox<Player> middle4Box;
    public ChoiceBox<Player> libero1Box;
    public ChoiceBox<Player> libero2Box;

    public Button serveReceptionButton;
    public Button settersButton;
    public Button attOppositesButton;
    public Button attOutsideButton;
    public Button attMiddleButton;
    public Button allButton;

    private Stage stage;

    private List<Player> playerList;
    private List<Player> setterList;
    private List<Player> outsideList;
    private List<Player> oppositeList;
    private List<Player> middleList;
    private List<Player> liberoList;
    private List<Player> servingList;
    private List<Player> receiversList;

    private Player noPlayer = new Player("-", "", 0, 0, 0);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerList = ControllersManager.getInstance().getMainController().getPlayersList();
        try {
            InstancesReader instancesReader = InstancesReader.getInstance();
            playerList = instancesReader.sortPlayers(playerList);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        preparePlayersLists();
        setConverters();
        setPlayersListsInBoxes();

        setter1Box.setValue(setterList.get(0));
        setter2Box.setValue(setterList.get(1));
        outside1Box.setValue(outsideList.get(0));
        outside2Box.setValue(outsideList.get(1));
        outside3Box.setValue(outsideList.get(2));
        outside4Box.setValue(outsideList.get(3));
        opposite1Box.setValue(oppositeList.get(0));
        opposite2Box.setValue(oppositeList.get(1));
        middle1Box.setValue(middleList.get(0));
        middle2Box.setValue(middleList.get(1));
        middle3Box.setValue(middleList.get(2));
        middle4Box.setValue(middleList.get(3));
        libero1Box.setValue(liberoList.get(0));
        libero2Box.setValue(liberoList.get(1));
    }

    private void preparePlayersLists() {
        setterList = new ArrayList<>();
        outsideList = new ArrayList<>();
        oppositeList = new ArrayList<>();
        middleList = new ArrayList<>();
        liberoList = new ArrayList<>();
        servingList = new ArrayList<>();
        receiversList = new ArrayList<>();

        for (Player player : playerList) {
            switch (player.getPosition()) {
                case 1:
                    liberoList.add(player);
                    break;
                case 2:
                    outsideList.add(player);
                    break;
                case 3:
                    oppositeList.add(player);
                    break;
                case 4:
                    middleList.add(player);
                    break;
                case 5:
                    setterList.add(player);
                    break;
            }
        }
        servingList.addAll(setterList);
        servingList.addAll(outsideList);
        servingList.addAll(oppositeList);
        servingList.addAll(middleList);

        receiversList.addAll(outsideList);
        receiversList.addAll(liberoList);

        if (liberoList.size() < 2) {
            for (int i = liberoList.size(); i < 2; i++) {
                liberoList.add(noPlayer);
            }
        }
        if (oppositeList.size() < 2) {
            for (int i = oppositeList.size(); i < 2; i++) {
                oppositeList.add(noPlayer);
            }
        }
        if (setterList.size() < 2) {
            for (int i = setterList.size(); i < 2; i++) {
                setterList.add(noPlayer);
            }
        }
        if (middleList.size() < 4) {
            for (int i = middleList.size(); i < 4; i++) {
                middleList.add(noPlayer);
            }
        }
        if (outsideList.size() < 4) {
            for (int i = outsideList.size(); i < 4; i++) {
                outsideList.add(noPlayer);
            }
        }
    }

    private void setConverters() {
        StringConverter<Player> stringConverter = new StringConverter<>() {
            @Override
            public String toString(Player object) {
                return object.getFullName();
            }

            @Override
            public Player fromString(String string) {
                for (Player player : playerList) {
                    if (player.getFullName().equals(string)) {
                        return player;
                    }
                }
                return null;
            }
        };
        setter1Box.setConverter(stringConverter);
        setter2Box.setConverter(stringConverter);
        outside1Box.setConverter(stringConverter);
        outside2Box.setConverter(stringConverter);
        outside3Box.setConverter(stringConverter);
        outside4Box.setConverter(stringConverter);
        opposite1Box.setConverter(stringConverter);
        opposite2Box.setConverter(stringConverter);
        middle1Box.setConverter(stringConverter);
        middle2Box.setConverter(stringConverter);
        middle3Box.setConverter(stringConverter);
        middle4Box.setConverter(stringConverter);
        libero1Box.setConverter(stringConverter);
        libero2Box.setConverter(stringConverter);
    }

    private void setPlayersListsInBoxes() {
        setter1Box.setItems(FXCollections.observableArrayList(setterList));
        setter2Box.setItems(FXCollections.observableArrayList(setterList));
        outside1Box.setItems(FXCollections.observableArrayList(outsideList));
        outside2Box.setItems(FXCollections.observableArrayList(outsideList));
        outside3Box.setItems(FXCollections.observableArrayList(outsideList));
        outside4Box.setItems(FXCollections.observableArrayList(outsideList));
        opposite1Box.setItems(FXCollections.observableArrayList(oppositeList));
        opposite2Box.setItems(FXCollections.observableArrayList(oppositeList));
        middle1Box.setItems(FXCollections.observableArrayList(middleList));
        middle2Box.setItems(FXCollections.observableArrayList(middleList));
        middle3Box.setItems(FXCollections.observableArrayList(middleList));
        middle4Box.setItems(FXCollections.observableArrayList(middleList));
        libero1Box.setItems(FXCollections.observableArrayList(liberoList));
        libero2Box.setItems(FXCollections.observableArrayList(liberoList));
    }

    @FXML
    private void generateAll() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(getStage());
        CSVGenerator csvGenerator = new CSVGenerator(selectedDirectory.getAbsolutePath());
        try {
            csvGenerator.generateServeAndReceptionCSV("/serve&reception.csv", servingList, receiversList);
            csvGenerator.generateSettersCSV("/setters.csv", setterList);
            csvGenerator.generateOppositeAttacksCSV("/opposites.csv", oppositeList);
            csvGenerator.generateOutsideHittersAttacksCSV("/outsideHitters.csv", outsideList);
            csvGenerator.generateMiddleBlockersAttacksCSV("/middleBlockers.csv", middleList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void generateServeReception() {
        CSVGenerator csvGenerator = createCSVGenerator();
        try {
            csvGenerator.generateServeAndReceptionCSV(null, servingList, receiversList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void generateSetters() {
        CSVGenerator csvGenerator = createCSVGenerator();
        try {
            csvGenerator.generateSettersCSV(null, setterList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void generateOpposites() {
        CSVGenerator csvGenerator = createCSVGenerator();
        try {
            csvGenerator.generateOppositeAttacksCSV(null, oppositeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void generateOutsides() {
        CSVGenerator csvGenerator = createCSVGenerator();
        try {
            csvGenerator.generateOutsideHittersAttacksCSV(null, outsideList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void generateMiddles() {
        CSVGenerator csvGenerator = createCSVGenerator();
        try {
            csvGenerator.generateMiddleBlockersAttacksCSV(null, middleList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CSVGenerator createCSVGenerator() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.csv", "*.csv"));
        File selectedFile = fileChooser.showSaveDialog(getStage());
        return new CSVGenerator(selectedFile.getAbsolutePath());
    }

    private Stage getStage() {
        if (stage != null) {
            return stage;
        } else {
            return (Stage) allButton.getScene().getWindow();
        }
    }
}
