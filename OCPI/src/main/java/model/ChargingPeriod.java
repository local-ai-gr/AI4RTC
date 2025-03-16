/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
// Getters and setters for the fie

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import simulation.DimensionType;

public class ChargingPeriod {

    private String start_date_time;
    private List<Dimension> dimensions = new ArrayList<>();

    /**
     */
    public ChargingPeriod() {
    }

    public ChargingPeriod(LocalDateTime start_date_time, double energy, double power) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        this.start_date_time = start_date_time.format(formatter);
        dimensions.add(new Dimension(DimensionType.ENERGY, energy));
        dimensions.add(new Dimension(DimensionType.POWER, power));
    }

    public double getChargingPeriodPower() {
        Optional<Dimension> dim = dimensions.stream().filter(d -> d.getType() == DimensionType.POWER).findAny();
        if (dim.isPresent()) {
            return dim.get().getVolume();
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "ChargingPeriod{" + "start_date_time=" + start_date_time + ", dimensions=" + dimensions+ '}';
    }

    public LocalDateTime getStart_date_time() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return LocalDateTime.parse(start_date_time, formatter);
    }

    /**
     * @param start_date_time the start_date_time to set
     */
    public void setStart_date_time(LocalDateTime start_date_time) {
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        this.start_date_time = start_date_time.format(formatter);
    }

    /**
     * @return the dimensions
     */
    public List<Dimension> getDimensions() {
        return dimensions;
    }

    /**
     * @param dimensions the dimensions to set
     */
    public void setDimensions(List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    //************************************
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

}
