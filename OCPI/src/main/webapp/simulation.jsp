<%-- 
    Document   : simulation
    Created on : Mar 16, 2025, 8:12:45 PM
    Author     : nsofias

//7 11 & 22
--%> 
<%@page import="simulation.SimulationData"%>
<%@page import="java.util.Arrays"%>
<%@page import="simulation.EVChargingStation"%>
<%@page import="java.util.Collection"%>
<%@page import="org.bson.conversions.Bson"%>
<%@page import="model.Location"%>
<%@page import="com.mongodb.client.model.Filters"%>
<%@page import="org.bson.Document"%>
<%@page import="nsofiasLib.time.TimeStamp1"%>
<%@page import="java.util.Map"%>
<%@page import="simulation.EventType"%>
<%@page import="model.Session"%>
<%@page import="model.ChargingPeriod"%>
<%@page import="java.util.List"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalTime"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.util.UUID"%>
<%@page import="simulation.EVChargingStation"%>
<%@page import="nsofiasLib.others.Parameters"%>
<%@page import="nsofiasLib.databases.Mongo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>        
        <%
            /*
            locationId, MAXIMUM_PLAN, HIGH_PLAN, MEDIUM_PLAN, LOW_PLAN, numberOfConnectors, visitsExpected
             */
            List<SimulationData> testData = List.of(
                    new SimulationData("GR-EMU-S4875457728279981905577094-L", 73, 22, 11, 7, 4, 11),//four seasons
                    new SimulationData("GR-EMU-S5721861176845717934021946-L", 73, 22, 11, 7, 3, 8),//AC Station
                    new SimulationData("GR-EMU-S2280630556667049706985982-L", 73, 22, 11, 7, 2, 5),//Ayvens - Glyfada
                    new SimulationData("GR-EMU-S6792111181543869683893493-L", 73, 22, 11, 7, 2, 5),//Alfa Techniki Episkevastiki Skafon Ltd
                    new SimulationData("GR-EMU-S1399677930376035019011074-L", 73, 22, 11, 7, 2, 5)//Serafeio Athletic Complex
            );

            final JspWriter out1 = out;
            String from = "2025-01-01T00:00:00.000";
            String to = "2025-04-15T00:00:00.000";
            String mongoURL = Parameters.loadStringValue(System.getenv("APPLICATIONS_PATH") + "/OCPI/conf/parameters.properties", "mongoURL", "UTF8", "mongodb://nsofias:#1Vasilokori@mongo:27017");
            Mongo myMongo = new Mongo(mongoURL, "OCPI");
            myMongo.dropCollection("sessions");
            myMongo.dropCollection("events");
            testData.forEach(d -> {
                try {
                    String locationId = d.getLocationId();
                    int MAXIMUM_PLAN = d.getMAXIMUM_PLAN();
                    int HIGH_PLAN = d.getHIGH_PLAN();
                    int MEDIUM_PLAN = d.getMEDIUM_PLAN();
                    int LOW_PLAN = d.getLOW_PLAN();
                    int numberOfConnectors = d.getNumberOfConnectors();
                    int visitsExpected = d.getVisitsExpected();
                    //-------------int visitsExpected
                    String[] connectorIds = new String[numberOfConnectors];
                    for (int i = 0; i < numberOfConnectors; i++) {
                        connectorIds[i] = "C" + String.valueOf(i + 1);
                    }
                    out1.println("<h1>"+locationId+" MAXIMUM_PLAN:" + MAXIMUM_PLAN + " HIGH_PLAN:" + HIGH_PLAN + " MEDIUM_PLAN:" + MEDIUM_PLAN + " LOW_PLAN:" + LOW_PLAN + " connectors:" + Arrays.asList(connectorIds) + " visitsExpected:" + visitsExpected+"</h1>");                             
                    EVChargingStation station = new EVChargingStation(locationId, connectorIds, MAXIMUM_PLAN, HIGH_PLAN, MEDIUM_PLAN, LOW_PLAN);
                    DateTimeFormatter formatter = EVChargingStation.FORMATER;
                    LocalDateTime T1 = LocalDateTime.parse(from, formatter);
                    LocalDateTime T2 = LocalDateTime.parse(to, formatter);
                    //------filtering ---------------    
                    Bson locationFilter = (locationId != null && !locationId.isEmpty()) ? Filters.eq("location_id", locationId) : new Document();
                    Bson timeFilter = Filters.and(Filters.gte("mydate", T1),
                            Filters.lte("mydate", T2));
                    //Bson filter = Filters.and(locationFilter, timeFilter);
                    //Collection<Session> sessions = myMongo.find("sessions", filter, true, Session.class);
                    //Map<String, Double> erlangsPerHour = EVChargingStation.getUtilizationPerHour(sessions, new TimeStamp1(T1).toLocalDateTime(), new TimeStamp1(T2).toLocalDateTime(), connectorIds.length);
                    //out1.println("<b>erlangsPerHour.size()====" + erlangsPerHour.size() + "</b>");

                    station.simulate_period(T1, T2,  visitsExpected);
                    //-------------- save sessions ----------------------------     

                    out1.println("<h1>sessions</h1>");
                    station.getSessions()
                            .forEach(chs -> {
                                Session s = chs.toSession();
                                try {
                                    myMongo.add("sessions", s, LocalDateTime.parse(s.getStart_date_time(), formatter));
                                    out1.println("\n Added Session: " + new Gson().toJson(s));
                                } catch (Exception e) {
                                    try {
                                        out1.println(e);
                                    } catch (Exception ex) {
                                    }
                                }
                            });
                    out1.println("<h1>events</h1>");
                    station.getEvents().stream()
                            .forEach(ev -> {
                                try {
                                    myMongo.add("events", ev, LocalDateTime.parse(ev.getEventTime(), formatter));
                                    out1.println("\n Added event " + new Gson().toJson(ev));
                                } catch (Exception e) {
                                    try {
                                        out1.println(e);
                                    } catch (Exception ex) {
                                    }
                                }
                            });
                } catch (Exception e) {
                }
            });
        %>
    </body>
</html>


<%!

%>