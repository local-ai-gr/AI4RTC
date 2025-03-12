/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package getters;

import java.net.URLEncoder;
import java.util.List;
import nsofiasLib.utils.URLContextReader_object;

/**
 *
 * @author nsofias
 */
public class Commands {

    String BASE_URL = "https://qa.ocpi.evloader.com/ocpi/2.2.1/commands";//46.101.126.196
    String CREDENTIALS_TOKEN_C = "10d4991f0d8643d2466a8915ad4b4a2b531e80df";

    public void get(String from, String to) {
        try {
            String url = BASE_URL + "?date_from=" + from + "&date_to=" + to + "&offset=0&limit=50";
            URLContextReader_object myURLContextReader = new URLContextReader_object();
            myURLContextReader.setAuthorizationKey("Token " + CREDENTIALS_TOKEN_C);
            List<String> res = (List<String>) myURLContextReader.getUrlContext(url);
            System.out.println("res=" + res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        public static void main(String[] args) {
        try{
            String from = URLEncoder.encode("2000-06-29T20:39:09", "utf8");
            String to = URLEncoder.encode("2024-06-29T20:39:09", "utf8");
        new Commands().get(from,to);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


/*


    Send a Start Session Command:

      {
          "location_id": "GR-ABC-Sabcd1234-L",
          "evse_uid": "abcdabcd-abcd-1234-1234-abcdabcd1234",
          "response_url": "https://yourresponseurl.com“
      }

    Send a Stop Session Command:

    {
        "session_id": "abcdabcd-abcd-1234-1234-abcdabcd1234",
        "response_url": "https://yourresponseurl.com“
    }

    Send a Reserve Now Command:

    {
        "location_id": "GR-ABC-Sabcd1234-L",
        "evse_uid": "abcdabcd-abcd-1234-1234-abcdabcd1234",
        "expiry_date": "2024-11-07",
        "reservation_id": "abcd1234",
        "response_url": "https://yourresponseurl.com“
    }

    Send a Cancel Reservation Command:

    {
        "reservation_id": "abcd1234",
        "response_url": "https://yourresponseurl.com“
    }

*/