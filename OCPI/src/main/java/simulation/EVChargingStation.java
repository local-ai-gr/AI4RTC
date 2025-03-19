package simulation;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import model.ChargingPeriod;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author nsofias
 */
public class EVChargingStation {

    private static final int MAXIMUM_POWER = 10;
    private static final int HIGH = 6;
    private static final int MEDIUM = 4;
    private static final int LOW = 2;
    private final String locationId;
    private final String[] connectorIds;
    private final Random random = new Random();
    private final List<ChargingSession> sessions = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    //private final Map<String, ChargingSession> activeSessionsByConnector = new HashMap<>();
    private static final SecureRandom secureRandom = new SecureRandom();
    public static DateTimeFormatter FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public EVChargingStation(String locationId, String[] connectorIds) {
        this.connectorIds = connectorIds;
        this.locationId = locationId;
    }

    public void simulate_period(LocalDateTime T1, LocalDateTime T2) {
        LocalDateTime T = T1;
        while (T.isBefore(T2)) {
            System.out.println("Simulating day   " + T.format(FORMATER));
            simulateDay(T);
            T = T.plusDays(1);
        }
        // correct the null end session of the last Charging_period        
        getSessions().forEach(session -> {
            List<ChargingPeriod> charging_periods = session.getCharging_periods();
            if (!charging_periods.isEmpty()) {
                ChargingPeriod lastPeriod = charging_periods.get(charging_periods.size() - 1);//get the last           
                lastPeriod.setEnd_date_time(session.getStopTime().format(FORMATER));// end of session time  
            }
        });
    }

    public void simulateDay(LocalDateTime day) {
        LocalDateTime now = LocalDateTime.now();
        int sessionCount = 10 + random.nextInt(11);

        List<LocalDateTime> localDateTimes = new ArrayList<>();
        for (int i = 0; i < sessionCount; i++) {
            localDateTimes.add(generateRandomStartTime(day));
        }
        localDateTimes.sort(Comparator.naturalOrder());
        for (LocalDateTime startTime : localDateTimes) {
            if (startTime.isAfter(now)) {
                continue;
            }
            //System.out.println("new start time->"+startTime);
            int durationMinutes = 30 + random.nextInt(211);
            LocalDateTime stopTime = startTime.plusMinutes(durationMinutes);

            List<ChargingSession> currentSessions = getSessions().stream()
                    .filter(s -> s.getStartTime().isBefore(startTime) && s.getStopTime().isAfter(startTime))
                    .sorted(Comparator.comparing((ChargingSession s) -> s.getPowerLevel()).reversed()).collect(toList());
            List<String> busyConnectors = currentSessions.stream().map(s -> s.getConnectorId()).collect(toList());

            Optional<String> connectorId_ = Arrays.asList(connectorIds).stream().filter(c -> !busyConnectors.contains(c)).findFirst();

            if (connectorId_.isEmpty()) {
                //System.out.println("LOST CUSTOMER. All connectors are busy!!!");
                getEvents().add(new Event(startTime.format(FORMATER), EventType.NOT_AVAILABLE_CONNECTOR_EVENT, locationId, "N/A", "N/A", 0));
                continue;
            }
            String connectorId = connectorId_.get();
            int assignedPower = assignPowerLevels(currentSessions, startTime);
            if (assignedPower == 0) {
                getEvents().add(new Event(startTime.format(FORMATER), EventType.NOT_AVAILABLE_POWER_EVENT, locationId, "N/A", "N/A", 0));
                continue;
            }
            String sessionId = new UUID(secureRandom.nextLong(), secureRandom.nextLong()).toString();

            ChargingSession session = new ChargingSession(getLocationId(), sessionId, connectorId, startTime, stopTime, -1, assignedPower);
            getSessions().add(session);
            //System.out.println("new session:" + session);
            getEvents().add(new Event(startTime.format(FORMATER), EventType.START_SESSION_EVENT, locationId, sessionId, connectorId, assignedPower));
            getEvents().add(new Event(stopTime.format(FORMATER), EventType.STOP_SESSION_EVENT, locationId, sessionId, connectorId, 0));
            //activeSessionsByConnector.remove(connectorId);
        }
        getSessions().stream().forEach(s -> generatePowerEvents(s));

    }

    private int assignPowerLevels(List<ChargingSession> currentSessions, LocalDateTime startTime) {
        double totalPower = currentSessions.stream().mapToDouble(ChargingSession::getPowerLevel).sum();
        if (totalPower + HIGH <= MAXIMUM_POWER) {
            //System.out.println("HIGH totalPower:" + totalPower);
            return HIGH;
        } else if (totalPower + MEDIUM <= MAXIMUM_POWER) {
            //System.out.println("MEDIUM totalPower:" + totalPower);
            return MEDIUM;
        } else if (totalPower + LOW <= MAXIMUM_POWER) {
            //System.out.println("LOW totalPower:" + totalPower);
            return LOW;
        } else {// try to downgrade
            for (ChargingSession s : currentSessions) {
                if (s.getPowerLevel() == HIGH) {
                    getEvents().add(new Event(startTime.format(FORMATER), EventType.DOWNGRADE_EVENT, locationId, s.getSessionId(), s.getConnectorId(), MEDIUM));
                    s.setPowerLevel(startTime, MEDIUM);
                    //System.out.println("DOWNGRADE Event generated !!!");
                    return assignPowerLevels(currentSessions, startTime);
                }
            }
        }
        return 0;
    }

    private void generatePowerEvents(ChargingSession session) {
        LocalDateTime eventTime = session.getStartTime();
        LocalDateTime endTime = session.getStopTime();

        while (eventTime.isBefore(endTime)) {
            //getEvents().add(new Event(eventTime, EventType.POWER_INFO_EVENT, locationId, session.getSessionId(), session.getConnectorId(), session.getPowerLevel(eventTime)));
            eventTime = eventTime.plusMinutes(1);
        }
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
        EVChargingStation station = new EVChargingStation(stationId, connectorIds);
        //
        LocalDateTime T1 = LocalDateTime.parse("2025-01-01T00:00:00.000", FORMATER);
        LocalDateTime T2 = LocalDateTime.parse("2025-03-31T00:00:00.000", FORMATER);
        station.simulate_period(T1, T2);
    }

}
