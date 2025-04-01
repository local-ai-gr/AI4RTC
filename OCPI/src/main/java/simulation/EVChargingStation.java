package simulation;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import model.ChargingPeriod;
import static simulation.EventType.DOWNGRADE_EVENT_PREDICTIVE;
import static simulation.EventType.START_SESSION_EVENT;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author nsofias
 */
public class EVChargingStation {

    private final int MAXIMUM_PLAN;// = 16;
    private final int HIGH_PLAN;// = 6;
    private final int MEDIUM_PLAN;// = 4;
    private final int LOW_PLAN;// = 2;
    private final String locationId;
    private final String[] connectorIds;
    private final Random random = new Random();
    private final List<ChargingSession> sessions = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    //private final Map<String, ChargingSession> activeSessionsByConnector = new HashMap<>();
    private static final SecureRandom secureRandom = new SecureRandom();
    public static DateTimeFormatter FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public EVChargingStation(String locationId, String[] connectorIds, int MAXIMUM_PLAN, int HIGH_PLAN, int MEDIUM_PLAN, int LOW_PLAN) {
        this.MAXIMUM_PLAN = MAXIMUM_PLAN;
        this.HIGH_PLAN = HIGH_PLAN;
        this.MEDIUM_PLAN = MEDIUM_PLAN;
        this.LOW_PLAN = LOW_PLAN;
        this.connectorIds = connectorIds;
        this.locationId = locationId;
    }

    public void simulate_period(LocalDateTime T1, LocalDateTime T2, Map<String, Double> erlangsPerHour) {

        LocalDateTime T = T1;
        while (T.isBefore(T2)) {
            System.out.println("Simulating day   " + T.format(FORMATER));
            simulateDay(T, erlangsPerHour);
            T = T.plusDays(1);
        }
        // correct the null end session of the last Charging_period        
        getSessions().forEach(session -> {
            List<ChargingPeriod> charging_periods = session.getCharging_periods();
            if (!charging_periods.isEmpty()) {
                ChargingPeriod previousPeriod = charging_periods.get(charging_periods.size() - 1);//get the last
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                // --- set previousPeriod.setEnd_date_time  and energy ---
                previousPeriod.setEnd_date_time(session.getStopTime().format(formatter));// end of previous period
                LocalDateTime T_1 = previousPeriod.getStartLocalDateTime();
                LocalDateTime T_2 = previousPeriod.getEndLocalDateTime();
                long previousDuration = T_1.until(T_2, SECONDS);
                double previousPowerlevel = previousPeriod.getChargingPeriodPower();
                double energy = (previousPowerlevel * previousDuration) / 3600;
                previousPeriod.setChargingPeriodEnergy(energy);
            }
        });
    }

    public void simulateDay(LocalDateTime day, Map<String, Double> erlangsPerHour) {
        LocalDateTime now = LocalDateTime.now();
        int sessionCount = 10 + random.nextInt(11);
        List<Event> primaryEvents = new ArrayList<>();
        for (int i = 0; i < sessionCount; i++) {
            //newEventsDateTimes.put(generateRandomStartTime(day), "SESSION");
            primaryEvents.add(new Event(generateRandomStartTime(day).format(FORMATER), EventType.START_SESSION_EVENT, "", "", "", 0));
        }
        Set<LocalDateTime> predictiveDegradations = generateTimesForDegradations(day, erlangsPerHour);
        System.out.println("predictiveDegradations:" + predictiveDegradations.size());
        predictiveDegradations.forEach(e -> primaryEvents.add(new Event(generateRandomStartTime(day).format(FORMATER), EventType.DOWNGRADE_EVENT_PREDICTIVE, "", "", "", 0)));
        primaryEvents.stream().sorted(Comparator.comparing(e -> e.getEventTime())).forEach(e -> {
            LocalDateTime startTime = LocalDateTime.parse(e.getEventTime(), FORMATER);
            System.out.println("startTime:" + startTime + " eventCategory:" + e.getEventType());
            List<ChargingSession> currentSessions = getSessions().stream()
                    .filter(s -> (s.getStartTime().isBefore(startTime) || s.getStartTime().isBefore(startTime)) && s.getStopTime().isAfter(startTime))
                    .sorted(Comparator.comparing((ChargingSession s) -> s.getPowerLevel()).reversed()).collect(toList());
            Set<String> busyConnectors = currentSessions.stream().map(s -> s.getConnectorId()).collect(toSet());
            // --- 

            if (e.getEventType() == START_SESSION_EVENT) {
                if (startTime.isAfter(now)) {
                    return;
                }
                System.out.println("new start time->" + startTime);
                int durationMinutes = 30 + random.nextInt(211);
                LocalDateTime stopTime = startTime.plusMinutes(durationMinutes);
                Optional<String> connectorId_ = Arrays.asList(connectorIds).stream().filter(c -> !busyConnectors.contains(c)).findFirst();
                if (connectorId_.isEmpty()) {
                    //System.out.println("LOST CUSTOMER. All connectors are busy!!!");
                    getEvents().add(new Event(startTime.format(FORMATER), EventType.NOT_AVAILABLE_CONNECTOR_EVENT, locationId, "N/A", "N/A", 0));
                    return;
                }
                String connectorId = connectorId_.get();
                int assignedPower = assignPowerLevels(currentSessions, startTime);
                if (assignedPower == 0) {
                    getEvents().add(new Event(startTime.format(FORMATER), EventType.NOT_AVAILABLE_POWER_EVENT, locationId, "N/A", "N/A", 0));
                    return;
                }
                String sessionId = new UUID(secureRandom.nextLong(), secureRandom.nextLong()).toString();
                ChargingSession session = new ChargingSession(getLocationId(), sessionId, connectorId, startTime, stopTime, 0, assignedPower);
                getSessions().add(session);
                //System.out.println("new session:" + session);
                getEvents().add(new Event(startTime.format(FORMATER), EventType.START_SESSION_EVENT, locationId, sessionId, connectorId, assignedPower));
                getEvents().add(new Event(stopTime.format(FORMATER), EventType.STOP_SESSION_EVENT, locationId, sessionId, connectorId, 0));
            } else if (e.getEventType() == DOWNGRADE_EVENT_PREDICTIVE) {
                System.out.println("getEventType:" + e.getEventType());
                getEvents().add(new Event(startTime.format(FORMATER), EventType.DOWNGRADE_EVENT_PREDICTIVE, locationId, "", "", 0));
                for (ChargingSession s : currentSessions) {
                    s.changePowerLevel(startTime, MEDIUM_PLAN);
                }

            }
        });

    }

