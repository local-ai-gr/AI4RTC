<%-- 
    Document   : charts
    Created on : Apr 27, 2023, 10:46:21 PM
    Author     : nsofias
--%>
<%
    String field = request.getParameter("field");
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
                    animationEnabled: true,
                    zoomEnabled: true,
                    theme: "light2",
                    title: {
                        text: "Real Time KW",
                        fontSize: 18
                    },
                    axisX: {
                        title: "Location",
                        titleFontSize: 14,
                        labelFontSize: 10,
                        //maintainAspectRatio: false,
                        interval: 1
                    },
                    axisY: {
                        title: "KW",
                        titleFontSize: 14,
                        tickLength: 5,
                        labelFontSize: 10
                    },
                    data: [{
                            type: "bar",
                            axisYType: "secondary",
                            maintainAspectRatio: false,
                            dataPoints: dataPoints
                        }]
                };

                function addData(data) {
                    for (var i = 0; i < data.length; i++) {
                        dataPoints.push({
                            label: data[i].label,
                            y: data[i].val
                        });
                    }

                    $("#chartContainer").CanvasJSChart(options);

                }
                $.getJSON("RealTimeKW", addData);
            };
        </script>
    </head>
    <body>

        <div id="chartContainer" style="height:100vh; width:100vh;"></div>

    </body>
</html>
