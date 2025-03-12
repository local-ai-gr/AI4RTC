/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package main;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.model.Filters;
import jakarta.servlet.ServletContext;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.List;
import model.ChargingPeriod;
import model.Location;
import model.Session;
import nsofiasLib.databases.Mongo;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * REST Web Service
 *
 * @author nsofias
 */
@Path("sessions")
public class SessionsResource {


    @Context
    private ServletContext servletContext;

    /**
     * Creates a new instance of SessionsResource
     */
    public SessionsResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{country_code}/{party_id}")
    public Response getJson(
            @PathParam("country_code") String country_code,
            @PathParam("party_id") String party_id) {
        Mongo myMongo = (Mongo) servletContext.getAttribute("myMongo");
        Bson filter = Filters.eq("id", new Document());
        Session oldObject = myMongo.findOne("sessions", filter, Session.class);
        return Response.ok(oldObject.getId()).build();
    }

    /**
     * Retrieves representation of an instance of main.SessionsResource
     *
     * @param country_code
     * @param session_id
     * @param party_id
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{country_code}/{party_id}/{session_id}")
    public Response getJson(
            @PathParam("country_code") String country_code,
            @PathParam("party_id") String party_id,
            @PathParam("session_id") String session_id) {
        Mongo myMongo = (Mongo) servletContext.getAttribute("myMongo");
        Bson filter = Filters.eq("id", session_id);
        Session oldObject = myMongo.findOne("sessions", filter, Session.class);
        if (oldObject != null) {
            return Response.ok(oldObject.getId()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * PUT method for updating or creating an instance of SessionsResource
     *
     * @param country_code
     * @param party_id
     * @param session_id
     * @param incomingData
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{country_code}/{party_id}/{session_id}")
    public Response replaceActiveSession(
            @PathParam("country_code") String country_code,
            @PathParam("party_id") String party_id,
            @PathParam("session_id") String session_id,
            InputStream incomingData) {
        List<String> events = (List<String>) servletContext.getAttribute("events");
        //if (country_code.equals("gr") && party_id.equals("001")) {
            try {                
                String jsonPayload = new String(incomingData.readAllBytes());
                System.out.println("OCPI:SessionsResource PUT jsonPayload:: " + jsonPayload);
                Session mySession = new Gson().fromJson(jsonPayload, Session.class);
                System.out.println("OCPI:SessionsResource PUT: Parsed sessionId is: " + mySession.getId());                
                events.add("PUT sessinon "+ mySession.getId()+" status:"+mySession.getStatus());
                includeSession(session_id, mySession);
                return Response.ok(mySession.getId()).build();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                events.add("PUT sessinon "+ session_id+" error:"+e);
                return Response.status(Response.Status.BAD_REQUEST).entity(e.toString()).build();
            }
        //} else {
        //    return Response.status(Response.Status.BAD_REQUEST).entity("invalid {country_code}/{party_id}").build();
        //}
    }

    /*
        {
            "kwh": 0.5,
            "charging_periods": [{
                    "start_date_time": "2025-01-08T13:09:07.340",
                    "dimensions": [{
                            "type": "ENERGY",
                            "volume": 0.5
                    },
                    {
                            "type": "POWER",
                            "volume": 50.4890304799
                    }]
            }],
            "last_updated": "2025-01-08T13:11:10.372437"
        }    
     */
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{country_code}/{party_id}/{session_id}")
    public Response updateActiveSession(
            @PathParam("country_code") String country_code,
            @PathParam("party_id") String party_id,
            @PathParam("session_id") String session_id,
            InputStream incomingData) {
        List<String> events = (List<String>) servletContext.getAttribute("events");
        //if (country_code.equals("gr") && party_id.equals("001")) {
            try {                                
                // Convert InputStream to String
                String jsonPayload = new String(incomingData.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("updateActiveSession:Updates (PATCH) received jsonPayload: " + jsonPayload);
                // Parse JSON using Gson
                Gson gson = new Gson();
                //Map<String, Object> updates = gson.fromJson(jsonPayload, new TypeToken<Map<String, Object>>() { }.getType());
                Update myUpdate = gson.fromJson(jsonPayload, Update.class);
                System.out.println("updateActiveSession:Updates received: " + new Gson().toJson(myUpdate));
                System.out.println("updateActiveSession:session_id: " + session_id);
                //
                Mongo myMongo = (Mongo) servletContext.getAttribute("myMongo");
                Bson filter = Filters.and(Filters.eq("id", session_id), Filters.eq("status", "ACTIVE"));
                Session activeSession = myMongo.findOne("sessions", filter, Session.class);
                //
                if (activeSession == null) {
                    System.out.println("updateActiveSession: NOT FOUND session_id: " + session_id);
                    return Response.status(Response.Status.NOT_FOUND).entity("ACTIVE SESSION NOT FOUND").build();
                }
                // Apply updates
                System.out.println("updateActiveSession:energyData.getStatus: " + myUpdate.getStatus());
                System.out.println("updateActiveSession:energyData.getKwh: " + myUpdate.getKwh());
                System.out.println("updateActiveSession:energyData.getCharging_periods: " + new Gson().toJson(myUpdate.getCharging_periods()));
                System.out.println("updateActiveSession:session_id: " + session_id);
                if (myUpdate.getStatus() != null) {
                    activeSession.setStatus(myUpdate.getStatus());
                    events.add("PATCH sessinon "+ activeSession.getId()+" new status:"+myUpdate.getStatus()+ " @"+myUpdate.getLast_updated());
                }
                if (myUpdate.getKwh() != 0) {
                    activeSession.setKwh(myUpdate.getKwh());
                    events.add("PATCH sessinon "+ activeSession.getId()+" new Kwh:"+myUpdate.getKwh()+ " @"+myUpdate.getLast_updated());
                }
                if (myUpdate.getCharging_periods() != null) {
                    activeSession.setCharging_periods(myUpdate.getCharging_periods());
                    events.add("PATCH sessinon "+ activeSession.getId()+" new Charging_periods:"+new Gson().toJson(myUpdate.getCharging_periods())+ " @"+myUpdate.getLast_updated());
                }
                if (myUpdate.getLast_updated() != null) {
                    activeSession.setLast_updated(myUpdate.getLast_updated());                    
                }
                System.out.println("updateActiveSession:updated session: " + new Gson().toJson(activeSession));
//----------------------
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                LocalDateTime now = LocalDateTime.parse(activeSession.getStart_date_time(), formatter);
                Bson filter1 = Filters.eq("id", session_id);
                myMongo.delete("sessions", filter1);
                System.out.println("updateActiveSession: deleted session_id: " + session_id);
                myMongo.add("sessions", activeSession, now);
                System.out.println("updateActiveSession: updated (added)  session_id: " + session_id);
                return Response.ok(new Gson().toJson(activeSession)).build();
            } catch (JsonSyntaxException e) {
                e.printStackTrace(System.out);
                events.add("PATCH sessinon "+ session_id+" error:"+e);
                return Response.status(Response.Status.BAD_REQUEST).entity("JsonSyntaxException").build();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                events.add("PATCH sessinon "+ session_id+" error:"+e);
                return Response.status(Response.Status.BAD_REQUEST).entity(e.toString()).build();
            }
        //} else {
        //    events.add("PATCH sessinon "+ session_id+" error:Invalid {country_code}/{party_id}:"+country_code+"/"+party_id);
        //    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid {country_code}/{party_id}").build();
        //}
    }

    private void includeSession(String session_id, Session newObject) throws Exception {
        try {
            Mongo myMongo = (Mongo) servletContext.getAttribute("myMongo");
            Collection<Location> locations = myMongo.find("locations", new Document(), false, Location.class);
            Location myLocation = newObject.getlocationData(locations);
            if (myLocation == null) {
                throw new Exception("Location ID not found in Locations database!");
            }
            //newObject.setLocation(myLocation);
            //
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            LocalDateTime now = LocalDateTime.parse(newObject.getStart_date_time(), formatter);
            //
            String id = session_id;
            Bson filter = Filters.eq("id", id);
            Session oldObject = myMongo.findOne("sessions", filter, Session.class);
            if (oldObject != null) {
                //String last_updated = oldObject.getLast_updated();
                System.out.println("OCPI:SessionsResource ewObject.getLast_updated(): " + newObject.getLast_updated());
                //if (!newObject.getLast_updated().equals(last_updated)) {
                // myMongo.replaceOne("sessions", filter, newObject);
                myMongo.delete("sessions", filter);
                myMongo.add("sessions", newObject, now);
                System.out.println("OCPI:SessionsResource myMongo updated: Parsed sessionId is: " + newObject.getId());
                //}
            } else {//"2024-08-08T14:21:59.035"
                myMongo.add("sessions", newObject, now);
                System.out.println("OCPI:SessionsResource myMongo.add: Parsed sessionId is: " + newObject.getId());
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}


class Update {

    private String status;
    private double kwh;
    private List<ChargingPeriod> charging_periods;
    private String last_updated;

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the kwh
     */
    public double getKwh() {
        return kwh;
    }

    /**
     * @param kwh the kwh to set
     */
    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    /**
     * @return the charging_periods
     */
    public List<ChargingPeriod> getCharging_periods() {
        return charging_periods;
    }

    /**
     * @param charging_periods the charging_periods to set
     */
    public void setCharging_periods(List<ChargingPeriod> charging_periods) {
        this.charging_periods = charging_periods;
    }

    /**
     * @return the last_updated
     */
    public String getLast_updated() {
        return last_updated;
    }

    /**
     * @param last_updated the last_updated to set
     */
    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }
}


/* ------- test -------------
curl -X PUT "http://34.136.104.208:8080/OCPI_2/webresources/sessions/gr/001/111111" -H "Content-Type: application/json" -d '{"id":"111111","start_date_time":"2024-12-10T12:04:00.000","end_date_time":"2024-12-10T12:05:00.000","kwh":0.0,"location_id":"GR-EMU-S1549614918776400382896359-L","evse_uid":"B","connector_id":"conn1"}'

curl -X PATCH "http://34.136.104.208:8080/OCPI_2/webresources/sessions/gr/001/111111" -H "Content-Type: application/json" -d '{"kwh":3.599,"last_updated":"2024-12-25T15:19:44.481"}'



        {
            "status": "COMPLETED",
            "last_updated": "2024-10-25T15:19:44.481"
        }
//------------------------ new  ------------
        {
            "kwh": 0.5,
            "charging_periods": [{
                    "start_date_time": "2025-01-08T13:09:07.340",
                    "dimensions": [{
                            "type": "ENERGY",
                            "volume": 0.5
                    },
                    {
                            "type": "POWER",
                            "volume": 50.4890304799
                    }]
            }],
            "last_updated": "2025-01-08T13:11:10.372437"
        }

curl -X PATCH "http://34.136.104.208:8080/OCPI_2/webresources/sessions/gr/001/76893310-6991-4500-a7af-9b22302f96ff" -H "Content-Type: application/json" -d '{"kwh": 0.5, "charging_periods": [{"start_date_time": "2025-01-08T13:09:07.340", "dimensions": [{"type": "ENERGY", "volume": 0.5}, {"type": "POWER", "volume": 55.4890304799}]}], "last_updated": "2025-01-08T13:11:10.372437"}'

 */
