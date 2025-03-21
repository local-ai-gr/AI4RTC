
<%@page import="org.bson.Document"%>
<%@page import="model.Session"%>
<%@page import="com.mongodb.client.model.Filters"%>
<%@page import="nsofiasLib.databases.Mongo"%>
<%@page import="java.util.Collection"%>
<%@page import="model.Location"%>
<%@page import="nsofiasLib.others.SimpleDaemon"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="nsofiasLib.others.Helpme"%>
<%@page import="java.util.Optional"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="CoordsUtils.Circle"%>
<%@page import="java.util.Set"%>
<%@page import="CoordsUtils.SmallestClosingCircle"%>
<%@page import="CoordsUtils.Outliers"%>
<%@page import="CoordsUtils.Point"%>
<%@page import="nsofiasLib.utils.Counters"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Collections"%>
<%@page import="nsofiasLib.time.TimeStamp1"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="nsofiasLib.utils.URLContextReader_object"%>
<%@page import="java.util.ArrayList"%>
<%@page import="nsofiasLib.others.Parameters"%>
<%@page import="nsofiasLib.utils.Counters1"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    response.setCharacterEncoding("utf-8");
    ServletContext myContext = session.getServletContext();
    Mongo myMongo = (Mongo) myContext.getAttribute("myMongo");
    Collection<Location> locations = myMongo.find("locations", new Document(), false, Location.class);
    //------------ active locatioons ----------
    Collection<Session> sessions = myMongo.find("sessions", new Document(), true, Session.class);
    List<String> activelocationIds = sessions.stream().map(s -> s.getLocation_id()).collect(Collectors.toList());
    //---
    Circle circle = new Circle(new Point(23.4248902, 38.980903), 2000);//23.8048027,38.0080677
    List<Point> points_ALL = new ArrayList<>();
    List<Point> points_pilot = new ArrayList<>();
    String location = request.getParameter("location") != null && !request.getParameter("location").isEmpty() ? request.getParameter("location") : "";
    points_ALL = locations.stream()
            .filter(v -> location == null || location.isEmpty() || v.getId().equals(location))
            .map(v -> {
                try {
                    Double[] latlon = {Double.parseDouble(v.getCoordinates().getLatitude()), Double.parseDouble(v.getCoordinates().getLongitude())};
                    if (latlon != null) {
                        Point myPoint = new Point(v.infoSummaryHTML(), latlon[1], latlon[0]);
                        if (activelocationIds.contains(v.getId())) {
                            myPoint.setIconUrl("./img/power.png");
                            points_pilot.add(myPoint);
                        } else {
                            myPoint.setIconUrl("./img/marker-gold.png");
                        }
                        return myPoint;
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(v -> v != null)
            .collect(Collectors.toList());

    if (!points_pilot.isEmpty()) {
        circle = SmallestClosingCircle.makeCircle(points_pilot);
    } else {
        circle = SmallestClosingCircle.makeCircle(points_ALL);
    }
%>
<!DOCTYPE html>
<html>  

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>EVSE Locations</title>
        <link rel="stylesheet" href="js/ol/ol.css" type="text/css">
        <style>
            .map {
                height: 100vh;
                width: 100vw;
            }

            .ol-popup {
                position: absolute;
                background-color: white;
                color:  darkgreen;
                padding: 15px;
                border: 1px solid black;
                border-radius: 5px;
                min-width: 500px;
                z-index: 1000; /* Ensure popup is on top */
            }
            .ol-popup table {
                width: 100%;
                border: 1px solid #ddd; /* Add border around the entire table */
                border-collapse: collapse; /* Collapse borders to ensure they are neatly combined */
                margin-top: 10px;
            }
/* Table Data Styling */
.ol-popup td {
    padding: 8px 12px;   
    border: 1px solid #ddd; /* Border around each data cell */
}            
            .ol-popup-closer {
                text-decoration: none;
                position: absolute;
                top: 5px;
                right: 10px;
                color: #000;           /* Black color for visibility */
                font-size: 18px;       /* Make font size large */
                font-weight: bold;     /* Make it bold */
                background: none;      /* No background */
                border: none;          /* No border */
                cursor: pointer;       /* Pointer cursor */
                font-family: Arial, sans-serif; /* Ensure a standard font is used */
            }

        </style>
    </head>

    <body>        
        <div id="map" class="map"></div>
        <div id="popup" class="ol-popup">
            <span id="popup-closer" class="ol-popup-closer">X</span>
            <div id="popup-content"></div>
        </div>

        <!-- Load OpenLayers from CDN -->
        <script src="js/ol/ol.js"></script>

        <script>
            // Step 1: Initialize the map
            const map = new ol.Map({
            target: 'map',
                    layers: [
                            new ol.layer.Tile({
                            source: new ol.source.OSM(), // OpenStreetMap layer
                            })
                    ],
                    view: new ol.View({
                    center: ol.proj.fromLonLat([<%=circle.c.x%>, <%=circle.c.y%>]), // Centered on the globe
            <%if (points_ALL.size() > 1) {%>
                    zoom: 10 // Initial zoom level  
            <%} else {%>
                    zoom: 16
            <%}%>
                    })
            });
            // Step 2: Array of Points (coordinates and descriptions)
            const points = [
            <%for (Point myPoint : points_ALL) {

            %>
            {coords: [<%=myPoint.x%>, <%=myPoint.y%>], desc: '<%=myPoint.name%>', iconSrc: '<%=myPoint.getIconUrl()%>'},
            <%}%>


            ];
// Create vector source and layer for markers
            const vectorSource = new ol.source.Vector();
            const vectorLayer = new ol.layer.Vector({
            source: vectorSource
            });
            map.addLayer(vectorLayer);
// Function to create a marker with dynamic styling
            function createMarker(point) {
            const marker = new ol.Feature({
            geometry: new ol.geom.Point(ol.proj.fromLonLat(point.coords))
            });
            marker.set('description', point.desc); // Store description
            marker.set('iconSrc', point.iconSrc); // Set custom icon source if provided

            // Create a style for this specific marker
            const iconStyle = new ol.style.Style({
            image: new ol.style.Icon({
            anchor: [0.5, 1],
                    src: point.iconSrc || './img/marker-green.png', // Use point-specific icon or default
                    scale: point.scale || 1.2 // Use custom scale if provided, or default to 0.8
            })
            });
            marker.setStyle(iconStyle); // Apply style to marker
            vectorSource.addFeature(marker);
            }

// Step 3: Add points to the map with dynamic styling
            points.forEach(point => createMarker(point));
            // Step 4: Create a popup overlay
            const popupElement = document.getElementById('popup');
            const popupContent = document.getElementById('popup-content');
            const popupCloser = document.getElementById('popup-closer');
            const overlay = new ol.Overlay({
            element: popupElement,
                    autoPan: true,
                    autoPanAnimation: {duration: 250}
            });
            map.addOverlay(overlay);
            // Close popup when "X" is clicked
            popupCloser.onclick = function () {
            overlay.setPosition(undefined);
            popupCloser.blur();
            };
            // Step 5: Add a click event listener for showing popups
            map.on('singleclick', function (evt) {
            const feature = map.forEachFeatureAtPixel(evt.pixel, function (feature) {
            return feature;
            });
            if (feature) {
            const coordinates = feature.getGeometry().getCoordinates();
            const description = feature.get('description');
            console.log('Description:', description); // Log the description
            popupContent.innerHTML = description; // Set the content using innerText
            overlay.setPosition(coordinates);
            } else {
            overlay.setPosition(undefined); // Hide popup if clicked elsewhere
            }
            });
        </script>

    </body>

</html>
