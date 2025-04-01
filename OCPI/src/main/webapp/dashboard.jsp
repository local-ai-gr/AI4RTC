<%-- 
    Document   : dashboard
    Created on : Oct 20, 2024, 4:26:45 PM
    Author     : gsofi
--%>

<%@page import="nsofiasLib.time.TimeStamp1"%>
<%@page import="org.bson.Document"%>
<%@page import="java.util.List"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="model.Session"%>
<%@page import="java.util.Comparator"%>
<%@page import="com.mongodb.client.model.Filters"%>
<%@page import="nsofiasLib.databases.Mongo"%>
<%@page import="model.Location"%>
<%@page import="java.util.Collection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    ServletContext myContext = session.getServletContext();
    Mongo myMongo = (Mongo) myContext.getAttribute("myMongo");
    //------------ active locatioons ----------
    Collection<Session> sessions = myMongo.find("sessions", new Document(), true, Session.class);
    List<String> activelocationIds = sessions.stream().map(s -> s.getLocation_id()).collect(Collectors.toList());
    //---
    Collection<Location> locations = myMongo.find("locations", new Document(), false, Location.class);
    //------- location & timestamps--------
    String location = request.getParameter("location") != null && !request.getParameter("location").isEmpty() ? request.getParameter("location") : "";
    String timeFrom = request.getParameter("timeFrom");
    if (timeFrom == null) {
        TimeStamp1 timeFromT = new TimeStamp1();
        timeFromT.addDays(-10);
        timeFrom = timeFromT.getNowUnformated_elegant().substring(0, 19).replaceAll("-", "/").replaceAll("T", " ");
    }
    String timeTo = request.getParameter("timeTo");
    if (timeTo == null) {
        TimeStamp1 timeToT = new TimeStamp1();
        timeTo = timeToT.getNowUnformated_elegant().substring(0, 19).replaceAll("-", "/").replaceAll("T", " ");
    }
    //--------------------------------
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="js/jquery-ui/external/jquery/jquery.js" type="text/javascript"></script> 
        <script src="js/jquery-ui/jquery-ui.js" type="text/javascript"></script>     
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-datetimepicker/2.5.20/jquery.datetimepicker.min.css">
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-datetimepicker/2.5.20/jquery.datetimepicker.full.min.js"></script>        
        <title>JSP Page</title>
        <script>
            $(function () {
                // Initialize both datetime pickers in one place
                $("#DatepickerTo").datetimepicker({
                    format: "Y-m-d h:m:s"  // Use 'Y' for a four-digit year
                });
                $("#DatepickerFrom").datetimepicker({
                    format: "Y-m-d h:m:s"
                });
            });
        </script>       
        <style>
            body {
                background-color:  whitesmoke;
                color: white;
            }

            .iframe-grid {
                display: grid;
                grid-template-columns: 1fr 1fr; /* 2 columns */
                grid-template-rows: 1fr 1fr;    /* 2 rows */
                gap: 10px;                      /* Optional: space between iframes */
                width: 95vw;                   /* Full viewport width */
                height: 85vh;                  /* Full viewport height */
            }

            .iframe-grid iframe {
                width: 100%;
                height: 100%;
            }
            #customSelect {
                width: 200px;
                padding: 10px;
                border: 2px solid #007BFF;
                border-radius: 10px;
                background-color: #f9f9f9;
                color: #333;
                font-size: 16px;
                font-family: Arial, sans-serif;
                appearance: none;
                outline: none;
                cursor: pointer;
                transition: all 0.3s ease;
            }

            #customSelect:hover {
                border-color: #0056b3;
                background-color: #e9ecef;
            }

            #customSelect:focus {
                border-color: #28a745;
                background-color: #fff;
            }

            #customSelect option {
                padding: 10px;
            }
            #customLabel {
                color: black;  /* Makes the label text black */
                font-size: 16px;
                font-family: Arial, sans-serif;
            }
        </style>        
    </head>
    <body>

        <form>
            <img src="img/AI4RTC.gif" width="70" height="25"/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <label id="customLabel" for="customSelect">Select location:</label>
            <select  id="customSelect" onchange="this.form.submit()" name="location">
                <%
                    JspWriter out1 = out;
                    out1.println("<option value=''>" + "ALL" + "</option>");
                    locations.stream().filter(l -> !l.getName().isEmpty()).sorted(Comparator.comparing(l -> l.getName())).forEach(l -> {
                        try {
                            String name = activelocationIds.contains(l.getId()) ? l.getName() + " >>>>> PILOT PROJECT <<<<<" : l.getName();
                            if (l.getId().equals(location)) {
                                out1.println("<option selected value='" + l.getId() + "'>" + name + "</option>");
                            } else {
                                out1.println("<option value='" + l.getId() + "'>" + name + "</option>");
                            }
                        } catch (Exception e) {
                        }
                    });
                %>
            </select>
            &nbsp;Time from: <input type="text" onchange="this.form.submit()" name="timeFrom" id="DatepickerFrom" value="<%=timeFrom%>">
            &nbsp;Time until: <input type="text" onchange="this.form.submit()" name="timeTo" id="DatepickerTo" value="<%=timeTo%>">
        </form>  

        <div class="iframe-grid">
            <iframe src="map.jsp?location=<%=location%>" style="width: 100%; height: 50vh;"></iframe>
                <%if (location != null && !location.isEmpty()) {%> 
            <iframe src="gantt_chart_for_sessions.jsp?location=<%=location%>&timeFrom=<%=timeFrom%>&timeTo=<%=timeTo%>" style="width: 100%; height: 50vh;">></iframe> 
            <iframe src="time_charts_for_sessions_1.jsp?type=erlangs&tittle=Utilization&location=<%=location%>&timeFrom=<%=timeFrom%>&timeTo=<%=timeTo%>" style="width: 100%; height: 50vh;"></iframe>     
            <iframe src="time_charts_for_sessions_1.jsp?type=power&location=<%=location%>&timeFrom=<%=timeFrom%>&timeTo=<%=timeTo%>" style="width: 100%; height: 50vh;"></iframe>
                <%}%>
        </div>
    </body>
</html>
