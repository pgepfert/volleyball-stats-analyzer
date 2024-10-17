package model.database.reader;

import model.database.entity.Game;
import model.database.entity.Player;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public class InstancesReader {

    private static InstancesReader instance;

    private InstanceQuery instanceQuery;
    private QueryMaker queryMaker;

    private InstancesReader() throws Exception {
        File file = new File(getClass().getResource("DatabaseUtils.props").getFile());
        instanceQuery = new InstanceQuery();
        instanceQuery.setCustomPropsFile(file);
        Preferences preferences = Preferences.userRoot().node("volleyball-analyzer");
        String databaseURL = preferences.get("database", "jdbc:sqlite::resource:database.db");
        instanceQuery.setDatabaseURL(databaseURL);
        queryMaker = new QueryMaker();
    }

    public static InstancesReader getInstance() throws Exception {
        if (instance == null) {
            instance = new InstancesReader();
        }
        return instance;
    }

    public void setURL(String databaseURL) {
        try {
            instanceQuery.disconnectFromDatabase();
            instanceQuery.setDatabaseURL(databaseURL);
            instanceQuery.connectToDatabase();
            instanceQuery.disconnectFromDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGamesToAnalyze(List<Game> selectedGames) {
        queryMaker.selectGames(selectedGames);
    }

    public Instances getData(Player player, List<Attribute> attributes, Map<String, String> filters, boolean selectAttributes) throws Exception {
        String query = queryMaker.createQuery(player, attributes, filters);
        instanceQuery.setQuery(query);
        Instances instances = instanceQuery.retrieveInstances();
        instances.setClassIndex(0);
        instances.deleteWithMissingClass();
        instances = discretizeInstances(attributes, instances);
        if (selectAttributes) {
            List<Integer> selectedAttributes = selectAttributes(instances);
            removeMisleadingAttributes(instances, selectedAttributes);
        }
        return instances;
    }

    public Instances getSetterData(Player player, Map<String, String> filters) throws Exception {
        String query = queryMaker.createSetsZonesQuery(player, filters);
        instanceQuery.setQuery(query);
        return instanceQuery.retrieveInstances();
    }

    public Instances getAttackData(Player player, String attackCmb) throws Exception {
        String query = queryMaker.createAttacksZonesQuery(player, attackCmb);
        instanceQuery.setQuery(query);
        return instanceQuery.retrieveInstances();
    }

    public Instances getAttackSummary(Player player) throws Exception {
        String query = queryMaker.createAttacksSummaryQuery(player);
        instanceQuery.setQuery(query);
        return instanceQuery.retrieveInstances();
    }

    public Instances getMiddleBlockersAttackSummary(Player player) throws Exception {
        String query = queryMaker.createMiddleBlockersAttacksSummaryQuery(player);
        instanceQuery.setQuery(query);
        return instanceQuery.retrieveInstances();
    }

    public Instances getReceptionSummary(Player player) throws Exception {
        String query = queryMaker.createReceptionSummaryQuery(player);
        instanceQuery.setQuery(query);
        return instanceQuery.retrieveInstances();
    }

    public Instances getServeSummary(Player player) throws Exception {
        String query = queryMaker.createServeSummaryQuery(player);
        instanceQuery.setQuery(query);
        return instanceQuery.retrieveInstances();
    }

    public Instances getPlayersWithMostActions(String skill) throws Exception {
        String query = queryMaker.createMostActionsQuery(skill);
        instanceQuery.setQuery(query);
        return instanceQuery.retrieveInstances();
    }

    public List<Player> sortPlayers(List<Player> playerList) {
        Map<Player, Double> playersActionsMap = new HashMap<>();
        for (Player player : playerList) {
            if (player.getId() != 0) {
                instanceQuery.setQuery(queryMaker.createNrOfActionsQuery(player.getId()));
                try {
                    Instance instance = instanceQuery.retrieveInstances().instance(0);
                    playersActionsMap.put(player, instance.value(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                playersActionsMap.put(player, Double.MAX_VALUE);
            }
        }
        Stream<Map.Entry<Player, Double>> sorted = playersActionsMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
        List<Player> resultList = new ArrayList<>();
        sorted.forEach(playerDoubleEntry -> resultList.add(playerDoubleEntry.getKey()));
        return resultList;
    }

    private List<Integer> selectAttributes(Instances instances) {
        AttributeSelection filter = new AttributeSelection();
        List<Integer> selectedAttributes = new ArrayList<>();
        try {
            Ranker search = new Ranker();
            search.setThreshold(0.05);
            InfoGainAttributeEval eval = new InfoGainAttributeEval();
            //ReliefFAttributeEval eval = new ReliefFAttributeEval();
            filter.setEvaluator(eval);
            filter.setSearch(search);
            filter.SelectAttributes(instances);
            System.out.println(filter.toResultsString());
            int[] selectedAttributesArray = filter.selectedAttributes();
            for (int i : selectedAttributesArray) {
                selectedAttributes.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectedAttributes;
    }

    public double[][] saveAttributesRanking(Player player, List<Attribute> attributes, Map<String, String> filters) throws Exception {
        String query = queryMaker.createQuery(player, attributes, filters);
        instanceQuery.setQuery(query);
        Instances instances = instanceQuery.retrieveInstances();
        instances.setClassIndex(0);
        instances.deleteWithMissingClass();
        instances = discretizeInstances(attributes, instances);
        AttributeSelection filter = new AttributeSelection();
        Ranker search = new Ranker();
        search.setThreshold(-0.05);
        InfoGainAttributeEval eval = new InfoGainAttributeEval();
        //ReliefFAttributeEval eval = new ReliefFAttributeEval();
        filter.setEvaluator(eval);
        filter.setSearch(search);
        filter.SelectAttributes(instances);
        System.out.println(player.getFullName());
        System.out.println(filter.toResultsString());
        return filter.rankedAttributes();
    }

    private void removeMisleadingAttributes(Instances instances, List<Integer> selectedAttributes) {
        for (int i = 1, j = 1; i < instances.numAttributes(); i++, j++) {
            if (!selectedAttributes.contains(j)) {
                instances.deleteAttributeAt(i);
                i--;
            }
        }
    }

    private Instances discretizeInstances(List<Attribute> attributes, Instances instances) throws Exception {
        for (int i = 1; i < attributes.size(); i++) {
            switch (attributes.get(i).getKey()) {
                case "attackAttempt":
                    instances = discretizeAttribute(instances, i, 3);
                    break;
                case "playerAttackAttempt":
                    instances = discretizeAttribute(instances, i, 4);
                    break;
                case "setNr":
                    instances = discretizeAttribute(instances, i, 5);
                    break;
            }
        }
        return instances;
    }

    private Instances discretizeAttribute(Instances instances, int index, int bins) throws Exception {
        Discretize disTransform = new Discretize();
        disTransform.setBins(bins);
        disTransform.setAttributeIndicesArray(new int[]{index});
        disTransform.setUseEqualFrequency(true);
        disTransform.setInputFormat(instances);
        return Filter.useFilter(instances, disTransform);
    }
}
