/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import nsofiasLib.databases.Mongo;
import nsofiasLib.time.TimeStamp1;
import nsofiasLib.utils.Counters;

public class Location {

    private String id;
    private String name;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private GeoLocation coordinates;
    private List<EVSE> evses = new ArrayList<>();
    private String last_updated;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        return Objects.equals(this.id, other.id);
    }

    public String infoSummary() {
        Map<String, Long> statuses = getEvses().stream()
                .map((EVSE ev) -> ev.getStatus())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        String power = getEvses().stream().flatMap(ev -> ev.getConnectors().stream()).map(con -> String.valueOf(con.getMax_electric_power()) + "W").collect(Collectors.joining(", "));
        return power + " " + statuses;
    }

    public String infoSummaryHTML() {
        StringBuilder desc = new StringBuilder("<table>");
        desc.append("<tr><td><b>Name</b></td><td>").append(this.getName()).append("</td></tr>");
        desc.append("<tr><td><b>Address</b></td><td>").append(this.getAddress()).append("</td></tr>");

        getEvses().stream().forEach(ev -> {
            ev.getConnectors().forEach(con -> {
                desc.append("<tr><td><b>Connector</b></td><td>").append(con.toString()).append("</td></tr>");
            });
        });
        desc.append("</table>");
        // Add a link (URL encoding to avoid special characters causing errors)
        /*
    if (false)
    try {
        String encodedLocation = URLEncoder.encode("dashboard.jsp?location=GR-EMU-S5721861176845717934021946-L", "UTF-8");
        desc.append("<a href='").append(encodedLocation)
            .append("' target='_blank'>details</a>");
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace(); // Handle the exception properly
    }        */
        return desc.toString();
    }



    public double getKWH(Mongo myMongo, TimeStamp1 periodStartT, TimeStamp1 periodStopT) {
        Counters myCounters = new Counters();
        String period_start = periodStartT.getNowUnformated_elegant();
        String period_stop = periodStopT.getNowUnformated_elegant();
        Collection<Session> sessions = myMongo.find("sessions",
                Filters.and(Filters.eq("location_id", this.getId()),
                        Filters.exists("end_date_time", true),
                        Filters.eq("location_id", this.getId()),
                        Filters.or(Filters.and(Filters.lte("start_date_time", period_start), Filters.gte("end_date_time", period_start)), // ss{ss }
                                Filters.and(Filters.lte("start_date_time", period_stop), Filters.gte("end_date_time", period_stop)),//         { ss}ss
                                Filters.and(Filters.gte("start_date_time", period_start), Filters.lte("end_date_time", period_stop)) //        { ss }
                        )), true,
                Session.class);
        sessions.forEach(s -> {
            TimeStamp1 session_start_T;
            try {
                session_start_T = TimeStamp1.fromUnformated_elegant(s.getStart_date_time());
                TimeStamp1 session_stop_T = TimeStamp1.fromUnformated_elegant(s.getEnd_date_time());
                TimeStamp1 uStart = periodStartT.isAfter(session_start_T) ? periodStartT : session_start_T;
                TimeStamp1 uStop = periodStopT.isBefore(session_stop_T) ? periodStopT : session_stop_T;
                myCounters.updateCounters("-", s.getKwh() * uStop.hoursDiff(uStart) / session_stop_T.hoursDiff(session_start_T));
            } catch (Exception ex) {
                Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return myCounters.getTotalValue();
    }

    


    public Map<String, Double> getKWHPerHour(Mongo myMongo, TimeStamp1 periodStartT, TimeStamp1 periodStopT) {
        Counters myCounters = new Counters();
        for (TimeStamp1 snapshot = periodStartT; snapshot.isBefore(periodStopT); snapshot.addHours(1)) {
            TimeStamp1 period_stop = new TimeStamp1(snapshot);
            period_stop.addHours(1);
            double res = getKWH(myMongo, snapshot, period_stop);
            if (res > 0) {
                myCounters.updateCounters(snapshot.getNowUnformated_elegant(), res);
            }
        }
        return myCounters.getValuesMap();
    }
    
 
//=========================================================

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the myCoordinates
     */
    public GeoLocation getCoordinates() {
        return coordinates;
    }

    /**
     * @param coordinates the myCoordinates to set
     */
    public void setCoordinates(GeoLocation coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * @return the myEvses
     */
    public List<EVSE> getEvses() {
        return evses;
    }

    /**
     * @param evses the myEvses to set
     */
    public void setEvses(List<EVSE> evses) {
        this.evses = evses;
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
