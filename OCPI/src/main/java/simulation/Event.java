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
public class Event {

    private final String eventTime;
    private final EventType eventType;
    private final String location_id;
    private final String sessionId;
    private final String connectorId;
    private final double powerValue;

    public Event(String eventTime, EventType eventType, String locationId, String sessionId, String connectorId, double powerValue) {
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.location_id = locationId;
        this.sessionId = sessionId;
        this.connectorId = connectorId;
        this.powerValue = powerValue;
    }

    public String getEventTime() {
        return eventTime;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getLocation_id() {
        return location_id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public double getPowerValue() {
        return powerValue;
    }

    @Override
    public String toString() {
        return "Event:::" + eventTime + ", type=" + eventType + ", sessionId=" + sessionId + ", connector=" + connectorId + (powerValue != -1 ? ", powerValue=" + powerValue + "kW" : "") + '}';
    }
}
