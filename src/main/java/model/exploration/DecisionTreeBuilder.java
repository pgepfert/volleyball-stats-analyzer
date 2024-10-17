package model.exploration;

import model.database.entity.Player;
import model.database.reader.Attribute;
import model.database.reader.InstancesReader;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class DecisionTreeBuilder {

    public J48 buildTree(Player player, List<Attribute> attributes, Map<String, String> filters,
                         int support, int confidence, boolean isAutoselection) throws Exception {
        Instances instances = InstancesReader.getInstance().getData(player, attributes, filters, isAutoselection);
        int folds = 10;
        J48 decisionTree = new J48();
        decisionTree.setMinNumObj(support);
        decisionTree.setConfidenceFactor((100f - confidence) / 100);
        decisionTree.buildClassifier(instances);
        Evaluation eval = new Evaluation(instances);
        eval.crossValidateModel(decisionTree, instances, folds, new Random(10));
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.pctCorrect());
        System.out.println("Tree size: " + decisionTree.measureTreeSize());
        return decisionTree;
    }

    public double[][] collectResults(Player player, List<Attribute> attributes, Map<String, String> filters, boolean isAutoselection) throws Exception {
        double[][] pctCorrectResults = new double[50][24];
        Instances instances = InstancesReader.getInstance().getData(player, attributes, filters, isAutoselection);
        J48 decisionTree = new J48();
        int folds = 10;
        for (int i = 50; i < 100; i++) {
            for (int j = 2; j < 26; j++) {
                decisionTree.setMinNumObj(j);
                decisionTree.setConfidenceFactor((100f - i) / 100);
                Evaluation eval = new Evaluation(instances);
                eval.crossValidateModel(decisionTree, instances, folds, new Random(10));
                double val = eval.pctCorrect() * 1000;
                val = Math.round(val);
                pctCorrectResults[i - 50][j - 2] = val / 1000;
            }
        }
        return pctCorrectResults;
    }

    public J48 buildTreeTrainTest(Player player, List<Attribute> attributes, Map<String, String> filters,
                                  int support, int confidence, boolean isAutoselection) throws Exception {
        Instances instances = InstancesReader.getInstance().getData(player, attributes, filters, isAutoselection);
        J48 decisionTree = new J48();
        decisionTree.setMinNumObj(support);
        decisionTree.setConfidenceFactor((100f - confidence) / 100);
        int trainSize = (int) Math.round(instances.numInstances() * 0.8);
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);
        decisionTree.buildClassifier(train);
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(decisionTree, test);
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.pctCorrect());
        return decisionTree;
    }

    public double[][] collectResultsTrainTest(Player player, List<Attribute> attributes, Map<String, String> filters, boolean isAutoselection) throws Exception {
        double[][] pctCorrectResults = new double[50][24];
        Instances instances = InstancesReader.getInstance().getData(player, attributes, filters, isAutoselection);
        J48 decisionTree = new J48();
        for (int i = 50; i < 100; i++) {
            for (int j = 2; j < 26; j++) {
                decisionTree.setMinNumObj(j);
                decisionTree.setConfidenceFactor((100f - i) / 100);
                int trainSize = (int) Math.round(instances.numInstances() * 0.8);
                int testSize = instances.numInstances() - trainSize;
                Instances train = new Instances(instances, 0, trainSize);
                Instances test = new Instances(instances, trainSize, testSize);
                decisionTree.buildClassifier(train);
                Evaluation eval = new Evaluation(train);
                eval.evaluateModel(decisionTree, test);
                double val = eval.pctCorrect() * 1000;
                val = Math.round(val);
                pctCorrectResults[i - 50][j - 2] = val / 1000;
            }
        }
        return pctCorrectResults;
    }
}
