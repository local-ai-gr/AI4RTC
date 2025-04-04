/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package simulation;

/**
 *
 * @author nsofias
 */
public enum EventType {
    START_SESSION_EVENT,
    STOP_SESSION_EVENT,
    DOWNGRADE_EVENT,
    DOWNGRADE_EVENT1, // dwngrade due to prediction
    DOWNGRADE_EVENT_PREDICTIVE,
    POWER_INFO_EVENT,
    NOT_AVAILABLE_CONNECTOR_EVENT,
    NOT_AVAILABLE_POWER_EVENT,
    //
    START_CHARGING_PERIOD_EVENT,
    STOP_CHARGING_PERIOD_EVENT;
}
