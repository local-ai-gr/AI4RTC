package model;

import simulation.DimensionType;

public class Dimension {

    private DimensionType type;
    private double volume;

    public Dimension() {
    }

    public Dimension(DimensionType type, double volume) {
        this.type = type;
        this.volume = volume;

    }

    @Override
    public String toString() {
        return "Dimension{" + "type=" + type + ", volume=" + volume + '}';
    }

    /**
     * @return the type
     */
    public DimensionType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(DimensionType type) {
        this.type = type;
    }

    /**
     * @return the volume
     */
    public double getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(double volume) {
        this.volume = volume;
    }

}
