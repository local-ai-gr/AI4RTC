<%-- 
    Document   : charts
    Created on : Apr 27, 2023, 10:46:21 PM
    Author     : nsofias
--%>
<%@page import="nsofiasLib.time.TimeStamp1"%>
<%@page import="nsofiasLib.utils.TimeSeriesParamCounters"%>
<%
    //------- location & timestamps--------
    String location = request.getParameter("location") != null && !request.getParameter("location").isEmpty() ? request.getParameter("location") : "";
    String timeFrom = request.getParameter("timeFrom");
    if (timeFrom == null) {
        TimeStamp1 timeFromT = new TimeStamp1();
        timeFromT.addDays(-7);
        timeFrom = timeFromT.getNowUnformated_elegant().substring(0, 19).replaceAll("-", "/").replaceAll("T", " ");
    }
    String timeTo = request.getParameter("timeTo");
    if (timeTo == null) {
        TimeStamp1 timeToT = new TimeStamp1();
        timeTo = timeToT.getNowUnformated_elegant().substring(0, 19).replaceAll("-", "/").replaceAll("T", " ");
    }
    //--------------------------------
    String type = request.getParameter("type") != null ? request.getParameter("type") : "";
    String title = "", yTitle = "";
    if (type.equals("erlangs")) {
        title = "Utilization";
        yTitle = "Utilization %";
    } else if (type.equals("kwh")) {
        title = "Energy consumption";
        yTitle = "kWh";
    } else if (type.equals("power")) {
        title = "Power";
        yTitle = "kW";
    }

    String params = location != null ? "?type=" + type + "&location=" + location : "?type=" + type;
    params = params + "&timeFrom=" + timeFrom + "&timeTo=" + timeTo;
    String sourceUrl = request.getRequestURL().toString().replace("time_charts_for_sessions_1.jsp", "") + "sessionsGanntServlet" + params;
    out.println("<p>");
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>

<html>
    <meta charset="UTF-8">
    <head>
        <title>Visualization Page</title>      
        <!-- Updated CDN for vis-timeline -->
        <link rel="stylesheet" href="https://unpkg.com/vis-timeline@latest/styles/vis-timeline-graph2d.min.css">
        <script src="https://unpkg.com/vis-timeline@latest/standalone/umd/vis-timeline-graph2d.min.js"></script>
        <style>
            /* Rotate labels vertically */
            .vis-time-axis .vis-text {
                transform: rotate(-45deg); /* Rotate text */
                transform-origin: top left;
                white-space: nowrap;
            }
        </style>
        <script>
            async function fetchEventData() {
                try {
                    const response = await fetch('<%=sourceUrl%>');
                    const eventData = await response.json();
                    initializeGraph(eventData);
                } catch (error) {
                    console.error('Error fetching event data:', error);
                }
            }

            function initializeGraph(eventData) {

                const items = new vis.DataSet(eventData.map(event => ({
                        x: new Date(event.start), // Start time on X-axis
                        y: event.value, // Value on Y-axis (height of bars)
                        end: new Date(event.end) // End time for intervals      
                    })));
                var minStartTime = new Date(Math.min.apply(null, eventData.map(event => new Date(event.start))));
                var maxEndTime = new Date(Math.max.apply(null, eventData.map(event => new Date(event.end))));
                var startFrom = new Date(maxEndTime);
                startFrom.setMonth(maxEndTime.getMonth() - 1);

                const options = {
                    min: minStartTime,
                    max: maxEndTime,
                    style: 'bar', // Sets bar style visualization
                    drawPoints: false,
                    height: '85%',
                    dataAxis: {// Y-axis configuration
                        left: {
                            title: {
                                text: '<%=yTitle%>'
                            }
                        }
                    },
                    timeAxis: {
                        //scale: 'day', // Time scale granularity
                        //step: 6
                    }
                };

                // Create the graph2d chart
                const container = document.getElementById('visualization');
                new vis.Graph2d(container, items, options);
            }

            fetchEventData();
        </script>
    </head>
    <body>

        <div id="visualization"></div>
    </body>
</html>