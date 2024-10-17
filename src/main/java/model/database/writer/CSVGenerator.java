package model.database.writer;

import model.database.entity.Player;
import model.database.reader.InstancesReader;
import weka.core.Instances;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

public class CSVGenerator {

    private String path;

    public CSVGenerator(String path) {
        this.path = path;
    }

    public void generateServeAndReceptionCSV(String name, List<Player> servingList, List<Player> receiversList) throws Exception {
        FileWriter writer = getFileWriter(name);
        boolean first = true;
        for (Player player : receiversList) {
            if (player.getId() != 0) {
                Instances instances = InstancesReader.getInstance().getReceptionSummary(player);
                if (instances != null) {
                    if (first) {
                        CSVUtils.writeAttributes("RECEPTION", writer, instances);
                        first = false;
                    }
                    CSVUtils.writeServeReceptionSummary(player.getFullName(), writer, instances);
                }
            }
        }
        writer.append("\n\n");
        first = true;
        for (Player player : servingList) {
            if (player.getId() != 0) {
                Instances instances = InstancesReader.getInstance().getServeSummary(player);
                if (instances != null) {
                    if (first) {
                        CSVUtils.writeAttributes("SERVE", writer, instances);
                        first = false;
                    }
                    CSVUtils.writeServeReceptionSummary(player.getFullName(), writer, instances);
                }
            }
        }
        writer.flush();
        writer.close();
    }

    public void generateSettersCSV(String name, List<Player> setterList) throws Exception {
        FileWriter writer = getFileWriter(name);
        for (Player player : setterList) {
            CSVUtils.writePlayerAndZonesStartData(player.getFullName(), writer);
            if (player.getId() != 0) {
                writeNumberOfPlays(player, "K1", writer);
                writeNumberOfPlays(player, "K7", writer);
                writeNumberOfPlays(player, "P1", writer);
                writeNumberOfPlays(player, "P2", writer);
                writeNumberOfPlays(player, "P3", writer);
                writeNumberOfPlays(player, "P4", writer);
                writeNumberOfPlays(player, "P5", writer);
                writeNumberOfPlays(player, "P6", writer);
                writeNumberOfPlays(player, "Z2", writer);
                writeNumberOfPlays(player, "Z4", writer);
                writeNumberOfPlays(player, "#", writer);
                writeNumberOfPlays(player, "KK", writer);
                writer.append("\n");
            }
        }
        writer.flush();
        writer.close();
    }

    public void generateOppositeAttacksCSV(String name, List<Player> oppositeList) throws Exception {
        FileWriter writer = getFileWriter(name);
        for (Player player : oppositeList) {
            CSVUtils.writePlayerAndZonesStartData(player.getFullName(), writer);
            if (player.getId() != 0) {
                writeNumberOfPlays(player, "X6", writer);
                writeNumberOfPlays(player, "X8", writer);
                writeNumberOfPlays(player, "X5", writer);
                writeNumberOfPlays(player, "V6", writer);
                writeNumberOfPlays(player, "V8", writer);
                writeNumberOfPlays(player, "V5", writer);
                Instances instances = InstancesReader.getInstance().getAttackSummary(player);
                if (instances != null) {
                    CSVUtils.writeAttacksSummary(writer, instances);
                }
                writer.append("\n");
            }
        }
        writer.flush();
        writer.close();
    }

    public void generateOutsideHittersAttacksCSV(String name, List<Player> outsideList) throws Exception {
        FileWriter writer = getFileWriter(name);
        for (Player player : outsideList) {
            CSVUtils.writePlayerAndZonesStartData(player.getFullName(), writer);
            if (player.getId() != 0) {
                writeNumberOfPlays(player, "X6", writer);
                writeNumberOfPlays(player, "X5", writer);
                writeNumberOfPlays(player, "XP", writer);
                writeNumberOfPlays(player, "V6", writer);
                writeNumberOfPlays(player, "V5", writer);
                Instances instances = InstancesReader.getInstance().getAttackSummary(player);
                if (instances != null) {
                    CSVUtils.writeAttacksSummary(writer, instances);
                }
                writer.append("\n");
            }
        }
        writer.flush();
        writer.close();
    }

    public void generateMiddleBlockersAttacksCSV(String name, List<Player> middleList) throws Exception {
        FileWriter writer = getFileWriter(name);
        for (Player player : middleList) {
            CSVUtils.writePlayerAndZonesStartData(player.getFullName(), writer);
            if (player.getId() != 0) {
                writeNumberOfPlays(player, "X1", writer);
                writeNumberOfPlays(player, "X7", writer);
                Instances instances = InstancesReader.getInstance().getMiddleBlockersAttackSummary(player);
                if (instances != null) {
                    CSVUtils.writeAttacksSummary(writer, instances);
                }
                writer.append("\n");
            }
        }
        writer.flush();
        writer.close();
    }

    public void generateResultsCSV(String prefix, String playerInfo, double[][] data) throws IOException {
        FileWriter writer = getFileWriter("/" + prefix + playerInfo + ".csv");
        CSVUtils.writeDecissionTreePctCorrectResults(writer, data);
        writer.flush();
        writer.close();
    }

    private FileWriter getFileWriter(String fileName) throws IOException {
        String filePath = path;
        if (fileName != null) {
            filePath += fileName;
        }
        return new FileWriter(filePath, Charset.forName("windows-1250"), true);
    }

    private void writeNumberOfPlays(Player player, String play, FileWriter writer) throws Exception {
        Instances instances = null;
        if (play.equals("KK")) {
            instances = InstancesReader.getInstance().getSetterData(player, new HashMap<>() {{
                put("attackAttempt", ">1");
            }});
            String playFull = "KK (without KE)";
            if (instances != null) {
                CSVUtils.writePlayZones(playFull, writer, instances);
            }
            return;
        } else if (play.startsWith("K")) {
            instances = InstancesReader.getInstance().getSetterData(player, new HashMap<>() {{
                put("setterCmb", "='" + play + "'");
                put("attackAttempt", "=1");
            }});
        } else if (play.startsWith("P")) {
            instances = InstancesReader.getInstance().getSetterData(player, new HashMap<>() {{
                put("position", "='" + play.substring(1, 2) + "'");
                put("attackAttempt", "=1");
            }});
        } else if (play.startsWith("Z")) {
            instances = InstancesReader.getInstance().getSetterData(player, new HashMap<>() {{
                put("endZone", "=" + play.substring(1, 2));
                put("attackAttempt", "=1");
            }});
        } else if (play.equals("#")) {
            instances = InstancesReader.getInstance().getSetterData(player, new HashMap<>() {{
                put("recDefQ", "='" + play + "'");
                put("attackAttempt", "=1");
            }});
        } else if (play.startsWith("X") || play.startsWith("V")) {
            instances = InstancesReader.getInstance().getAttackData(player, play);
        }
        if (instances != null) {
            CSVUtils.writePlayZones(play, writer, instances);
        }
    }
}
