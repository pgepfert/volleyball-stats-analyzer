package controller;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
import weka.gui.visualize.VisualizePanel;

import javax.swing.*;
import java.awt.*;

public class VisualizeController {

    public static void visualizeData(Instances instances) {
        VisualizePanel vp = new VisualizePanel();
        vp.setName("Number of instances: " + instances.numInstances());
        vp.setInstances(instances);

        String plotName = vp.getName();
        final JFrame jf = new JFrame("Weka Clusterer Visualize: " + plotName);
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
            }
        });
        jf.setSize(1000, 800);
        jf.getContentPane().setLayout(new BorderLayout());
        jf.getContentPane().add(vp, BorderLayout.CENTER);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }

    public static void visualizeRules(String rules) {
        JFrame jf = new JFrame("Associacion rules:");
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
            }
        });

        JPanel jPanel = new JPanel(new GridBagLayout());
        JTextArea textArea = new JTextArea(40, 60);
        textArea.setEditable(false);
        textArea.setText(rules);
        JScrollPane sp = new JScrollPane(textArea);
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        jPanel.add(sp, c);
        jf.add(jPanel);
        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }

    public static void visualizeTree(J48 cls) throws Exception {
        JFrame jf = new JFrame("Weka Classifier Tree Visualizer: J48");
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
            }
        });
        jf.setSize(2000, 800);
        jf.getContentPane().setLayout(new BorderLayout());
        TreeVisualizer tv = new TreeVisualizer(null,
                cls.graph(),
                new PlaceNode2());
        jf.getContentPane().add(tv, BorderLayout.CENTER);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        tv.fitToScreen();
    }
}
