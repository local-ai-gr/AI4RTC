/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package getters;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import model.Location;

/**
 *
 * @author gsofi
 */
public class LocationsGetter extends Getter {

    public Map<String, Location> get(String from, String to) throws Exception {
        String url = BASE_URL + "locations" + "?date_from=" + from + "&date_to=" + to + "&offset=0";
        String res = myURLContextReader.getUrlContext(url).get(0);
        System.out.println("result=" + res);
        LocationsWrapper myLocations = new Gson().fromJson(res, LocationsWrapper.class);
        return Arrays.asList(myLocations.data).stream().collect(Collectors.toMap((Location v) -> v.getId(), (Location v) -> v));
    }

    public Map<String, Location> get() throws Exception {
        String url = BASE_URL + "locations" + "?&offset=0";
        String res = myURLContextReader.getUrlContext(url).get(0);
        System.out.println("result=" + res);
        LocationsWrapper myLocations = new Gson().fromJson(res, LocationsWrapper.class);
        return Arrays.asList(myLocations.data).stream().collect(Collectors.toMap((Location v) -> v.getId(), (Location v) -> v));
    }

    public Location get(String id) throws Exception {
        String url = BASE_URL + "locations" + "?&offset=0";
        String res = myURLContextReader.getUrlContext(url).get(0);
        System.out.println("result=" + res);
        LocationsWrapper myLocations = new Gson().fromJson(res, LocationsWrapper.class);
        return Arrays.asList(myLocations.data).stream().filter(l -> l.getId().equals(id)).findAny().orElse(null);
    }
}

class LocationsWrapper {

    public Location[] data;
    public String status_code;
    public String status_message;
    public String timestamp;
}

/*
[
	{
		"data": [
			{
				"id": "GR-EMU-S5721861176845717934021946-L",
				"publish": true,
				"country_code": "GR",
				"party_id": "EMU",
				"address": "Grammou 52, Marousi",
				"city": "Athens",
				"country": "GRC",
				"coordinates": {
					"longitude": "23.8037598",
					"latitude": "38.048732"
				},
				"time_zone": "Europe/Athens",
				"evses": [
					{
						"uid": "1e392166-4751-4289-a333-f6a5f31af791",
						"evse_id": "GR*EMU*E00105",
						"status": "CHARGING",
						"connectors": [
							{
								"id": "377cff52-36b9-4c59-a4b2-49a806ec18f2",
								"standard": "IEC_62196_T2",
								"format": "SOCKET",
								"power_type": "AC_3_PHASE",
								"max_voltage": 230,
								"max_amperage": 32,
								"tariff_ids": [
									"7fca5b21-a1b4-4fff-82ff-5060eb92c540"
								],
								"last_updated": "2024-06-04T03:00:02",
								"max_electric_power": 22000
							}
						],
						"last_updated": "2024-06-04T03:00:02",
						"capabilities": [
							"START_SESSION_CONNECTOR_REQUIRED",
							"RESERVABLE"
						]
					}
				],
				"last_updated": "2024-06-04T03:00:02",
				"name": "AC Station",
				"postal_code": "15124",
				"state": "Attiki",
				"images": [
					{
						"url": "https://qa.evloader.com//photos/ev-charging.jpg",
						"category": "LOCATION",
						"type": "jpeg"
					}
				],
				"opening_times": {
					"twentyfourseven": false,
					"regular_hours": [
						{
							"weekday": 1,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 2,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 3,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 4,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 5,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 6,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 7,
							"period_begin": "00:00",
							"period_end": "23:59"
						}
					]
				},
				"operator": {
					"name": "EV Loader",
					"website": "https://qa.evloader.com/"
				}
			},
			{
				"id": "GR-EMU-S1549614918776400382896359-L",
				"publish": true,
				"country_code": "GR",
				"party_id": "EMU",
				"address": "Grammou 52, Marousi",
				"city": "Athens",
				"country": "GRC",
				"coordinates": {
					"longitude": "23.8037598",
					"latitude": "38.048732"
				},
				"time_zone": "Europe/Athens",
				"evses": [
					{
						"uid": "a3334786-6d0e-443b-8cee-e569ee9145a7",
						"evse_id": "GR*EMU*E00048",
						"status": "AVAILABLE",
						"connectors": [
							{
								"id": "8388c4dd-a39a-47e5-b658-1d60c7c4cb69",
								"standard": "IEC_62196_T2_COMBO",
								"format": "CABLE",
								"power_type": "DC",
								"max_voltage": 600,
								"max_amperage": 200,
								"tariff_ids": [
									"182bcafb-8e8f-41b1-b3b7-17893a9710a8"
								],
								"last_updated": "2024-06-04T03:00:02",
								"max_electric_power": 120000
							}
						],
						"last_updated": "2024-06-04T03:00:02",
						"capabilities": [
							"START_SESSION_CONNECTOR_REQUIRED",
							"RESERVABLE"
						]
					},
					{
						"uid": "85f2a46b-a18b-4b82-977b-c39f5b557896",
						"evse_id": "GR*EMU*E00192",
						"status": "AVAILABLE",
						"connectors": [
							{
								"id": "163ac472-13e9-4301-a08c-b6f8c625a664",
								"standard": "IEC_62196_T2_COMBO",
								"format": "CABLE",
								"power_type": "DC",
								"max_voltage": 600,
								"max_amperage": 200,
								"tariff_ids": [
									"83b0b747-f065-432c-8c01-3ac1d507c5ce"
								],
								"last_updated": "2024-06-04T03:00:05",
								"max_electric_power": 120000
							}
						],
						"last_updated": "2024-06-04T03:00:05",
						"capabilities": [
							"START_SESSION_CONNECTOR_REQUIRED",
							"RESERVABLE"
						]
					}
				],
				"last_updated": "2024-06-04T03:00:05",
				"name": "DC Station",
				"postal_code": "15124",
				"state": "Attiki",
				"images": [
					{
						"url": "https://qa.evloader.com//photos/ev-charging.jpg",
						"category": "LOCATION",
						"type": "jpeg"
					}
				],
				"opening_times": {
					"twentyfourseven": false,
					"regular_hours": [
						{
							"weekday": 1,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 2,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 3,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 4,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 5,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 6,
							"period_begin": "00:00",
							"period_end": "23:59"
						},
						{
							"weekday": 7,
							"period_begin": "00:00",
							"period_end": "23:59"
						}
					]
				},
				"operator": {
					"name": "EV Loader",
					"website": "https://qa.evloader.com/"
				}
			}
		],
		"status_code": 1000,
		"status_message": "Generic success code",
		"timestamp": "2024-06-20T19:41:50.170Z"
	}
]
 */
