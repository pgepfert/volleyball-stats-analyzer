package model.database.writer;

import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.io.Writer;

class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ',';

    static void writePlayerAndZonesStartData(String name, Writer w) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(DEFAULT_SEPARATOR);
        for (int i = 1; i < 10; i++) {
            sb.append(i).append(DEFAULT_SEPARATOR);
        }
        sb.append("E").append("\n");
        w.append(sb.toString());
    }

    static void writePlayZones(String header, Writer w, Instances data) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(header);
        int currentZone = 1;
        int sum = 0;
        for (int i = 0; i < data.numInstances(); i++) {
            Instance currInst = data.instance(i);
            try {
                int zone = Integer.valueOf(currInst.toString(0));
                if (zone < 1 || zone > 9) {
                    continue;
                }
                while (zone != currentZone) {
                    sb.append(DEFAULT_SEPARATOR).append(0);
                    currentZone++;
                }
                int instances = Integer.valueOf(currInst.toString(1));
                sb.append(DEFAULT_SEPARATOR).append(instances);
                sum += instances;
                currentZone++;
            } catch (NumberFormatException ignored) {
            }
        }
        for (; currentZone < 10; currentZone++) {
            sb.append(DEFAULT_SEPARATOR).append(0);
        }
        sb.append(DEFAULT_SEPARATOR).append(sum);
        sb.append("\n");
        w.append(sb.toString());
    }

    static void writeAttacksSummary(Writer w, Instances data) throws IOException {
        StringBuilder sb = new StringBuilder();
        Instance instance = data.instance(0);
        for (int i = 0; i < instance.numAttributes(); i++) {
            sb.append(instance.attribute(i).name()).append(DEFAULT_SEPARATOR);
        }
        sb.append("\n");
        for (int i = 0; i < instance.numAttributes(); i++) {
            try {
                int value = Integer.valueOf(instance.toString(i));
                sb.append(value).append(DEFAULT_SEPARATOR);
            } catch (NumberFormatException e) {
                sb.append(0).append(DEFAULT_SEPARATOR);
            }
        }
        sb.append("\n");
        w.append(sb.toString());
    }

    static void writeServeReceptionSummary(String name, Writer w, Instances data) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(DEFAULT_SEPARATOR);
        Instance instance = data.instance(0);
        for (int i = 0; i < instance.numAttributes(); i++) {
            try {
                int value = Integer.valueOf(instance.toString(i));
                sb.append(value).append(DEFAULT_SEPARATOR);
            } catch (NumberFormatException e) {
                sb.append(0).append(DEFAULT_SEPARATOR);
            }
        }
        sb.append("\n");
        w.append(sb.toString());
    }

    static void writeAttributes(String header, Writer w, Instances data) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append(DEFAULT_SEPARATOR);
        Instance instance = data.instance(0);
        for (int i = 0; i < instance.numAttributes(); i++) {
            sb.append(instance.attribute(i).name()).append(DEFAULT_SEPARATOR);
        }
        sb.append("\n");
        w.append(sb.toString());
    }

    static void writeDecissionTreePctCorrectResults(Writer w, double[][] data) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (double[] rows : data) {
            for (double d : rows) {
                sb.append(d).append(DEFAULT_SEPARATOR);
            }
            sb.append("\n");
        }
        sb.append("\n");
        w.append(sb.toString());
    }
}
