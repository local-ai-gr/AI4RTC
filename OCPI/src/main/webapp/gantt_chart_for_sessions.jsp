<%@page import="nsofiasLib.time.TimeStamp1"%>
<%@page import="java.net.URLEncoder"%>
<%
    //------- location & timestamps--------
    String location = request.getParameter("location") != null && !request.getParameter("location").isEmpty() ? request.getParameter("location") : "";
    String timeFrom = request.getParameter("timeFrom");
    if (timeFrom == null) {
        TimeStamp1 timeFromT = new TimeStamp1();
        timeFromT.addDays(-7);
        timeFrom = timeFromT.getNowUnformated_elegant().substring(0, 16).replaceAll("-", "/").replaceAll("T", " ");
    }
    String timeTo = request.getParameter("timeTo");
    if (timeTo == null) {
        TimeStamp1 timeToT = new TimeStamp1();
        timeTo = timeToT.getNowUnformated_elegant().substring(0, 16).replaceAll("-", "/").replaceAll("T", " ");
    }
    //--------------------------------
    String params = location != null ? "?type=gannt&location="+location : "?type=gannt";
    params = params+"&timeFrom="+timeFrom+"&timeTo="+timeTo;
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
                height: 800px;
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
                    // Make a GET request to the remote URL
                    const response = await fetch('<%=sourceUrl%>');
                    console.log(response);
                    // Parse the JSON response
                    const eventData = await response.json();
                    initializeTimeline(eventData);
                } catch (error) {
                    console.error('Error fetching event data:', error);
                }
            }


            // Function to initialize the timeline with the fetched event data
            function initializeTimeline(eventData) {
                // Create a DataSet from the fetched event data
                var items = new vis.DataSet(eventData);
                var groupIds = [...new Set(items.get().map(item => item.group))];
                // Automatically create groups based on the unique group IDs
                var groups = new vis.DataSet(
                        groupIds.map(groupId => ({id: groupId, content: groupId}))
                        );
                var minStartTime = new Date(Math.min.apply(null, eventData.map(event => new Date(event.start))));
                var maxEndTime = new Date(Math.max.apply(null, eventData.map(event => new Date(event.end))));
                var startFrom = new Date(maxEndTime);
                startFrom.setDate(startFrom.getDate() - 1);

                var options = {
                    width: '100%', // Set the width of the timeline container to 100%
                    min: minStartTime, // Set the start time of the display
                    max: maxEndTime, // Set the end time of the display
                    stack: false, // Disable event stacking to prevent wrapping
                    //start: startFrom, // 30 days back from today
                    //end: maxEndTime,
                    align: "center",
                    groupOrder: 'content',
                    //horizontalScroll: true,
                    editable: false
                };
                var timeline = new vis.Timeline(document.getElementById('timeline'), items, groups, options);
                // Step 1: Show the full timeline
                timeline.fit();

                // Step 2: After a small delay, zoom into the last day
                /*
                 setTimeout(() => {
                 timeline.setWindow(startFrom, maxEndTime);
                 }, 500);*/

            }
            fetchEventData();
        </script>
    </body>
</html>
