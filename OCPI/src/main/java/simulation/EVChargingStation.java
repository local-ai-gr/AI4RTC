package simulation;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;

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

    private final String[] connectorIds;
    private final Random random = new Random();
    private final List<ChargingSession> sessions = new ArrayList<>();
    private final List<ChargingEvent> events = new ArrayList<>();
    private final Map<String, ChargingSession> activeSessionsByConnector = new HashMap<>();

    public EVChargingStation(String stationId, String[] connectorIds) {
        this.connectorIds = connectorIds;
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
            String sessionId = UUID.randomUUID().toString();

            List<ChargingSession> currentSessions = sessions.stream()
                    .filter(s -> s.getStartTime().isBefore(startTime) && s.getStopTime().isAfter(startTime))
                    .sorted(Comparator.comparing((ChargingSession s) -> s.getPowerLevel()).reversed()).collect(toList());
            List<String> busyConnectors = currentSessions.stream().map(s -> s.getConnectorId()).collect(toList());

            Optional<String> connectorId_ = Arrays.asList(connectorIds).stream().filter(c -> !busyConnectors.contains(c)).findFirst();

            if (connectorId_.isEmpty()) {
                System.out.println("LOST CUSTOMER. All connectors are busy!!!");
                continue;
            }
            String connectorId = connectorId_.get();
            int assignedPower = assignPowerLevels(currentSessions, startTime);
            if (assignedPower == 0) {
                continue;
            }

            ChargingSession session = new ChargingSession(sessionId, connectorId, startTime, stopTime, assignedPower);
            //System.out.println("new session found->"+session);
            //System.out.println("PowerEvents generated");
            activeSessionsByConnector.put(connectorId, session);
            //System.out.println("activeSessionsByConnector added");
            sessions.add(session);
            System.out.println("new session:" + session);
            events.add(new ChargingEvent(startTime, EventType.START_SESSION, sessionId, connectorId, assignedPower));
            events.add(new ChargingEvent(stopTime, EventType.STOP_SESSION, sessionId, connectorId, 0));
            //activeSessionsByConnector.remove(connectorId);
        }
        sessions.stream().forEach(s -> generatePowerEvents(s));

    }

    private int assignPowerLevels(List<ChargingSession> currentSessions, LocalDateTime startTime) {
        int totalPower = currentSessions.stream().mapToInt(ChargingSession::getPowerLevel).sum();
        if (totalPower + HIGH <= MAXIMUM_POWER) {
            System.out.println("HIGH totalPower:" + totalPower);
            return HIGH;
        } else if (totalPower + MEDIUM <= MAXIMUM_POWER) {
            System.out.println("MEDIUM totalPower:" + totalPower);
            return MEDIUM;
        } else if (totalPower + LOW <= MAXIMUM_POWER) {
            System.out.println("LOW totalPower:" + totalPower);
            return LOW;
        } else {// try to downgrade
            for (ChargingSession s : currentSessions) {
                if (s.getPowerLevel() == HIGH) {
                    s.setPowerLevel(startTime, MEDIUM);
                    System.out.println("DOWNGRADE Event generated !!!");
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
            events.add(new ChargingEvent(eventTime, EventType.POWER_EVENT, session.getSessionId(), session.getConnectorId(), session.getPowerLevel(eventTime)));
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

    public void printEvents() {
        events.stream()
                .sorted(Comparator.comparing(ChargingEvent::getEventTime))
                .collect(toList())
                .forEach(System.out::println);
    }

    public static void main(String[] args) {
        String stationId = UUID.randomUUID().toString();
        String[] connectorIds = {"C1", "C2", "C3"};
        EVChargingStation station = new EVChargingStation(stationId, connectorIds);

        LocalDateTime today = LocalDateTime.now().with(LocalTime.MIDNIGHT);
        LocalDateTime startDate = today.minusDays(30);

        for (int i = 0; i <= 30; i++) { // Changed to <= to include today
            LocalDateTime day = startDate.plusDays(i);
            station.simulateDay(day);
        }

        station.printEvents();
    }
}
