<%-- 
    Document   : simulation
    Created on : Mar 16, 2025, 8:12:45 PM
    Author     : nsofias
http://34.136.104.208:8080/OCPI_2/simulation.jsp?MAXIMUM_PLAN=10&HIGH_PLAN=6&MEDIUM_PLAN=4&LOW_PLAN=2
--%> 
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
            final JspWriter out1 = out;
            String locationId = "GR-EMU-S4875457728279981905577094-L";
            int MAXIMUM_PLAN = 16;
            int HIGH_PLAN = 6;
            int MEDIUM_PLAN = 4;
            int LOW_PLAN = 2;
            if (request.getParameter("locationId") != null)
            try {
                locationId = request.getParameter("locationId");
            } catch (Exception e) {
            }
            if (request.getParameter("MAXIMUM_PLAN") != null)
            try {
                MAXIMUM_PLAN = Integer.parseInt(request.getParameter("MAXIMUM_PLAN"));
            } catch (Exception e) {
            }
            if (request.getParameter("HIGH_PLAN") != null)
            try {
                HIGH_PLAN = Integer.parseInt(request.getParameter("HIGH_PLAN"));
            } catch (Exception e) {
            }
            if (request.getParameter("MEDIUM_PLAN") != null)
            try {
                MEDIUM_PLAN = Integer.parseInt(request.getParameter("MEDIUM_PLAN"));
            } catch (Exception e) {
            }
            if (request.getParameter("LOW_PLAN") != null)
            try {
                LOW_PLAN = Integer.parseInt(request.getParameter("LOW_PLAN"));
            } catch (Exception e) {
            }
            out.println(locationId + " MAXIMUM_PLAN:" + MAXIMUM_PLAN + " HIGH_PLAN:" + HIGH_PLAN + " MEDIUM_PLAN:" + MEDIUM_PLAN + " LOW_PLAN:" + LOW_PLAN);
            // --- 
            String mongoURL = Parameters.loadStringValue(System.getenv("APPLICATIONS_PATH") + "/OCPI/conf/parameters.properties", "mongoURL", "UTF8", "mongodb://nsofias:#1Vasilokori@mongo:27017");
            Mongo myMongo = new Mongo(mongoURL, "OCPI");
            Bson myLocationFilter = (locationId != null && !locationId.isEmpty()) ? Filters.eq("id", locationId) : new Document();
            List<Location> myLocations = myMongo.find("locations", myLocationFilter, false, Location.class);
            //-------------            
            out.println("<h1>" + locationId + "</h1>");
            String[] connectorIds = {"C1", "C2", "C3", "C4"};
            EVChargingStation station = new EVChargingStation(locationId, connectorIds, MAXIMUM_PLAN, HIGH_PLAN, MEDIUM_PLAN, LOW_PLAN);
            DateTimeFormatter formatter = EVChargingStation.FORMATER;
            LocalDateTime T1 = LocalDateTime.parse("2025-01-01T00:00:00.000", formatter);
            LocalDateTime T2 = LocalDateTime.parse("2025-03-31T00:00:00.000", formatter);
            Map<String, Double> erlangsPerHour = myLocations.get(0).getErlangsPerHour(myMongo, new TimeStamp1(T1), new TimeStamp1(T2));
            out.println("<b>erlangsPerHour.size()====" + erlangsPerHour.size() + "</b>");
            myMongo.dropCollection("sessions");
            myMongo.dropCollection("events");
            station.simulate_period(T1, T2, erlangsPerHour);
            //-------------- save sessions ----------------------------     

            out.println("<h1>sessions</h1>");
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
            out.println("<h1>events</h1>");
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
        %>
    </body>
</html>
