/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package getters;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import model.CDR;

/**
 *
 * @author nsofias
 */
public class CdrsGetter  extends Getter{

    public Map<String,CDR> get(String from, String to) throws Exception {
        String url = BASE_URL + "cdrs" + "?date_from=" + from + "&date_to=" + to + "&offset=0";
        String res =  myURLContextReader.getUrlContext(url).get(0);
        System.out.println("result=" + res);
        CDRsWrapper myCDRs = new Gson().fromJson(res, CDRsWrapper.class);
        return Arrays.asList(myCDRs.data).stream().collect(Collectors.toMap((CDR v)->v.getId(), (CDR v)->v));       
    }

    public Map<String,CDR> get() throws Exception {
        String url = BASE_URL + "cdrs" + "?&offset=0";
        String res =  myURLContextReader.getUrlContext(url).get(0);
        System.out.println("result=" + res);
        CDRsWrapper myCDRs = new Gson().fromJson(res, CDRsWrapper.class);
        return Arrays.asList(myCDRs.data).stream().collect(Collectors.toMap((CDR v)->v.getId(), (CDR v)->v));  
    }
}

class CDRsWrapper {

    public CDR[] data;
    public String status_code;
    public String status_message;
    public String timestamp;
}
/*
[
	{
		"data": [
			{
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
		],
		"status_code": 1000,
		"status_message": "Generic success code",
		"timestamp": "2024-06-20T19:48:30.984Z"
	}
]
*/