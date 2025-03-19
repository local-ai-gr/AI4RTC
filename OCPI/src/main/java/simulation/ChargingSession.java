/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.ChargingPeriod;
import model.Session;

public class ChargingSession {

    private String location_id;
    private final String sessionId;
    private final String connectorId;
    private final LocalDateTime startTime;
    private final LocalDateTime stopTime;
    private String status;
    double powerLevel;
    private final List<ChargingPeriod> charging_periods = new ArrayList<>();

    public ChargingSession(String location_id, String sessionId, String connectorId, LocalDateTime startTime, LocalDateTime stopTime, double energyLevel, double powerLevel) {
        this.location_id=location_id;
        this.sessionId = sessionId;
        this.connectorId = connectorId;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.status = stopTime.isAfter(LocalDateTime.now()) ? "ACTIVE" : "COMPLETED";
        this.powerLevel = powerLevel;
        charging_periods.add(new ChargingPeriod(startTime, energyLevel, powerLevel));
    }

    /**
     * @return the charging_periods
     */
    public List<ChargingPeriod> getCharging_periods() {
        return charging_periods;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPowerLevel() {
        return powerLevel;
    }

    public double getPowerLevel(LocalDateTime atTime) {
        ChargingPeriod nearestChargingPeriod = null;
        for (ChargingPeriod myChargingPeriod : charging_periods) {
            if (myChargingPeriod.getStartLocalDateTime().isBefore(atTime) || myChargingPeriod.getStartLocalDateTime().isEqual(atTime)) {
                if (nearestChargingPeriod == null || myChargingPeriod.getStartLocalDateTime().isAfter(nearestChargingPeriod.getStartLocalDateTime())) {
                    nearestChargingPeriod = myChargingPeriod;
                }
            }
        }
        return nearestChargingPeriod != null ? nearestChargingPeriod.getChargingPeriodPower() : getPowerLevel();
    }

    public void setPowerLevel(LocalDateTime myStartTime, double powerLevel) {
        this.powerLevel = powerLevel;
        if (!charging_periods.isEmpty()){
           ChargingPeriod lastPeriod =  charging_periods.get(charging_periods.size()-1);//get the last
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
           lastPeriod.setEnd_date_time(myStartTime.format(formatter));// end of previous period
        }
        charging_periods.add(new ChargingPeriod(myStartTime, 0, powerLevel));
        
    }

    @Override
    public String toString() {
        return "ChargingSession{id=" + sessionId + ", connector=" + connectorId + ", start=" + startTime + ", stop=" + stopTime + ", status=" + status + ", power=" + powerLevel + "kW}";
    }

    /**
     * @return the location_id
     */
    public String getLocation_id() {
        return location_id;
    }

    /**
     * @param location_id the location_id to set
     */
    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public Session toSession() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Session mySessiom = new Session();
        mySessiom.setId(this.sessionId);
        mySessiom.setEvse_uid("ewsd-12321");
        mySessiom.setConnector_id(this.getConnectorId());
        mySessiom.setCharging_periods(this.getCharging_periods());
        mySessiom.setLocation_id(this.getLocation_id());
        mySessiom.setStatus(this.getStatus());        
        mySessiom.setStart_date_time(this.getStartTime().format(formatter));
        mySessiom.setEnd_date_time(this.getStopTime().format(formatter));        
        return mySessiom;
    }
}
