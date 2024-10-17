package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Cp1250Control;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class MainWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static void load(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("main.fxml"),
                ResourceBundle.getBundle("Strings", Locale.getDefault(), new Cp1250Control()));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Volleyball Stats Analyzer");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Preferences preferences = Preferences.userRoot().node("volleyball-analyzer");
        String language = preferences.get("language", "");
        switch (language) {
            case "en":
                Locale.setDefault(Locale.ENGLISH);
                break;
            case "pl":
                Locale.setDefault(new Locale("pl", "PL"));
                break;
        }
        load(stage);
    }
}
