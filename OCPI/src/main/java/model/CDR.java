/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class CDR {
    private String id;
    private String created_at;
    private CdrData1 data;


    /*
				"session_id": "bbe37a6e-53c8-4aeb-969a-b4bffd10acda",
				"data": {
					"country_code": "gr",
					"party_id": "emu",
					"start_date_time": "2024-04-17T15:38:28.809425+03:00",
					"end_date_time": "2024-04-17T17:38:28.809425+03:00",
					"session_id": "bbe37a6e-53c8-4aeb-969a-b4bffd10acda",
					"currency": "EUR",
					"auth_method": "COMMAND",
					"total_cost": {
						"excl_vat": "50.00",
						"incl_vat": "50.00"
					},
					"total_energy": "0.000",
					"total_time": 0,
					"last_updated": "2024-04-17T15:38:40.678248+03:00"
				},
				"updated_at": "2024-04-17T12:38:40.716350",
				"id": "2c5b623c-deec-4911-be4f-0d7c2f0b3fb0",
				"created_at": "2024-04-17T12:38:40.716338"
			}    
     */
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
     * @return the created_at
     */
    public String getCreated_at() {
        return created_at;
    }

    /**
     * @param created_at the created_at to set
     */
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * @return the data
     */
    public CdrData1 getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(CdrData1 data) {
        this.data = data;
    }

    //**************************
    /**
     * @return the start_date_time
     */
    public String getStart_date_time() {
        return data.start_date_time;
    }

    /**
     * @return the end_date_time
     */
    public String getEnd_date_time() {
        return data.end_date_time;
    }

    /**
     * @return the session_id
     */
    public String getSession_id() {
        return data.session_id;
    }

    /**
     * @return the total_cost
     */
    public String getTotal_cost() {
        return data.total_cost.excl_vat;
    }

    /**
     * @return the last_updated
     */
    public String getLast_updated() {
        return data.last_updated;
    }

    /**
     * @return the party_id
     */
    public String getParty_id() {
        return data.party_id;
    }

    /**
     * @return the country_code
     */
    public String getCountry_code() {
        return data.country_code;
    }
    
class CdrData1 {

    public String start_date_time;
    public String end_date_time;
    public String session_id;
    public Cost total_cost;
    public String last_updated;
    public String party_id;
    public String country_code;

    class Cost {
        public String excl_vat;
        public String incl_vaτ;
    }
}    
}


