<%-- 
    Document   : simulation
    Created on : Mar 16, 2025, 8:12:45 PM
    Author     : nsofias
--%>

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
            String stationId = "GR-EMU-S4875457728279981905577094-L";
            out.println("<h1>" + stationId + "</h1>");
            String[] connectorIds = {"C1", "C2", "C3", "C4"};
            EVChargingStation station = new EVChargingStation(stationId, connectorIds);

            DateTimeFormatter formatter = EVChargingStation.FORMATER;
            LocalDateTime T1 = LocalDateTime.parse("2025-01-01T00:00:00.000", formatter);
            LocalDateTime T2 = LocalDateTime.parse("2025-03-31T00:00:00.000", formatter);
            station.simulate_period(T1, T2);
            //-------------- save sessions ----------------------------     
            String mongoURL = Parameters.loadStringValue(System.getenv("APPLICATIONS_PATH") + "/OCPI/conf/parameters.properties", "mongoURL", "UTF8", "mongodb://nsofias:#1Vasilokori@mongo:27017");
            Mongo myMongo = new Mongo(mongoURL, "OCPI");
            myMongo.deleteAll("sessions");
            myMongo.deleteAll("events");
            //-------------
            out.println("<h1>sessions</h1>");
            station.getSessions()
                    .forEach(chs -> {
                        Session s = chs.toSession();
                        try {
                            myMongo.add("sessions", s, LocalDateTime.now());
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
                            myMongo.add("events", ev, LocalDateTime.now());
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
