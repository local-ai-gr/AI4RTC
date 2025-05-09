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
    System.out.println("sourceUrl=" + sourceUrl);
    if (location != null) {
        // out.println("<h1>timeline for Location" + location + "</h1>");
    }
    //out.println("<h1>timeline for Location" + location + "</h1>");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"> 
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>SESSIONS timeline</title>
        <link rel="stylesheet" href="js/vis-4.21/vis.min.css">           
        <script src="js/vis-4.21/vis.min.js"></script>
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
                    console.log(minStartTime);
                    var maxEndTime = new Date(Math.max.apply(null, eventData.map(event => new Date(event.end))));
                    console.log(maxEndTime);

                    var options = {
                        //width: '100%',
                        //height: '80%',
                        autoResize: true,
                        min: minStartTime,
                        max: maxEndTime,
                        stack: false,
                        align: "center",
                        groupOrder: 'content',
                        editable: false
                    };
                    try {
                        var timeline = new vis.Timeline(document.getElementById('timeline'), items, groups, options);
                        //timeline.fit();
                    } catch (error) {
                        console.error('Error timeline.fit:', error);
                    }
                } else {
                    console.error("No event data found");
                }
            }
            fetchEventData();
        </script>
        <style>
            /* Set the height of the timeline container */
            #timeline {
                height: 400px;
            }
        </style>
        <style>
            /* Ensure the container div takes full available space */
            #timeline {
                width: 100%;
                height: 100vh; /* Use 100vh or any desired height */
                margin: 0;
                padding: 0;
            }

            /* Optional: prevent any overflow */
            body, html {
                margin: 0;
                padding: 0;
                overflow: hidden;
            }
        </style>         
    </head>
    <body>            
        <!--<a href="<%=sourceUrl%>">data</a> -->
        <div id="timeline"></div>
    </body>
</html>