    private int assignPowerLevels(List<ChargingSession> currentSessions, LocalDateTime startTime) {
        double totalPower = currentSessions.stream().mapToDouble(ChargingSession::getPowerLevel).sum();
        if (totalPower + HIGH_PLAN <= MAXIMUM_PLAN) {
            //System.out.println("HIGH_PLAN totalPower:" + totalPower);
            return HIGH_PLAN;
        } else if (totalPower + MEDIUM_PLAN <= MAXIMUM_PLAN) {
            //System.out.println("MEDIUM_PLAN totalPower:" + totalPower);
            return MEDIUM_PLAN;
        } else if (totalPower + LOW_PLAN <= MAXIMUM_PLAN) {
            //System.out.println("LOW_PLAN totalPower:" + totalPower);
            return LOW_PLAN;
        } else {// try to downgrade
            for (ChargingSession s : currentSessions) {
                if (s.getPowerLevel() == HIGH_PLAN) {
                    getEvents().add(new Event(startTime.format(FORMATER), EventType.DOWNGRADE_EVENT, locationId, s.getSessionId(), s.getConnectorId(), MEDIUM_PLAN));
                    s.changePowerLevel(startTime, MEDIUM_PLAN);
                    //System.out.println("DOWNGRADE Event generated !!!");
                    return assignPowerLevels(currentSessions, startTime);
                }
            }
        }
        return 0;
    }

    private LocalDateTime generateRandomStartTime(LocalDateTime day) {
        int peakHourWeight = 3;
        boolean peakHour = random.nextInt(peakHourWeight) == 0;

        int hour;
        if (peakHour) {
            hour = 9;
        } else {
            hour = random.nextInt(24);
        }
        int minute = random.nextInt(60);
        return day.with(LocalTime.of(hour, minute));
    }

    public static Set<LocalDateTime> generateTimesForDegradations(LocalDateTime day, Map<String, Double> erlangsPerHour) {
        //DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");
        System.out.println("erlangsPerHour:+" + erlangsPerHour.size());
        return erlangsPerHour.entrySet().stream().filter((Entry<String, Double> e) -> e.getValue() > 3).map((Entry<String, Double> e) -> LocalDateTime.parse(e.getKey())).collect(toSet());
    }

    /**
     * @return the sessions
     */
    public List<ChargingSession> getSessions() {
        return sessions;
    }

    /**
     * @return the events
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * @return the locationId
     */
    public String getLocationId() {
        return locationId;
    }

    public static void main(String[] args) {
        String stationId = UUID.randomUUID().toString();
        String[] connectorIds = {"C1", "C2", "C3", "C4"};
        EVChargingStation station = new EVChargingStation(stationId, connectorIds, 16, 6, 4, 2);
        //
        LocalDateTime T1 = LocalDateTime.parse("2025-01-01T00:00:00.000", FORMATER);
        LocalDateTime T2 = LocalDateTime.parse("2025-03-31T00:00:00.000", FORMATER);
        //station.simulate_period(T1, T2);
    }

}
