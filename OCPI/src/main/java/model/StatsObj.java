package model;

public class StatsObj {

    final private String label;
    final private double val;

    public StatsObj(String label, double val) {
        this.label = label;
        this.val = val;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the val
     */
    public double getVal() {
        return val;
    }
}
