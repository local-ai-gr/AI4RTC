package main;

import getters.SessionsGetterDaemon;
import getters.LocationsGetterDaemon;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nsofiasLib.databases.Mongo;
import nsofiasLib.others.Parameters;
import nsofiasLib.others.SimpleDaemon;

/**
 * Web application lifecycle listener.
 *
 * @author gsofi
 */
public class MyServletListener implements ServletContextListener {

    List<String> events = new ArrayList<>();
    private String APPLICATIONS_PATH = System.getenv("APPLICATIONS_PATH");
    Mongo myMongo;
    Map<String, SimpleDaemon> daemons = new HashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (APPLICATIONS_PATH == null) {
            APPLICATIONS_PATH = "C:\\myFiles\\data\\";
        }
        //----------- mongo ----
        String mongoURL = Parameters.loadStringValue(APPLICATIONS_PATH + "/OCPI/conf/parameters.properties", "mongoURL", "UTF8", "mongodb://nsofias:#1Vasilokori@mongo:27017");
        myMongo = new Mongo(mongoURL, "OCPI");
        //--- init databases -----
        if (!myMongo.getMyCollectionNames().contains("sessions")) {
            myMongo.createCollection("sessions");
            myMongo.addSimpleIndexToCollection("sessions", "start_date_time");
            myMongo.addSimpleIndexToCollection("sessions", "end_date_time");
            myMongo.addSimpleIndexToCollection("sessions", "location_id");
            myMongo.addUniqueIndexToCollection("sessions", "id");
            myMongo.addDateIndexToCollection("sessions", 180L);
        }
        if (!myMongo.getMyCollectionNames().contains("locations")) {
            myMongo.createCollection("locations");
            myMongo.addUniqueIndexToCollection("locations", "id");
        }
        if (!myMongo.getMyCollectionNames().contains("cdrs")) {
            myMongo.createCollection("cdrs");
            myMongo.addUniqueIndexToCollection("cdrs", "id");
            myMongo.addDateIndexToCollection("cdrs", 180L);
        }
        ServletContext myContext = sce.getServletContext();
        myContext.setAttribute("daemons", daemons);
        myContext.setAttribute("myMongo", myMongo);
        myContext.setAttribute("events", events);
        //
        //registerDaemon(new LocationsGetterDaemon(myMongo, 60000, 5), "LocationsGetterDaemon");//every 5 minutes
        //registerDaemon(new SessionsGetterDaemon(myMongo, 60000, 5), "SessionsGetterDaemon");//every 5 minutes
        //registerDaemon(new CdrsGetterDaemon(myMongo, 60000, 5), "CdrsGetterDaemon");//every 5 minutes
    }

    private void registerDaemon(SimpleDaemon myDaemon, String myDaemonName) {
        try {
            myDaemon.setRunning(true);
            myDaemon.setDaemonName(myDaemonName);
            new Thread(myDaemon).start();
            daemons.put(myDaemon.getDaemonName(), myDaemon);
            System.out.println("OCPI:" + myDaemon.getDaemonName() + " started");
        } catch (Exception ex) {
            Logger.getLogger(MyServletListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        myMongo.disconnect();
        daemons.values().forEach(d -> d.setRunning(false));
    }

}
