<%@page import="java.net.URLEncoder"%>
<%
    String location = request.getParameter("location");
    String params = location != null ? "?type=gannt&location=" + location : "?type=gannt";
    String sourceUrl = "sessionsGanntServlet" + params;
    //out.println(sourceUrl);
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"> 
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>vis.js Timeline Example with Remote JSON Data</title>
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
                    /*
                     eventData = eventData.filter(event => {
                     const start = new Date(event.start);
                     const end = new Date(event.end);
                     const isValidStart = !isNaN(start.getTime()); // Check if start date is valid
                     const isValidEnd = !isNaN(end.getTime());     // Check if end date is valid
                     
                     if (!isValidStart || !isValidEnd) {
                     console.warn(`Invalid event data:`, event); // Optionally log invalid events
                     return false; // Exclude invalid events
                     }
                     return true;
                     });               */
                    // Initialize the timeline with the fetched event data
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
                startFrom.setMonth(maxEndTime.getMonth() - 1);
                var options = {
                    width: '100%', // Set the width of the timeline container to 100%
                    min: minStartTime, // Set the start time of the display
                    max: maxEndTime, // Set the end time of the display
                    stack: false, // Disable event stacking to prevent wrapping
                    start: startFrom  // 30 days back from today
                };
                var timeline = new vis.Timeline(document.getElementById('timeline'), items, groups, options);
                // Adjust the width of events to fit them all without wrapping
                //timeline.setOptions({width: minContainerWidth + 'px'});
            }
            fetchEventData();
        </script>
    </body>
</html>
