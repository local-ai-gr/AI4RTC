/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simulation;

import java.time.LocalDateTime;

/**
 *
 * @author nsofias
 */
public class ChargingEvent {
    private final LocalDateTime eventTime;
    private final EventType eventType;
    private final String sessionId;
    private final String connectorId;
    private final Integer powerValue;

    public ChargingEvent(LocalDateTime eventTime, EventType eventType, String sessionId, String connectorId) {
        this(eventTime, eventType, sessionId, connectorId, null);
    }

    public ChargingEvent(LocalDateTime eventTime, EventType eventType, String sessionId, String connectorId, Integer powerValue) {
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.sessionId = sessionId;
        this.connectorId = connectorId;
        this.powerValue = powerValue;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getConnectorId() {
        return connectorId;
    }
    
    public Integer getPowerValue() {
        return powerValue;
    }

    @Override
    public String toString() {
        return "ChargingEvent{time=" + eventTime + ", type=" + eventType + ", sessionId=" + sessionId + ", connector=" + connectorId + (powerValue != null ? ", powerValue=" + powerValue + "kW" : "") + '}';
    }
}