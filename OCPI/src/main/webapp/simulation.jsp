<%-- 
    Document   : simulation
    Created on : Mar 16, 2025, 8:12:45 PM
    Author     : nsofias
--%>

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
        <h1>Hello World!</h1>
        <%
            String stationId = "GR-EMU-S4875457728279981905577094-L";
            String[] connectorIds = {"C1", "C2", "C3", "C4"};
            EVChargingStation station = new EVChargingStation(stationId, connectorIds);

            LocalDateTime today = LocalDateTime.now().with(LocalTime.MIDNIGHT);
            LocalDateTime startDate = today.minusDays(30);

            for (int i = 0; i <= 30; i++) { // Changed to <= to include today
                LocalDateTime day = startDate.plusDays(i);
                station.simulateDay(day);
            }
            //------------------------------------------------------
            final JspWriter out1 = out;
            out.println("<p>*************** SESSIONS *********************************");
            String APPLICATIONS_PATH = System.getenv("APPLICATIONS_PATH");
            if (APPLICATIONS_PATH == null) {
                APPLICATIONS_PATH = "C:\\myFiles\\data\\";
            }
            String mongoURL = Parameters.loadStringValue(APPLICATIONS_PATH + "/OCPI/conf/parameters.properties", "mongoURL", "UTF8", "mongodb://nsofias:#1Vasilokori@mongo:27017");
            Mongo myMongo = new Mongo(mongoURL, "OCPI");

            station.getSessions().stream().map(s -> s.toSession()).forEach(s -> {
                try {
                    out1.println("<p>" + s);
                } catch (Exception e) {
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                LocalDateTime ttlDate = LocalDateTime.parse(s.getStart_date_time(), formatter);
                myMongo.add("sessions", s, ttlDate);
                s.getCharging_periods().forEach(p -> {
                    try {
                        out1.println("   " + p);
                    } catch (Exception e) {
                    }

                });
            });


        %>
    </body>
</html>
