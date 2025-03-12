/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Session {

    private String id;
    private String start_date_time;
    private String end_date_time;
    private double kwh;
    private String location_id;
    private String evse_uid;
    private String status;
    private String connector_id;
    private String last_updated;
    private String country_code;
    private String party_id;
    private List<ChargingPeriod> charging_periods;
    //private Location location;

    public Location getlocationData(Collection<Location> locations) {
        return locations.stream().filter(l -> l.getId().equals(location_id)).findAny().orElse(null);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.id);
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
        final Session other = (Session) obj;
        return Objects.equals(this.id, other.id);
    }

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
     * @param locations
     * @return the location
     */
    public Location getLocation( Collection<Location> locations) {
        return locations.stream().filter(l->l.getId().equals(this.getLocation_id())).findAny().orElse(null);
    }


    /**
     * @return the start_date_time
     */
    public String getStart_date_time() {
        return start_date_time;
    }

    /**
     * @param start_date_time the start_date_time to set
     */
    public void setStart_date_time(String start_date_time) {
        this.start_date_time = start_date_time;
    }

    /**
     * @return the end_date_time
     */
    public String getEnd_date_time() {
        return end_date_time;
    }

    /**
     * @param end_date_time the end_date_time to set
     */
    public void setEnd_date_time(String end_date_time) {
        this.end_date_time = end_date_time;
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
     * @return the location_id
     */
    public String getLocation_id() {
        return location_id;
    }

    /**
     * @param location_id the location_id to set
     */
    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    /**
     * @return the evse_uid
     */
    public String getEvse_uid() {
        return evse_uid;
    }

    /**
     * @param evse_uid the evse_uid to set
     */
    public void setEvse_uid(String evse_uid) {
        this.evse_uid = evse_uid;
    }

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
     * @return the connector_id
     */
    public String getConnector_id() {
        return connector_id;
    }

    /**
     * @param connector_id the connector_id to set
     */
    public void setConnector_id(String connector_id) {
        this.connector_id = connector_id;
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

    /**
     * @return the country_code
     */
    public String getCountry_code() {
        return country_code;
    }

    /**
     * @param country_code the country_code to set
     */
    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    /**
     * @return the party_id
     */
    public String getParty_id() {
        return party_id;
    }

    /**
     * @param party_id the party_id to set
     */
    public void setParty_id(String party_id) {
        this.party_id = party_id;
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

}

/*
			{
				"id": "758f5d09-a298-4b20-b4bd-b27494ea5343",
				"country_code": "GR",
				"party_id": "EMU",
				"start_date_time": "2024-04-23T16:00:41.426635",
				"end_date_time": null,
				"kwh": 0.0,
				"auth_method": "COMMAND",
				"authorization_reference": null,
				"location_id": "GR-EMU-S1549614918776400382896359-L",
				"evse_uid": "a3334786-6d0e-443b-8cee-e569ee9145a7",
				"connector_id": "1",
				"meter_id": null,
				"currency": "EUR",
				"charging_periods": [],
				"total_cost": null,
				"status": "PENDING",
				"last_updated": "2024-04-23T13:00:44.225600"
			}
 */
