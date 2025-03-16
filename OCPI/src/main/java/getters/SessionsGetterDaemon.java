package getters;

import com.mongodb.client.model.Filters;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import model.Location;
import model.Session;
import nsofiasLib.databases.Mongo;
import nsofiasLib.others.SimpleDaemon;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author gsofi
 */
public class SessionsGetterDaemon extends SimpleDaemon {

    Mongo myMongo;
    final Map<String, Session> data = new ConcurrentHashMap<>();
    Collection<Location> locations;
    int offset = 0;

    public SessionsGetterDaemon(Mongo myMongo, int sleep, int step) {
        super(sleep, step);
        this.myMongo = myMongo;
    }

    @Override
    public void processData() {
        try {
            locations = myMongo.find("locations", new Document(),false, Location.class);
            //--

            //---------------------------
            System.out.println("\n\n--- Sessions ---");

            int found = 0;            
            int limit = 50;
            do {
                try {
                    Map<String, Session> mySessions = new SessionsGetter().get(offset, limit);
                    found = mySessions.size();
                    data.putAll(mySessions);
                    offset += found;
                    System.out.println("offset=" + offset);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            } while (found > 0);

            //---
            data.values().stream().forEach(newObject -> {
                try {
                    //Location myLocation = newObject.getlocationData(locations);
                    //newObject.setLocation(myLocation);
                    //
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    LocalDateTime aggrDate = LocalDateTime.parse(newObject.getStart_date_time(), formatter);
                    //
                    String id = newObject.getId();
                    Bson filter = Filters.eq("id", id);
                    Session oldObject = myMongo.findOne("sessions", filter, Session.class);
                    if (oldObject != null) {
                        String last_updated = oldObject.getLast_updated();
                        if (!newObject.getLast_updated().equals(last_updated)) {
                            // myMongo.replaceOne("sessions", filter, newObject);
                            myMongo.delete("sessions", filter);
                            myMongo.add("sessions", newObject, aggrDate);
                        }
                    } else {//"2024-08-08T14:21:59.035"
                        myMongo.add("sessions", newObject, aggrDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            });
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Map<String, Session> getData() {
        return data;
    }
}
