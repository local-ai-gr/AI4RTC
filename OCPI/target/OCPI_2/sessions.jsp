<%-- 
    Document   : locations
    Created on : Jun 30, 2024, 6:02:44 PM
    Author     : nsofias
--%>

<%@page import="org.bson.Document"%>
<%@page import="jakarta.servlet.jsp.JspWriter"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="nsofiasLib.time.TimeStamp1"%>
<%@page import="model.Connector"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="model.Location"%>
<%@page import="java.nio.charset.Charset"%>
<%@page import="nsofiasLib.others.Helpme"%>
<%@page import="java.util.List"%>
<%@page import="java.io.IOException"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="com.mongodb.client.model.Filters"%>
<%@page import="java.util.Collection"%>
<%@page import="nsofiasLib.databases.Mongo"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="model.Session"%>
<%@page import="nsofiasLib.others.SimpleDaemon"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    ServletContext myContext = session.getServletContext();
    Mongo myMongo = (Mongo) myContext.getAttribute("myMongo");
    if (request.getParameter("deleteAll") != null) {
        myMongo.delete("sessions", new Document());
    }
    Collection<Session> sessions = myMongo.find("sessions", new Document(), false, Session.class);
    final JspWriter out1 = out;
    if (request.getParameter("loadFromFile") != null) {
        String filename = System.getenv("APPLICATIONS_PATH") + "/OCPI/conf/sessions.csv";
        loadFromFile(filename, StandardCharsets.ISO_8859_1, myMongo);
    }

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sessions</title>
    </head>
    <body>
        <h1>Sessions (from DB)</h1>
        <a href="sessions.jsp?loadFromFile">load From File</a>
        <a href="sessions.jsp?deleteAll">Delete All</a>
        <%            sessions.stream().sorted(Comparator.comparing((Session s) -> s.getStart_date_time()).reversed()).forEach(v -> {
                try {
                    out1.println("<p>" + new Gson().toJson(v));
                } catch (Exception e) {
                }
            }
            );
        %>
    </body>
</html>

<%!
    void loadFromFile(String filename, Charset charSet, Mongo myMongo) throws IOException {
        Collection<Location> locations = myMongo.find("locations", new Document(), false, Location.class);
        List<String> sessions = Helpme.getFileRowsAsList(filename, charSet);
        LocalDateTime localDateTime = new TimeStamp1().toLocalDateTime();
        sessions.stream().filter(s -> !s.startsWith("#")).map(s -> s.split(";")).map(sesionData -> {
            Session s = new Session();

            s.setLocation_id(sesionData[0]);
            Location myLocation = s.getlocationData(locations);
            //s.setLocation(myLocation);
            s.setStart_date_time(sesionData[4]);
            s.setEnd_date_time(sesionData[5]);
            s.setKwh(Double.parseDouble(sesionData[3].replace(",", ".")));
            String plug = sesionData[2];

            if (plug.equals("A")) {
                s.setEvse_uid("A");
                s.setConnector_id("conn1");
            } else {
                s.setEvse_uid("B");
                s.setConnector_id("conn1");
            }
            s.setId(String.valueOf(new Gson().toJson(s).hashCode()));
            return s;
        }).forEach(s -> {
            //System.out.println("added"+s.getLocation());
            myMongo.add("sessions", s, localDateTime);
        });
    }

%>

<!--  
                                                                
#Driver Name	Station	Plug Name	Consumption (in kWh)	Start Time	End Time	Duration(minutes)	Total Cost	Commission	Net
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	A	1,935	2024-07-24 14:16 (UTC+0300)	2024-07-24 14:18  (UTC+0300)	3	0	0	0
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	B	2,883	2024-07-24 14:21 (UTC+0300)	2024-07-24 14:25  (UTC+0300)	4	1,7	0,2	1,5
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	A	0	2024-07-26 19:36 (UTC+0300)	2024-07-27 05:36  (UTC+0300)	600	0	0	0
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	B	0	2024-07-27 11:43 (UTC+0300)	2024-07-27 13:43  (UTC+0300)	120	0	0	0
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	A	0,24	2024-07-27 17:45 (UTC+0300)	2024-07-27 17:45  (UTC+0300)	1	0,1	0,01	0,09
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	A	0	2024-07-27 17:52 (UTC+0300)	2024-07-27 17:53  (UTC+0300)	1	0	0	0
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	A	11,124	2024-07-27 17:55 (UTC+0300)	2024-07-27 18:11  (UTC+0300)	16	6,6	0,79	5,81
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	A	2,976	2024-08-01 18:26 (UTC+0300)	2024-08-01 18:30  (UTC+0300)	4	0	0	0
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	A	0	2024-08-07 01:05 (UTC+0300)	2024-08-07 03:05  (UTC+0300)	120	0	0	0
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	B	24,859	2024-08-07 01:09 (UTC+0300)	2024-08-07 02:12  (UTC+0300)	63	12,1	1,45	10,65
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	A	27,082	2024-08-09 15:39 (UTC+0300)	2024-08-09 16:48  (UTC+0300)	69	13,2	1,58	11,62
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	A	17,376	2024-08-31 17:53 (UTC+0300)	2024-08-31 18:54  (UTC+0300)	60	8,5	1,02	7,48
GR-EMU-S6792111181543869683893493-L	Alfa Techniki Episkevastiki Skafon Ltd	A	0	2024-09-01 10:33 (UTC+0300)	2024-09-01 10:34  (UTC+0300)	1	0	0	0

-->

