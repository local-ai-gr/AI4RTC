/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulation;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class ChargingSession {

    private final String sessionId;
    private final String connectorId;
    private final LocalDateTime startTime;
    private final LocalDateTime stopTime;
    private String status;
    int powerLevel;
    private final Map<LocalDateTime, Integer> powerLevels = new HashMap<>();

    public ChargingSession(String sessionId, String connectorId, LocalDateTime startTime, LocalDateTime stopTime, int powerLevel) {
        this.sessionId = sessionId;
        this.connectorId = connectorId;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.status = stopTime.isAfter(LocalDateTime.now()) ? "ACTIVE" : "COMPLETED";
        this.powerLevel = powerLevel;
        powerLevels.put(startTime, powerLevel);
    }

    /**
     * @return the powerLevels
     */
    public Map<LocalDateTime, Integer> getPowerLevels() {
        return powerLevels;
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

    public int getPowerLevel() {
        return powerLevel;
    }

    public int getPowerLevel(LocalDateTime atTime) {
        Map.Entry<LocalDateTime, Integer> nearestEntry = null;
        for (Map.Entry<LocalDateTime, Integer> entry : powerLevels.entrySet()) {
            if (entry.getKey().isBefore(atTime) || entry.getKey().isEqual(atTime)) {
                if (nearestEntry == null || entry.getKey().isAfter(nearestEntry.getKey())) {
                    nearestEntry = entry;
                }
            }
        }
        return nearestEntry != null ? nearestEntry.getValue() : getPowerLevel();
    }

    public void setPowerLevel(int powerLevel) {
        this.powerLevel = powerLevel;
    }

    public void setPowerLevel(LocalDateTime myStartTime, int powerLevel) {
        this.powerLevel = powerLevel;
        getPowerLevels().put(myStartTime, powerLevel);
    }

    @Override
    public String toString() {
        return "ChargingSession{id=" + sessionId + ", connector=" + connectorId + ", start=" + startTime + ", stop=" + stopTime + ", status=" + status + ", power=" + powerLevel + "kW}";
    }

}
