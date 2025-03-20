/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package getters;

import com.google.gson.Gson;
import jakarta.xml.bind.DatatypeConverter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import model.CDR;
import model.Location;
import model.Session;
import nsofiasLib.databases.Mongo;
import nsofiasLib.time.TimeStamp1;
import nsofiasLib.utils.URLContextReader_object;

/**
 *
 * @author gsofi
 */
public class Getter {

    public String BASE_URL = "https://qa.ocpi.evloader.com/ocpi/2.2.1/";//46.101.126.196
    public String CREDENTIALS_TOKEN_C = "10d4991f0d8643d2466a8915ad4b4a2b531e80df";

    //public String BASE_URL = "https://ocpi.evloader.com/ocpi/2.2.1/";//production
    //public String CREDENTIALS_TOKEN_C = "10d4991f0d8643d2466a8915ad4b4a2b531e80df";
    public URLContextReader_object myURLContextReader;

    public Getter() {
        CREDENTIALS_TOKEN_C = DatatypeConverter.printBase64Binary(CREDENTIALS_TOKEN_C.getBytes());
        myURLContextReader = new URLContextReader_object();
        myURLContextReader.setAuthorizationKey("Token " + CREDENTIALS_TOKEN_C);
        //System.setProperty("https.proxyHost", "oteproxy.ote.gr");
        //System.setProperty("https.proxyHost", "proxy.ote.gr");
        //System.setProperty("https.proxyPort", "8080");        
        //System.out.println("CREDENTIALS_TOKEN_C=" + CREDENTIALS_TOKEN_C);
    }

    public static void main(String[] args) {
        if (false)  
        try {
            String from = "2024-09-13T14:56:42.798Z";// URLEncoder.encode("2000-06-29T20:39:09", "utf8");
            //String to = "2024-12-17T20:20:04.198z";//URLEncoder.encode("2024-06-29T20:39:09", "utf8");
            Map<String, Session> mySessions = new SessionsGetter().get(from,0,50);
            System.out.println("locs:" + mySessions.values());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        System.out.println("\n\n--- Locations ---");
        if (false)  
        try {
            Map<String, Location> myLocations = new LocationsGetter().get();
            myLocations.values().forEach(l -> {
                //System.out.println(l.getId() + " " + l.getAddress() + " lat=" + l.getCoordinates().getLatitude() + " lon=" + l.getCoordinates().getLongitude() + " last updated:" + l.getLast_updated());
                //l.getEvses().forEach(ev -> System.out.println("    " + ev.getStatus()));

                System.out.println("location:" + new Gson().toJson(l));
            });

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        //---------------------------
        if (false)  {
        System.out.println("\n\n--- Sessions ---");
        Map<String, Session> allSessions = new HashMap<>();
        int found = 0;
        int offset = 0;
        int limit = 50;
        do {
            try {
                Map<String, Session> mySessions = new SessionsGetter().get(offset, limit);
                found = mySessions.size();
                allSessions.putAll(mySessions);
                offset += found;
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        } while (found > 0);
        allSessions.values().stream().sorted(Comparator.comparing(s->s.getStart_date_time())).forEach(l -> {
            System.out.println(l.getId() + " started:" + l.getStart_date_time() + " endded:" + l.getEnd_date_time() + " Loc:" + l.getLocation_id() + " last updated:" + l.getLast_updated());
        });}
        //---------------------------------
        System.out.println("\n\n--- Cdrs ---");
        if (false)  
        try {
            Map<String, CDR> myCdrs = new CdrsGetter().get();
            myCdrs.values().forEach(l -> {
                System.out.println(l.getId() + " started:" + l.getStart_date_time() + " endded:" + l.getEnd_date_time() + " last updated:" + l.getLast_updated());
            });
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        //----------
        if (true)            
        try {
            String t1 = "20240519T000000";
            String t2 = "20241030T000000";
            System.out.println("\n\n--- erlaqngs ---");
            Mongo myMongo = new Mongo("mongodb://localhost:27017", "OCPI");
            Location myLocation = new LocationsGetter().get("GR-EMU-S6792111181543869683893493-L");
           // double erlangs = myLocation.getErlangs(myMongo, new TimeStamp1(t1), new TimeStamp1(t2));
            //System.out.println("erlangs=" + erlangs);
            Map<String, Double> erlangsPerHour = myLocation.getErlangsPerHour(myMongo, new TimeStamp1(t1), new TimeStamp1(t2));
            erlangsPerHour.forEach((k, v) -> System.out.println("erlangs=" + k + " " + v));
            //
            double kwh = myLocation.getKWH(myMongo, new TimeStamp1(t1), new TimeStamp1(t2));
            System.out.println("kwh=" + kwh);
            Map<String, Double> kwhPerHour = myLocation.getKWHPerHour(myMongo, new TimeStamp1(t1), new TimeStamp1(t2));
            kwhPerHour.forEach((k, v) -> System.out.println("kwh=" + k + " " + v));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

    }
}
