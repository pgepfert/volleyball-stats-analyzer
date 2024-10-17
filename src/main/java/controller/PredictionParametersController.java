package controller;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.cell.CheckBoxListCell;
import model.database.reader.Attribute;
import org.controlsfx.control.CheckListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class PredictionParametersController implements Initializable {

    public CheckListView<Attribute> parametersListView;

    public Label selectParametersLabel;
    public Slider supportSlider;
    public Slider confidenceSlider;
    public Label supportValueLabel;
    public Label confidenceValueLabel;
    public CheckBox autoSelectCheckBox;

    private int support = 15;
    private int confidence = 75;
    private boolean isAutoSelection = false;

    private List<Attribute> setParameters;
    private List<Attribute> attackParameters;
    private List<Attribute> receiveParameters;

    private List<Attribute> currentParameters;

    private ResourceBundle strings;
    private ControllersManager controllersManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        strings = resources;
        selectParametersLabel.setText(strings.getString("selectParameters"));
        autoSelectCheckBox.setText(strings.getString("autoSelect"));
        autoSelectCheckBox.setSelected(false);
        autoSelectCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            isAutoSelection = newValue;
            if (isAutoSelection) {
                setAllParametersChecked();
                parametersListView.setDisable(true);
            } else {
                parametersListView.setDisable(false);
            }
        });
        currentParameters = new ArrayList<>();
        supportSlider.valueProperty().addListener((ov, old_val, new_val) -> {
            support = new_val.intValue();
            supportValueLabel.setText("support: " + support);
        });
        confidenceSlider.valueProperty().addListener((ov, old_val, new_val) -> {
            confidence = new_val.intValue();
            confidenceValueLabel.setText("confidence: " + confidence + "%");
        });

        parametersListView.setCellFactory(listView -> new CheckBoxListCell<>(parametersListView::getItemBooleanProperty) {
            @Override
            public void updateItem(Attribute attribute, boolean empty) {
                super.updateItem(attribute, empty);
                setText(attribute == null ? "" : attribute.getValue());
            }
        });

        setParameters = new ArrayList<>(Arrays.asList(
                new Attribute("position", strings.getString("position")),
                new Attribute("opponentPosition", strings.getString("opponentPosition")),
                new Attribute("startZone", strings.getString("receiveZone")),
                new Attribute("endZone", strings.getString("setZone")),
                new Attribute("setterCmb", strings.getString("setterCalls")),
                new Attribute("recDefQ", strings.getString("recDefQuality")),
                new Attribute("lastSetTrg", strings.getString("lastSetTrg")),
                new Attribute("attackAttempt", strings.getString("attackAttempt")),
                new Attribute("sideOutAttempt", strings.getString("sideOutAttempt")),
                new Attribute("setPhase", strings.getString("setPhase")),
                new Attribute("setNr", strings.getString("setNr")),
                new Attribute("time", strings.getString("gameTime"))));

        attackParameters = new ArrayList<>(Arrays.asList(
                new Attribute("position", strings.getString("position")),
                new Attribute("opponentPosition", strings.getString("opponentPosition")),
                new Attribute("startZone", strings.getString("attackZone")),
                new Attribute("type", strings.getString("setType")),
                new Attribute("skillType", strings.getString("attackType")),
                new Attribute("attackCmb", strings.getString("attackCmb")),
                new Attribute("setQ", strings.getString("setQuality")),
                new Attribute("attackAttempt", strings.getString("attackAttempt")),
                new Attribute("playerAttackAttempt", strings.getString("playerAttackAttempt")),
                new Attribute("players", strings.getString("playersInBlock")),
                new Attribute("sideOutAttempt", strings.getString("sideOutAttempt")),
                new Attribute("setPhase", strings.getString("setPhase")),
                new Attribute("setNr", strings.getString("setNr")),
                new Attribute("time", strings.getString("gameTime"))));

        receiveParameters = new ArrayList<>(Arrays.asList(
                new Attribute("position", strings.getString("position")),
                new Attribute("opponentPosition", strings.getString("opponentPosition")),
                new Attribute("startZone", strings.getString("serveZone")),
                new Attribute("endZone", strings.getString("receiveZone")),
                new Attribute("type", strings.getString("serveType")),
                new Attribute("sideOutAttempt", strings.getString("sideOutAttempt")),
                new Attribute("setPhase", strings.getString("setPhase")),
                new Attribute("setNr", strings.getString("setNr")),
                new Attribute("time", strings.getString("gameTime"))));

        controllersManager = ControllersManager.getInstance();
        controllersManager.setPredictionParametersController(this);
    }

    void setParameters(int actionType) {
        switch (actionType) {
            case 1:
                currentParameters = new ArrayList<>(setParameters);
                break;
            case 2:
                currentParameters = new ArrayList<>(attackParameters);
                break;
            case 3:
                currentParameters = new ArrayList<>(receiveParameters);
                break;
        }
        setAllParametersChecked();
    }

    void addRemoveParameter(boolean add, Attribute attribute) {
        if (add) {
            if (currentParameters.stream().noneMatch(o -> o.getKey().equals(attribute.getKey())) && currentParameters.size() > 0) {
                currentParameters.add(attribute);
            }
        } else {
            if (currentParameters.size() > 0 && currentParameters.stream().anyMatch(o -> o.getKey().equals(attribute.getKey()))) {
                for (Attribute a : currentParameters) {
                    if (a.getKey().equals(attribute.getKey())) {
                        currentParameters.remove(a);
                        break;
                    }
                }
            }
        }
        setAllParametersChecked();
    }

    void clearParametersList() {
        currentParameters = new ArrayList<>();
        parametersListView.setItems(FXCollections.observableArrayList(currentParameters));
    }

    private void setAllParametersChecked() {
        parametersListView.setItems(FXCollections.observableArrayList(currentParameters));
        for (int i = 0; i < currentParameters.size(); i++) {
            parametersListView.getCheckModel().check(i);
        }
    }

    List<Attribute> getAllAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>(parametersListView.getCheckModel().getCheckedItems());
        attributes.add(0, controllersManager.getPredictionOptionsController().getResultClass());
        return attributes;
    }

    int getSupport() {
        return support;
    }

    int getConfidence() {
        return confidence;
    }

    boolean getAutoSelection() {
        return isAutoSelection;
    }

}
