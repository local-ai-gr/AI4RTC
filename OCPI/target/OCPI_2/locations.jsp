<%-- 
    Document   : locations
    Created on : Jun 30, 2024, 6:02:44 PM
    Author     : nsofias
--%>

<%@page import="jakarta.servlet.jsp.JspWriter"%>
<%@page import="org.bson.Document"%>
<%@page import="java.util.ArrayList"%>
<%@page import="nsofiasLib.time.TimeStamp1"%>
<%@page import="nsofiasLib.others.Parameters"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="java.nio.charset.Charset"%>
<%@page import="java.io.IOException"%>
<%@page import="nsofiasLib.others.Helpme"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="model.Connector"%>
<%@page import="model.EVSE"%>
<%@page import="model.GeoLocation"%>
<%@page import="java.util.List"%>
<%@page import="com.mongodb.client.model.Filters"%>
<%@page import="nsofiasLib.databases.Mongo"%>
<%@page import="java.util.Collection"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="model.Location"%>
<%@page import="nsofiasLib.others.SimpleDaemon"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    ServletContext myContext = session.getServletContext();
    Mongo myMongo = (Mongo) myContext.getAttribute("myMongo");
    String delete = request.getParameter("delete");
    if (request.getParameter("delete") != null) {
        myMongo.delete("locations", Filters.eq("id", delete));
    }
    if (request.getParameter("deleteAll") != null) {
        myMongo.delete("locations", new Document());
    }

    if (request.getParameter("loadFromFile") != null) {
        String filename = System.getenv("APPLICATIONS_PATH") + "/OCPI/conf/parity_db.csv";
        loadFromFile(filename, StandardCharsets.ISO_8859_1, myMongo);
    }
    Collection<Location> locations = myMongo.find("locations", new Document(), false, Location.class);
    final JspWriter out1 = out;
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Locations</title>
    </head>
    <body>
        <a href="locations.jsp?loadFromFile">load From File</a>
        <a href="locations.jsp?deleteAll">Delete All</a>
        <h1>Locations (from DB)</h1>

        <%
            if (request.getParameter("loadFromFile") != null) {
                try {

                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
            for (Location l : locations) {
                try {%>
        <p><%=new Gson().toJson(l)%> <a href="locations.jsp?delete=<%=l.getId()%>">delete</a>

            <% } catch (Exception e) {
                    }
                }

            %>
    </body>
</html>


<%!
    void loadFromFile(String filename, Charset charSet, Mongo myMongo) throws IOException {
        /*
0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15
id	name	adress	city	postal_code	state	country	latitute	longitude	evse_id	id	standard	power_type	max_voltage	max_amperage	max_electric_power
        
         */
        List<Location> myLocationsFound = new ArrayList();
        List<String> locations = Helpme.getFileRowsAsList(filename, charSet);
        locations.stream().filter(s->!s.startsWith("#")).map(s -> s.split(";")).forEach(locationData -> {
            Location myLocation = new Location();
            myLocation.setId(locationData[0]);
            if (myLocationsFound.indexOf(myLocation) == -1) {
                myLocation.setName(locationData[1]);
                myLocation.setAddress(locationData[2]);
                myLocation.setCity(locationData[3]);
                GeoLocation myCoordinates = new GeoLocation();
                myCoordinates.setLatitude(locationData[7].replace(",", "."));
                myCoordinates.setLongitude(locationData[8].replace(",", "."));
                myLocation.setCoordinates(myCoordinates);
                myLocationsFound.add(myLocation);
            } else {
                myLocation = myLocationsFound.get(myLocationsFound.indexOf(myLocation));
            }
            //--------
            EVSE myEVSE = new EVSE();
            myEVSE.setUid(locationData[9]);
            if (myLocation.getEvses().indexOf(myEVSE) == -1) {
                myLocation.getEvses().add(myEVSE);
            } else {
                myEVSE = myLocation.getEvses().get(myLocation.getEvses().indexOf(myEVSE));
            }
            //
            Connector myConnector = new Connector();
            myConnector.setId(locationData[10]);
            if (myEVSE.getConnectors().indexOf(myConnector)==-1){
            myConnector.setStandard(locationData[11]);
            myConnector.setPower_type(locationData[12]);
            myConnector.setVoltage(Integer.parseInt(locationData[13]));
            myConnector.setAmperage(Integer.parseInt(locationData[14]));
            myConnector.setMax_electric_power(Integer.parseInt(locationData[15]));            
                myEVSE.getConnectors().add(myConnector);
            }
            System.out.println(new Gson().toJson(myLocation));
            myLocationsFound.add(myLocation);
        });
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");     
        LocalDateTime localDateTime = new TimeStamp1().toLocalDateTime();
        myLocationsFound.forEach(l -> {
            myMongo.add("locations", l, localDateTime);
        });

    }
%>
