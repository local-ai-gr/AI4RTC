<%-- 
    Document   : locations
    Created on : Jun 30, 2024, 6:02:44 PM
    Author     : nsofias
xxx
--%>

<%@page import="java.util.Comparator"%>
<%@page import="simulation.Event"%>
<%@page import="org.bson.Document"%>
<%@page import="com.mongodb.client.model.Filters"%>
<%@page import="java.util.Collection"%>
<%@page import="nsofiasLib.databases.Mongo"%>
<%@page import="model.CDR"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="model.Location"%>
<%@page import="nsofiasLib.others.SimpleDaemon"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    ServletContext myContext = session.getServletContext();
    Mongo myMongo = (Mongo) myContext.getAttribute("myMongo");
    Collection<Event> events = myMongo.find("events", new Document(), false,Event.class);    
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Events</title>
    </head>
    <body>
        <h1>Events (from DB)</h1>
        <%
            final JspWriter out1 = out;
            events.stream().sorted(Comparator.comparing(e->e.getEventTime())).forEach(v -> {
                try {
                    out1.println("<p>" + new Gson().toJson(v));
                } catch (Exception e) {
                }
            }
            );
        %>
    </body>
</html>