package model.exploration;

import model.database.entity.Player;
import model.database.reader.Attribute;
import model.database.reader.InstancesReader;
import weka.associations.Apriori;
import weka.associations.ItemSet;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.car.JCBA;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class AssociationRulesBuilder {

    public Apriori buildRules(Player player, List<Attribute> attributes, Map<String, String> filters,
                              int support, int confidence, boolean isAutoSelection) throws Exception {
        Instances instances = InstancesReader.getInstance().getData(player, attributes, filters, isAutoSelection);
        final NumericToNominal filter = new NumericToNominal();
        int folds = 10;
        filter.setInputFormat(instances);
        instances = Filter.useFilter(instances, filter);
        if (instances.size() > 0) {
            Apriori apriori = new Apriori();
            apriori.setClassIndex(1);
            apriori.setCar(true);
            apriori.setMinMetric(((double) confidence) / 100);
            apriori.setLowerBoundMinSupport((double) support / instances.numInstances());
            apriori.setNumRules(100);
            JCBA jcba = new JCBA();
            jcba.setCarMiner(apriori);
            jcba.buildClassifier(instances);
            Evaluation eval = new Evaluation(instances);
            eval.crossValidateModel(jcba, instances, folds, new Random(10));
            System.out.println(jcba.toString());
            System.out.println(eval.toSummaryString());
            System.out.println(eval.toClassDetailsString());
            System.out.println(eval.pctCorrect());
            return apriori;
        } else {
            System.out.print("Not enough data provided");
        }
        return null;
    }

    public JCBA buildJCBATrainTest(Player player, List<Attribute> attributes, Map<String, String> filters,
                                  int support, int confidence, boolean isAutoselection) throws Exception {
        Instances instances = InstancesReader.getInstance().getData(player, attributes, filters, isAutoselection);
        int trainSize = (int) Math.round(instances.numInstances() * 0.8);
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);
        Apriori apriori = new Apriori();
        apriori.setClassIndex(1);
        apriori.setCar(true);
        apriori.setMinMetric(((double) confidence) / 100);
        apriori.setLowerBoundMinSupport((double) support / instances.numInstances());
        apriori.setNumRules(100);
        JCBA jcba = new JCBA();
        jcba.setCarMiner(apriori);
        jcba.buildClassifier(train);
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(jcba, test);
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.pctCorrect());
        return jcba;
    }

    public double[][] collectResults(Player player, List<Attribute> attributes, Map<String, String> filters, boolean isAutoSelection) throws Exception {
        double[][] pctCorrectResults = new double[50][24];
        Instances instances = InstancesReader.getInstance().getData(player, attributes, filters, isAutoSelection);
        final NumericToNominal filter = new NumericToNominal();
        int folds = 10;
        filter.setInputFormat(instances);
        JCBA jcba = new JCBA();
        Apriori apriori = new Apriori();
        apriori.setClassIndex(0);
        apriori.setCar(true);
        apriori.setNumRules(100);
        instances = Filter.useFilter(instances, filter);
        for (int i = 50; i < 100; i += 1) {
            for (int j = 2; j < 26; j += 1) {
                apriori.setMinMetric(((double) 100f - i) / 100);
                apriori.setLowerBoundMinSupport((double) j / instances.numInstances());
                jcba.setCarMiner(apriori);
                Evaluation eval = new Evaluation(instances);
                eval.crossValidateModel(jcba, instances, folds, new Random(10));
                double val = eval.pctCorrect() * 1000;
                val = Math.round(val);
                pctCorrectResults[i - 50][j - 2] = val / 1000;
            }
        }
        return pctCorrectResults;
    }
}
