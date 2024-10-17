package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.database.entity.Player;
import model.database.reader.Attribute;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PredictionOptionsController implements Initializable {

    public ToggleGroup whatTypeGroup;
    public ToggleGroup resultClassGroup;
    public RadioButton setRadioButton;
    public RadioButton attackRadioButton;
    public RadioButton receiveRadioButton;
    public RadioButton result1RadioButton;
    public RadioButton result2RadioButton;
    public RadioButton result3RadioButton;

    public Label selectPredictLabel;
    public Label whatKindLabel;
    public Label resultClassLabel;

    public RadioButton kindRadio1;
    public RadioButton kindRadio2;
    public RadioButton kindRadio3;
    public CheckBox checkBox1;
    public ChoiceBox<String> choiceBox1;
    public CheckBox checkBox2;
    public ChoiceBox<String> choiceBox2;

    public ToggleGroup whatKindGroup;

    private ResourceBundle strings;
    private ControllersManager controllersManager;

    private ObservableList<String> setterCalls;
    private ObservableList<String> setPhases;
    private ObservableList<String> recQuality;
    private ObservableList<String> oppositeAttackCmb;
    private ObservableList<String> outsideAttackCmb;
    private ObservableList<String> middleAttackCmb;
    private ObservableList<String> attackTypes;

    private Map<String, String> filters;

    private int actionType;
    private Attribute resultClass;
    private Attribute setterCmbAttribute;
    private Attribute setPhaseAttribute;
    private Attribute attackAttemptAttribute;
    private Attribute receiverAttribute;
    private Attribute serveTypeAttribute;
    private Attribute skillTypeAttribute;
    private Attribute attackCmbAttribute;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        strings = resources;
        setterCmbAttribute = new Attribute("setterCmb", strings.getString("setterCalls"));
        setPhaseAttribute = new Attribute("setPhase", strings.getString("setPhase"));
        attackAttemptAttribute = new Attribute("attackAttempt", strings.getString("attackAttempt"));
        receiverAttribute = new Attribute("receiver", strings.getString("receiver"));
        serveTypeAttribute = new Attribute("type", strings.getString("serveType"));
        skillTypeAttribute = new Attribute("skillType", strings.getString("attackType"));
        attackCmbAttribute = new Attribute("attackCmb", strings.getString("attackCmb"));
        selectPredictLabel.setText(strings.getString("selectPredict"));
        whatKindLabel.setText(strings.getString("whatKind"));
        resultClassLabel.setText(strings.getString("resultClass"));
        setRadioButton.setText(strings.getString("radioSet"));
        attackRadioButton.setText(strings.getString("attack"));
        receiveRadioButton.setText(strings.getString("receive"));
        whatTypeGroup.selectedToggleProperty().addListener((ob, o, n) -> {
            filters.clear();
            RadioButton rb = (RadioButton) whatTypeGroup.getSelectedToggle();
            if (rb != null) {
                if (rb == setRadioButton) {
                    actionType = 1;
                    filters.put("skill", "='E'");
                } else if (rb == attackRadioButton) {
                    actionType = 2;
                    filters.put("skill", "='A'");
                } else {
                    actionType = 3;
                    filters.put("skill", "='R'");
                }
                showResultOptions();
                controllersManager.getPredictionParametersController().setParameters(actionType);
            }
        });
        resultClassGroup.selectedToggleProperty().addListener((ob, o, n) -> {
            RadioButton rb = (RadioButton) resultClassGroup.getSelectedToggle();
            if (rb != null) {
                if (rb == result1RadioButton) {
                    showPredictionOptions(1);
                } else if (rb == result2RadioButton) {
                    showPredictionOptions(2);
                } else if (rb == result3RadioButton) {
                    showPredictionOptions(3);
                }
            }
        });
        setterCalls = FXCollections.observableArrayList("K1", "K2", "K7", "KE", "KC", "KK");
        setPhases = FXCollections.observableArrayList("1", "2", ">2", "<3", "3", "4", "0");
        recQuality = FXCollections.observableArrayList("(+,#) vs (!,-,/,=)", "(+,#,!) vs (-,/,=)", "(+,#) vs (!,-) vs (/,=)", "-,/,=", "#,+,!");
        oppositeAttackCmb = FXCollections.observableArrayList("X6", "X8", "X5", "V6", "V8", "V5");
        outsideAttackCmb = FXCollections.observableArrayList("X6", "X5", "XP", "V6", "V5");
        middleAttackCmb = FXCollections.observableArrayList("X1", "X7", "X2");
        attackTypes = FXCollections.observableArrayList("H", "T", "P");
        checkBox1.selectedProperty().addListener((observable, oldValue, newValue) -> {
            onOffChoiceBox1(!newValue);
            if (actionType == 1) {
                controllersManager.getPredictionParametersController().addRemoveParameter(!newValue, setterCmbAttribute);
            } else if (actionType == 2) {
                controllersManager.getPredictionParametersController().addRemoveParameter(!newValue, attackCmbAttribute);
            }
        });
        choiceBox1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (actionType == 1) {
                filters.put("setterCmb", "='" + newValue + "'");
            } else if (actionType == 2) {
                filters.put("attackCmb", "='" + newValue + "'");
            } else if (actionType == 3) {
                putQualityFilter(newValue);
            }
        });

        checkBox2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            onOffChoiceBox2(!newValue);
            if (actionType == 1) {
                controllersManager.getPredictionParametersController().addRemoveParameter(!newValue, setPhaseAttribute);
            } else if (actionType == 2) {
                controllersManager.getPredictionParametersController().addRemoveParameter(!newValue, skillTypeAttribute);
            }
        });
        choiceBox2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (actionType == 1) {
                if (newValue.length() == 1) {
                    filters.put("setPhase", "=" + newValue);
                } else {
                    filters.put("setPhase", newValue);
                }
            } else if (actionType == 2) {
                filters.put("skillType", "='" + newValue + "'");
            }
        });
        whatKindGroup.selectedToggleProperty().addListener((ob, o, n) -> {
            RadioButton rb = (RadioButton) whatKindGroup.getSelectedToggle();
            if (rb != null) {
                boolean add = (rb == kindRadio1);
                if (actionType < 3) {
                    controllersManager.getPredictionParametersController().addRemoveParameter(add, attackAttemptAttribute);
                    if (add) {
                        filters.remove("attackAttempt");
                        controllersManager.getPredictionParametersController().addRemoveParameter(false, receiverAttribute);
                    } else {
                        if (rb == kindRadio2) {
                            filters.put("attackAttempt", "=1");
                            controllersManager.getPredictionParametersController().addRemoveParameter(true, receiverAttribute);
                        } else {
                            filters.put("attackAttempt", ">1");
                            controllersManager.getPredictionParametersController().addRemoveParameter(false, receiverAttribute);
                        }
                    }
                } else {
                    controllersManager.getPredictionParametersController().addRemoveParameter(add, serveTypeAttribute);
                    if (add) {
                        filters.remove("type");
                    } else {
                        if (rb == kindRadio2) {
                            filters.put("type", "='M'");
                        } else {
                            filters.put("type", "='Q'");
                        }
                    }
                }
            }
        });
        filters = new HashMap<>();
        controllersManager = ControllersManager.getInstance();
        controllersManager.setPredictionOptionsController(this);
    }

    Attribute getResultClass() {
        return resultClass;
    }

    Map<String, String> getFilters() {
        return filters;
    }

    void enablePredicions() {
        disablePredictions();
        Player analyzedPlayer = controllersManager.getMainController().getAnalyzedPlayer();
        if (analyzedPlayer != null) {
            switch (analyzedPlayer.getPosition()) {
                case 1:
                    receiveRadioButton.setDisable(false);
                    receiveRadioButton.setSelected(true);
                    break;
                case 2:
                    receiveRadioButton.setDisable(false);
                case 3:
                case 4:
                    attackRadioButton.setDisable(false);
                    attackRadioButton.setSelected(true);
                    break;
                case 5:
                    setRadioButton.setDisable(false);
                    setRadioButton.setSelected(true);
                    break;
            }
        }
    }

    void disablePredictions() {
        setRadioButton.setDisable(true);
        attackRadioButton.setDisable(true);
        receiveRadioButton.setDisable(true);
        result1RadioButton.setDisable(true);
        result2RadioButton.setDisable(true);
        result3RadioButton.setDisable(true);
        result1RadioButton.setText("");
        result2RadioButton.setText("");
        result3RadioButton.setText("");
        kindRadio1.setDisable(true);
        kindRadio2.setDisable(true);
        kindRadio3.setDisable(true);
        kindRadio1.setText("");
        kindRadio2.setText("");
        kindRadio3.setText("");
        checkBox1.setDisable(true);
        checkBox2.setDisable(true);
        checkBox1.setSelected(false);
        checkBox2.setSelected(false);
        checkBox1.setText("");
        checkBox2.setText("");
        if (whatTypeGroup.getSelectedToggle() != null) {
            whatTypeGroup.getSelectedToggle().setSelected(false);
        }
        if (resultClassGroup.getSelectedToggle() != null) {
            resultClassGroup.getSelectedToggle().setSelected(false);
        }
        if (whatKindGroup.getSelectedToggle() != null) {
            whatKindGroup.getSelectedToggle().setSelected(false);
        }
        controllersManager.getPredictionParametersController().clearParametersList();
        filters.clear();
    }

    private void showResultOptions() {
        result1RadioButton.setDisable(false);
        result1RadioButton.setSelected(false);
        result1RadioButton.setSelected(true);
        switch (actionType) {
            case 1:
                result2RadioButton.setDisable(false);
                result3RadioButton.setDisable(false);
                result1RadioButton.setText(strings.getString("direction"));
                result2RadioButton.setText(strings.getString("toWhichPlayer"));
                result3RadioButton.setText(strings.getString("toWhichZone"));
                break;
            case 2:
                result2RadioButton.setDisable(false);
                result3RadioButton.setDisable(true);
                result1RadioButton.setText(strings.getString("direction"));
                result2RadioButton.setText(strings.getString("pitchSide"));
                result3RadioButton.setText("");
                break;
            case 3:
                result2RadioButton.setDisable(true);
                result3RadioButton.setDisable(true);
                result1RadioButton.setText(strings.getString("quality"));
                result2RadioButton.setText("");
                break;
        }
    }

    private void showPredictionOptions(int result) {
        kindRadio1.setSelected(true);
        kindRadio1.setDisable(false);
        kindRadio2.setDisable(false);
        kindRadio3.setDisable(false);
        checkBox1.setDisable(true);
        checkBox2.setDisable(true);
        checkBox1.setSelected(false);
        checkBox2.setSelected(false);
        checkBox1.setText("");
        checkBox2.setText("");
        switch (actionType) {
            case 1:
                checkBox1.setText(strings.getString("setterCalls") + ":");
                checkBox2.setText(strings.getString("setPhase") + ":");
                if (result == 1) {
                    resultClass = new Attribute("trg", strings.getString("direction"));
                } else if (result == 2) {
                    resultClass = new Attribute("attacker", strings.getString("toWhichPlayer"));
                } else if (result == 3) {
                    resultClass = new Attribute("setZone", strings.getString("toWhichZone"));
                }
            case 2:
                kindRadio1.setText(strings.getString("allActions"));
                kindRadio2.setText(strings.getString("firstActions"));
                kindRadio3.setText(strings.getString("counterattacks"));
                checkBox1.setDisable(false);
                checkBox2.setDisable(false);
                if (actionType == 2) {
                    checkBox1.setText(strings.getString("attackCmb"));
                    checkBox2.setText(strings.getString("attackType"));
                    if (result == 1) {
                        resultClass = new Attribute("endZone", strings.getString("direction"));
                    } else {
                        resultClass = new Attribute("attackDirection", strings.getString("pitchSide"));
                    }
                }
                break;
            case 3:
                kindRadio1.setText(strings.getString("allReceptions"));
                kindRadio2.setText(strings.getString("floatReceptions"));
                kindRadio3.setText(strings.getString("jumpReceptions"));
                checkBox1.setDisable(false);
                checkBox1.setText(strings.getString("quality"));
                resultClass = new Attribute("quality", strings.getString("quality"));
                break;
        }
    }

    private void onOffChoiceBox1(boolean disable) {
        choiceBox1.setDisable(disable);
        if (!disable) {
            if (actionType == 1) {
                choiceBox1.setItems(setterCalls);
                if (choiceBox1.getValue() != null) {
                    filters.put("setterCmb", "='" + choiceBox1.getValue() + "'");
                }
            } else if (actionType == 2) {
                switch (controllersManager.getMainController().getAnalyzedPlayer().getPosition()) {
                    case 2:
                        choiceBox1.setItems(outsideAttackCmb);
                        break;
                    case 3:
                        choiceBox1.setItems(oppositeAttackCmb);
                        break;
                    case 4:
                        choiceBox1.setItems(middleAttackCmb);
                        break;
                }
                filters.put("attackCmb", "='" + choiceBox1.getValue() + "'");
            } else if (actionType == 3) {
                choiceBox1.setItems(recQuality);
                putQualityFilter(choiceBox1.getValue());
            }
            choiceBox1.setValue(choiceBox1.getItems().get(0));
        } else {
            if (actionType == 1) {
                filters.remove("setterCmb");
            } else if (actionType == 2) {
                filters.remove("attackCmb");
            }
        }
    }

    private void putQualityFilter(String newValue) {
        String value = newValue;
        if (choiceBox1.getValue() != null) {
            for (int i = 0; i < 3; i++) {
                if (recQuality.get(i).equals(value)) {
                    value = String.valueOf(i);
                    break;
                }
            }
        }
        filters.put("quality", value);
    }

    private void onOffChoiceBox2(boolean disable) {
        choiceBox2.setDisable(disable);
        if (!disable) {
            if (actionType == 1) {
                choiceBox2.setItems(setPhases);
                String selectedSetPhase = choiceBox2.getValue();
                if (selectedSetPhase != null) {
                    if (selectedSetPhase.length() == 1) {
                        filters.put("setPhase", "=" + selectedSetPhase);
                    } else {
                        filters.put("setPhase", selectedSetPhase);
                    }
                }
            } else if (actionType == 2) {
                choiceBox2.setItems(attackTypes);
                filters.put("skillType", "='" + choiceBox2.getValue() + "'");
            }
            choiceBox2.setValue(choiceBox2.getItems().get(0));
        } else {
            if (actionType == 1) {
                filters.remove("setPhase");
            } else if (actionType == 2) {
                filters.remove("skillType");
            }
        }
    }
}
