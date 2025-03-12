/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package getters;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import model.Session;

/**
 *
 * @author nsofias
 */
public class SessionsGetter extends Getter {

    public Map<String,Session> get(String from,long offset,long limit) throws Exception {
        String url = BASE_URL + "sessions" + "?date_from=" + from+"&offset="+offset+"&limit="+limit;
        String res = myURLContextReader.getUrlContext(url).get(0);
        System.out.println("url=" + url);
        System.out.println("result=" + res);
        SessionsWrapper mySessions = new Gson().fromJson(res, SessionsWrapper.class);
        return Arrays.asList(mySessions.data).stream().collect(Collectors.toMap((Session v)->v.getId(), (Session v)->v));       
    }

    public Map<String,Session> get(long offset,long limit) throws Exception {
        String url = BASE_URL + "sessions" + "?offset="+offset+"&limit="+limit;
        String res = myURLContextReader.getUrlContext(url).get(0);
        System.out.println("result=" + res);
        System.out.println("url=" + url);
        SessionsWrapper mySessions = new Gson().fromJson(res, SessionsWrapper.class);
        return Arrays.asList(mySessions.data).stream().collect(Collectors.toMap((Session v)->v.getId(), (Session v)->v));  
    }
}


class SessionsWrapper {

    public Session[] data;
    public String status_code;
    public String status_message;
    public String timestamp;
}
//

/*
[
	{
		"data": [
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
			},
			{
				"id": "bbe37a6e-53c8-4aeb-969a-b4bffd10acda",
				"country_code": "gr",
				"party_id": "emu",
				"start_date_time": "2024-04-17T15:38:39.877000",
				"end_date_time": null,
				"kwh": 0.0,
				"auth_method": "COMMAND",
				"authorization_reference": null,
				"location_id": "gr-emu-s1549614918776400382896359-l",
				"evse_uid": "a3334786-6d0e-443b-8cee-e569ee9145a7",
				"connector_id": "",
				"meter_id": null,
				"currency": "EUR",
				"charging_periods": [],
				"total_cost": null,
				"status": "COMPLETED",
				"last_updated": "2024-04-17T12:44:50.468169"
			},
			{
				"id": "6eed06d8-6dce-46d7-a09f-9a372b150d3c",
				"country_code": "GR",
				"party_id": "EMU",
				"start_date_time": "2024-04-17T15:55:33.031622",
				"end_date_time": null,
				"kwh": 0.0,
				"auth_method": "COMMAND",
				"authorization_reference": null,
				"location_id": "GR-EMU-S1549614918776400382896359-L",
				"evse_uid": "a3334786-6d0e-443b-8cee-e569ee9145a7",
				"connector_id": "",
				"meter_id": null,
				"currency": "EUR",
				"charging_periods": [],
				"total_cost": null,
				"status": "PENDING",
				"last_updated": "2024-04-17T12:55:33.618242"
			},
			{
				"id": "8f002cc4-e80d-4cc8-9fc0-1f8715c22cb9",
				"country_code": "GR",
				"party_id": "EMU",
				"start_date_time": "2024-04-19T18:04:51.864368",
				"end_date_time": null,
				"kwh": 0.0,
				"auth_method": "COMMAND",
				"authorization_reference": null,
				"location_id": "GR-EMU-S1549614918776400382896359-L",
				"evse_uid": "85f2a46b-a18b-4b82-977b-c39f5b557896",
				"connector_id": "",
				"meter_id": null,
				"currency": "EUR",
				"charging_periods": [],
				"total_cost": null,
				"status": "PENDING",
				"last_updated": "2024-04-19T15:04:53.588006"
			},
			{
				"id": "954550a6-7f77-4304-924f-975cecaa3285",
				"country_code": "GR",
				"party_id": "EMU",
				"start_date_time": "2024-04-26T17:42:22.955310",
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
				"last_updated": "2024-04-26T14:42:24.674884"
			},
			{
				"id": "19aaf6c3-25aa-43fb-afdc-7ec4e0347aac",
				"country_code": "GR",
				"party_id": "EMU",
				"start_date_time": "2024-04-22T17:07:52.545217",
				"end_date_time": null,
				"kwh": 0.0,
				"auth_method": "COMMAND",
				"authorization_reference": null,
				"location_id": "GR-EMU-S1549614918776400382896359-L",
				"evse_uid": "a3334786-6d0e-443b-8cee-e569ee9145a7",
				"connector_id": "",
				"meter_id": null,
				"currency": "EUR",
				"charging_periods": [],
				"total_cost": null,
				"status": "PENDING",
				"last_updated": "2024-04-22T14:07:54.744460"
			},
			{
				"id": "632d5b4f-5aa2-495b-8d74-7319ed0e9b58",
				"country_code": "gr",
				"party_id": "emu",
				"start_date_time": "2024-05-22T13:18:06.525000",
				"end_date_time": null,
				"kwh": 0.0,
				"auth_method": "COMMAND",
				"authorization_reference": null,
				"location_id": "gr-emu-s5721861176845717934021946-l",
				"evse_uid": "1e392166-4751-4289-a333-f6a5f31af791",
				"connector_id": "1",
				"meter_id": null,
				"currency": "EUR",
				"charging_periods": [],
				"total_cost": null,
				"status": "ACTIVE",
				"last_updated": "2024-05-22T14:40:00.859140"
			},
			{
				"id": "08caee25-6b30-4e71-bcae-532c63ab2634",
				"country_code": "gr",
				"party_id": "emu",
				"start_date_time": "2024-05-23T16:28:06.521000",
				"end_date_time": "2024-05-23T16:35:10.245000",
				"kwh": 0.0,
				"auth_method": "COMMAND",
				"authorization_reference": null,
				"location_id": "gr-emu-s5721861176845717934021946-l",
				"evse_uid": "1e392166-4751-4289-a333-f6a5f31af791",
				"connector_id": "1",
				"meter_id": null,
				"currency": "EUR",
				"charging_periods": [],
				"total_cost": null,
				"status": "COMPLETED",
				"last_updated": "2024-05-23T13:35:15.829771"
			}
		],
		"status_code": 1000,
		"status_message": "Generic success code",
		"timestamp": "2024-06-20T19:47:05.253Z"
	}
]
 */
