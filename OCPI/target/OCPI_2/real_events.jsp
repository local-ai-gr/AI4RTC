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
    ServletContext servletContext = session.getServletContext();
    List<String> events = (List<String>) servletContext.getAttribute("events");
    final JspWriter out1 = out;
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sessions</title>
    </head>
    <body>
        <h1>Events</h1>

        <%
            events.stream().forEach(v -> {
                try {
                    out1.println("<p>" + new Gson().toJson(v));
                } catch (Exception e) {
                }
            }
            );
        %>
    </body>
</html>


