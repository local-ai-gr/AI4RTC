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
    private String end_date_time;
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
            return 0;
        }
    }

    public double getChargingPeriodEnergy() {
        Optional<Dimension> dim = dimensions.stream().filter(d -> d.getType() == DimensionType.ENERGY).findAny();
        if (dim.isPresent()) {
            return dim.get().getVolume();
        } else {
            return 0;
        }
    }

    public void setChargingPeriodEnergy(double energy) {
        for (Dimension d : dimensions) {
            if (d.getType() == DimensionType.ENERGY) {
                d.setVolume(energy);
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "ChargingPeriod{" + "start_date_time=" + start_date_time + ", end_date_time=" + end_date_time + ", dimensions=" + dimensions + '}';
    }

    public String getStart_date_time() {
        return start_date_time;
    }

    public LocalDateTime getStartLocalDateTime() {
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
     * @return the end_date_time
     */
    public String getEnd_date_time() {
        return end_date_time;
    }

    public LocalDateTime getEndLocalDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return LocalDateTime.parse(end_date_time, formatter);
    }

    /**
     * @param end_date_time the end_date_time to set
     */
    public void setEnd_date_time(String end_date_time) {
        this.end_date_time = end_date_time;
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
}
