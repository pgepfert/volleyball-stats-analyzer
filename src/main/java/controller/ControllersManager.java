package controller;

class ControllersManager {

    private static ControllersManager instance;
    private MainController mainController;
    private PredictionOptionsController predictionOptionsController;
    private PredictionParametersController predictionParametersController;

    static ControllersManager getInstance() {
        if (instance == null) {
            instance = new ControllersManager();
        }
        return instance;
    }

    MainController getMainController() {
        return mainController;
    }

    void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    PredictionOptionsController getPredictionOptionsController() {
        return predictionOptionsController;
    }

    void setPredictionOptionsController(PredictionOptionsController predictionOptionsController) {
        this.predictionOptionsController = predictionOptionsController;
    }

    PredictionParametersController getPredictionParametersController() {
        return predictionParametersController;
    }

    void setPredictionParametersController(PredictionParametersController predictionParametersController) {
        this.predictionParametersController = predictionParametersController;
    }
}
