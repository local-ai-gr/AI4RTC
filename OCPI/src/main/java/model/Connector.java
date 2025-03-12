/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class Connector {

    private String id;
    private String standard;
    private String format;
    private String power_type;
    private int voltage;
    private int amperage;
    private long max_electric_power;
    private String last_updated;

    @Override
    public String toString() {
        return " - Standard: "+standard+"<p> - powerType: " + power_type + "<p> - voltage: " + voltage + "<p> - amperage: " + amperage + "<p> - max_electric_power: " + max_electric_power ;
    }
    
    /**
     * @return the max_electric_power
     */
    public long getMax_electric_power() {
        return max_electric_power;
    }

    /**
     * @param max_electric_power the max_electric_power to set
     */
    public void setMax_electric_power(int max_electric_power) {
        this.max_electric_power = max_electric_power;
    }

    /**
     * @return the last_updated
     */
    public String getLast_updated() {
        return last_updated;
    }

    /**
     * @param last_updated the last_updated to set
     */
    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the standard
     */
    public String getStandard() {
        return standard;
    }

    /**
     * @param standard the standard to set
     */
    public void setStandard(String standard) {
        this.standard = standard;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the power_type
     */
    public String getPower_type() {
        return power_type;
    }

    /**
     * @param power_type the power_type to set
     */
    public void setPower_type(String power_type) {
        this.power_type = power_type;
    }

    /**
     * @return the voltage
     */
    public int getVoltage() {
        return voltage;
    }

    /**
     * @param voltage the voltage to set
     */
    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    /**
     * @return the amperage
     */
    public int getAmperage() {
        return amperage;
    }

    /**
     * @param amperage the amperage to set
     */
    public void setAmperage(int amperage) {
        this.amperage = amperage;
    }

    // Getters and setters
}
