package controller.utils;

import javafx.concurrent.Task;
import model.database.writer.GameFileParser;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

public class ReadFileTask extends Task<List<File>> {

    private List<File> files;
    private GameFileParser gameFileParser;
    private ResourceBundle strings;

    public ReadFileTask(List<File> files, ResourceBundle strings) {
        this.files = files;
        this.gameFileParser = new GameFileParser();
        this.strings = strings;
    }

    @Override
    protected List<File> call() throws Exception {
        int nrOfGames = files.size();
        for (int i=0; i<nrOfGames; i++) {
            this.updateProgress(i*2 + 1, nrOfGames * 2);
            this.updateMessage(i+1 + "/" + nrOfGames);
            this.read(files.get(i));
            this.updateProgress(i*2 + 2, nrOfGames * 2);
        }
        return files;
    }

    private void read(File file) throws Exception {
        this.updateTitle(strings.getString("loadingGame") + file.getName());
        gameFileParser.readFile(file.getPath());
        Thread.sleep(500);
    }

}