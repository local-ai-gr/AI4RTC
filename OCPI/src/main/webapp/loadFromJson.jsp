<%-- 
    Document   : loadFromJson
    Created on : Sep 26, 2024, 3:25:56 PM
    Author     : nsofias
--%>
<%@page import="nsofiasLib.databases.Mongo"%>
<%@page import="nsofiasLib.time.TimeStamp1"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.util.Arrays"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.ArrayList"%>
<%@page import="model.Location"%>
<%@page import="java.util.List"%>
<%
    ServletContext myContext = session.getServletContext();
    Mongo myMongo = (Mongo) myContext.getAttribute("myMongo");
    String location = request.getParameter("location");
    if (location != null) {
        location = location.trim();
        if (!location.isEmpty()) {
            try {
                Location myLocation = new Gson().fromJson(location, Location.class);
                LocalDateTime localDateTime = new TimeStamp1().toLocalDateTime();
                myMongo.add("locations", myLocation, localDateTime);
                out.println("<p>" + myLocation.infoSummaryHTML() + " added to DB");
            } catch (Exception e) {
                out.println("<p>load from Json error:" + e.toString());
            }
        }
        out.println("<h2> you can close this window now");
        return;
    }
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello Locations (Json)!</h1>
        <form method="POST">
            <textarea name="location" rows="25" cols="100">
            </textarea>
            <p><input type="submit" value="submit" />
        </form>


    </body>
</html>
