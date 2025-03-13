/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.ChargingPeriod;

class ChargingSession {

    private final String sessionId;
    private final String connectorId;
    private final LocalDateTime startTime;
    private final LocalDateTime stopTime;
    private String status;
    double powerLevel;
    private final List<ChargingPeriod> charging_periods = new ArrayList<>();

    public ChargingSession(String sessionId, String connectorId, LocalDateTime startTime, LocalDateTime stopTime, double energyLevel, double powerLevel) {
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
            if (myChargingPeriod.getStart_date_time().isBefore(atTime) || myChargingPeriod.getStart_date_time().isEqual(atTime)) {
                if (nearestChargingPeriod == null || myChargingPeriod.getStart_date_time().isAfter(nearestChargingPeriod.getStart_date_time())) {
                    nearestChargingPeriod = myChargingPeriod;
                }
            }
        }
        return nearestChargingPeriod != null ? nearestChargingPeriod.getChargingPeriodPower() : getPowerLevel();
    }

    public void setPowerLevel(LocalDateTime myStartTime, double powerLevel) {
        this.powerLevel = powerLevel;
        charging_periods.add(new ChargingPeriod(startTime, 0, powerLevel));
    }

    @Override
    public String toString() {
        return "ChargingSession{id=" + sessionId + ", connector=" + connectorId + ", start=" + startTime + ", stop=" + stopTime + ", status=" + status + ", power=" + powerLevel + "kW}";
    }

}
