<%-- 
    Document   : charts
    Created on : Apr 27, 2023, 10:46:21 PM
    Author     : nsofias
--%>


<%@page import="nsofiasLib.utils.TimeSeriesParamCounters"%>
<%
    String collection = request.getParameter("collection") != null ? request.getParameter("collection") : "sessions";
    String matchField = request.getParameter("matchField");
    String matchValue = request.getParameter("matchValue");
    String field = request.getParameter("field");
    String type = request.getParameter("type") != null ? request.getParameter("type") : nsofiasLib.databases.Mongo.TYPE_COUNT;
    String step = request.getParameter("step") != null ? request.getParameter("step") : nsofiasLib.databases.Mongo.STEP_HOUR;
    String sourceUrl = "TimeAggregationServlet"
            + "?collection=" + collection
            + "&field=" + field
            + "&type=" + type
            + "&step=" + step
            + "&matchField=" + matchField
            + "&matchValue=" + matchValue;
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
    <meta charset="UTF-8">
    <head>
        <script src="js/canvas/jquery-1.11.1.min.js"></script>
        <script src="js/canvas/jquery.canvasjs.min.js"></script>
        <script>
            window.onload = function () {
                var dataPoints = [];
                var options = {
                    title: {
                        text: "<%=field%>: <%=type%> per  <%=step%> for <%=matchValue%>",
                                        fontSize: 18
                                    },
                                    zoomEnabled: true, // Enable zooming
                                    panEnabled: true, // Enable panning
                                    animationEnabled: true,
                                    theme: "light2",

                                    axisX: {
                                        title: "Date and Time",

                                        labelFormatter: function (e) {
                                            const date = e.label;
                                            const options = {
                                                year: '2-digit', // 'yy'
                                                month: '2-digit', // 'mm'
                                                day: '2-digit', // 'dd'
                                                hour: '2-digit', // 'hh'
                                                minute: '2-digit', // 'mm'
                                                hour12: false     // Use 24-hour format (for Greece)
                                            };
                                            return date.toLocaleString('el-GR', options);
                                        },
                                        interval: 10, // Number of units in intervalType
                                        intervalType: "hour", // Interval type set to hours
                                        labelAngle: -45
                                    },
                                    axisY: {
                                        title: "Values"
                                    },
                                    data: [{
                                            type: "column",
                                            axisYType: "secondary",
                                            dataPoints: dataPoints
                                        }]
                                };

                                function addData(data) {
                                    for (var i = 0; i < data.length; i++) {
                                        dataPoints.push({
                                            label: new Date(data[i].label),
                                            y: data[i].val
                                        });
                                    }

                                    $("#chartContainer").CanvasJSChart(options);

                                }
                                $.getJSON('<%=sourceUrl%>', addData);
                            };
        </script>
    </head>
    <body>        
        <div id="chartContainer" style="height: 370px; width: 100%;"></div>
    </body>
</html>
