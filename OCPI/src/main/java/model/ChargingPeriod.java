/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
// Getters and setters for the fie

import java.util.List;

public class ChargingPeriod {
    private String start_date_time;
    private List<Dimension> dimensions;
    /**
     * @return the start_date_time
     */
    public String getStart_date_time() {
        return start_date_time;
    }

    /**
     * @param start_date_time the start_date_time to set
     */
    public void setStart_date_time(String start_date_time) {
        this.start_date_time = start_date_time;
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


    public class Dimension {

        private String type;
        private double volume;

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(String type) {
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
