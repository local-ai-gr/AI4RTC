<%@page import="nsofiasLib.time.TimeStamp1"%>
<%@page import="java.net.URLEncoder"%>
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
    String params = location != null ? "?type=gannt&location=" + location : "?type=gannt";
    params = params + "&timeFrom=" + URLEncoder.encode(timeFrom, "utf8") + "&timeTo=" + URLEncoder.encode(timeTo, "utf8");
    String sourceUrl = "sessionsGanntServlet" + params;
    if (location != null) {
        // out.println("<h1>timeline for Location" + location + "</h1>");
    }
    //out.println(sourceUrl);
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"> 
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>SESSIONS timeline</title>
        <link rel="stylesheet" href="js/vis-4.21/vis.min.css">
        <script src="js/vis-4.21/vis.min.js"></script>
        <style>
            /* Set the height of the timeline container */
            #timeline {
                height: 400px;
            }            
        </style>
    </head>
    <body>            
        <h3>Sessions timeline</h3>
        <!--<a href="<%=sourceUrl%>">data</a> -->
        <div id="timeline"></div>

        <script>
            // Function to fetch event data from a remote URL
            async function fetchEventData() {
                try {
                    const response = await fetch('<%=sourceUrl%>');
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    const eventData = await response.json();
                    console.log(eventData);  // Log event data to verify its format
                    initializeTimeline(eventData);
                } catch (error) {
                    console.error('Error fetching event data:', error);
                }
            }


            // Function to initialize the timeline with the fetched event data
            function initializeTimeline(eventData) {
                if (eventData && eventData.length > 0) {
                    var items = new vis.DataSet(eventData);
                    var groupIds = [...new Set(items.get().map(item => item.group))];
                    var groups = new vis.DataSet(
                            groupIds.map(groupId => ({id: groupId, content: groupId}))
                            );
                    var minStartTime = new Date(Math.min.apply(null, eventData.map(event => new Date(event.start))));
                    var maxEndTime = new Date(Math.max.apply(null, eventData.map(event => new Date(event.end))));

                    var options = {
                        width: '100%',
                        min: minStartTime,
                        max: maxEndTime,
                        stack: false,
                        align: "center",
                        groupOrder: 'content',
                        editable: false
                    };
                    var timeline = new vis.Timeline(document.getElementById('timeline'), items, groups, options);
                    timeline.fit();
                } else {
                    console.error("No event data found");
                }
            }
            document.addEventListener('DOMContentLoaded', function () {
                fetchEventData();
            });
        </script>
    </body>
</html>
