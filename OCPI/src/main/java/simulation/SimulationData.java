/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulation;

public class SimulationData {

    private final String locationId;
    private final int MAXIMUM_PLAN;
    private final int HIGH_PLAN;
    private final int MEDIUM_PLAN;
    private final int LOW_PLAN;
    private final int numberOfConnectors;
    private final int visitsExpected;

    public SimulationData(String locationId, int MAXIMUM_PLAN, int HIGH_PLAN, int MEDIUM_PLAN, int LOW_PLAN, int numberOfConnectors, int visitsExpected) {
        this.locationId = locationId;
        this.MAXIMUM_PLAN = MAXIMUM_PLAN;
        this.HIGH_PLAN = HIGH_PLAN;
        this.MEDIUM_PLAN = MEDIUM_PLAN;
        this.LOW_PLAN = LOW_PLAN;
        this.numberOfConnectors = numberOfConnectors;
        this.visitsExpected = visitsExpected;
    }

    /**
     * @return the locationId
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * @return the MAXIMUM_PLAN
     */
    public int getMAXIMUM_PLAN() {
        return MAXIMUM_PLAN;
    }

    /**
     * @return the HIGH_PLAN
     */
    public int getHIGH_PLAN() {
        return HIGH_PLAN;
    }

    /**
     * @return the MEDIUM_PLAN
     */
    public int getMEDIUM_PLAN() {
        return MEDIUM_PLAN;
    }

    /**
     * @return the LOW_PLAN
     */
    public int getLOW_PLAN() {
        return LOW_PLAN;
    }

    /**
     * @return the numberOfConnectors
     */
    public int getNumberOfConnectors() {
        return numberOfConnectors;
    }

    /**
     * @return the visitsExpected
     */
    public int getVisitsExpected() {
        return visitsExpected;
    }

}
