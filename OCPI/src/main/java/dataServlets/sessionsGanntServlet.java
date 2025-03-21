package dataServlets;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.GanntObj;
import model.Location;
import model.Session;
import nsofiasLib.databases.Mongo;
import nsofiasLib.time.TimeStamp1;
import org.bson.Document;
import org.bson.conversions.Bson;
import simulation.EVChargingStation;
import static simulation.EVChargingStation.FORMATER;
import simulation.Event;
import simulation.EventType;

/**
 *
 * @author gsofi
 */
public class sessionsGanntServlet extends HttpServlet {

    
    private static final long serialVersionUID = 1L;
    int id = 0;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DateTimeFormatter formater = EVChargingStation.FORMATER;
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");

        //------- location & timestamps--------
        String location = request.getParameter("location") != null && !request.getParameter("location").isEmpty() ? request.getParameter("location") : "";
        String timeFrom = request.getParameter("timeFrom");

        if (timeFrom == null) {
            TimeStamp1 timeFromT = new TimeStamp1();
            timeFromT.addDays(-7);
            timeFrom = timeFromT.getNowUnformated_elegant()+".000";
        } else {
            timeFrom = timeFrom.replaceAll("/", "-").replaceAll(" ", "T") + ".000";
        }
        String timeTo = request.getParameter("timeTo");
        if (timeTo == null) {
            TimeStamp1 timeToT = new TimeStamp1();
            timeTo = timeToT.getNowUnformated_elegant()+".000";
        } else {
            timeTo = timeTo.replaceAll("/", "-").replaceAll(" ", "T") + ".000";
        }
        final LocalDateTime T1 = LocalDateTime.parse(timeFrom, formater);
        final LocalDateTime T2 = LocalDateTime.parse(timeTo, formater);
        System.out.println("T1:" + T1);
        System.out.println("T2:" + T2);
        //--------------------------------         
        try (PrintWriter out = response.getWriter()) {
            ServletContext myContext = request.getServletContext();
            Mongo myMongo = (Mongo) myContext.getAttribute("myMongo");
            Collection<Location> locations = myMongo.find("locations", new Document(), false, Location.class);
            //Comparator<Session> myComparator = Comparator.comparing((Session v) -> v.getEnd_date_time()).reversed();
            //------filtering ---------------    
            Bson locationFilter = (location != null && !location.isEmpty()) ? Filters.eq("location_id", location) : new Document();
            Bson timeFilter = Filters.and(Filters.gte("mydate", T1),
                    Filters.lte("mydate", T2));
            Bson filter = Filters.and(locationFilter, timeFilter);
            Collection<Session> sessions = myMongo.find("sessions", filter, true, Session.class);
            Collection<Event> events = myMongo.find("events", filter, true, Event.class);
            //-------------------------------
            String type = request.getParameter("type");
            //
            if (type == null || type.isEmpty() || type.equals("gannt")) {
                List<GanntObj> ganntList = new ArrayList<>();
                sessions.stream().
                        filter(s -> s.getEnd_date_time() != null && s.getStart_date_time() != null)
                        .forEach(s -> {
                            s.getCharging_periods().stream().
                                    forEach(period -> {
                                        try {
                                            Location myLocation = locations.stream().filter(l -> l.getId().equals(s.getLocation_id())).findAny().orElse(null);
                                            if (myLocation != null) {
                                                String power = String.valueOf(period.getChargingPeriodPower());
                                                //List<String> ewses = myLocation.getEvses().stream().map(ev -> ev.getUid()).collect(toList());
                                                //int ewseIndex = ewses.indexOf(s.getEvse_uid()) + 1;
                                                String connector = s.getConnector_id();
                                                String period_start_date_time = period.getStart_date_time();
                                                String period_end_date_time = period.getEnd_date_time();
                                                String groupName = request.getParameter("location") == null ? myLocation.getName() + " " + connector : connector;
                                                ganntList.add(new GanntObj(id++, power, period_start_date_time, period_end_date_time, groupName, null, 0.0));
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    });
                        });

                events.stream().
                        filter(e -> e.getEventType() == EventType.DOWNGRADE_EVENT)
                        .forEach(e -> {
                            try {
                                Location myLocation = locations.stream().filter(l -> l.getId().equals(e.getLocation_id())).findAny().orElse(null);
                                LocalDateTime ldt = LocalDateTime.parse(e.getEventTime(), FORMATER);
                                LocalDateTime ldt1 = ldt.plusSeconds(1);
                                String eventEnd = ldt1.format(FORMATER);
                                String connector = e.getConnectorId();
                                String groupName = request.getParameter("location") == null ? myLocation.getName() + " " + connector : connector;
                                ganntList.add(new GanntObj(id++, "", e.getEventTime(), eventEnd, "Downgrades", "background-color: red; border-color: red;", 0.0));
                                ganntList.add(new GanntObj(id++, "", e.getEventTime(), eventEnd, groupName, "background-color: red; border-color: red;", 0.0));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });

                out.println(new Gson().toJson(ganntList));
                //----------------------------------
            } else if (type.equals("erlangs")) {
                List<GanntObj> ganntList = new ArrayList<>();
                System.out.println("\n\n--- erlaqngs ---");
                Bson myLocationFilter = (location != null && !location.isEmpty()) ? Filters.eq("id", location) : new Document();
                List<Location> myLocations = myMongo.find("locations", myLocationFilter, false, Location.class);
                myLocations.forEach(myLocation -> {
                    try {
                        //double erlangs = myLocation.getErlangs(myMongo, new TimeStamp1(t1), new TimeStamp1(t2));
                        System.out.println("erlangs for " + myLocation.getName());
                        Map<String, Double> erlangsPerHour = myLocation.getErlangsPerHour(myMongo, new TimeStamp1(T1), new TimeStamp1(T2));
                        erlangsPerHour.forEach((k, v) -> {
                            try {
                                TimeStamp1 endTime = TimeStamp1.fromUnformated_elegant(k);
                                endTime.addHours(1);
                                String _endTime = endTime.getNowUnformated_elegant();
                                //System.out.println(_endTime);
                                ganntList.add(new GanntObj(1, "", k, _endTime, myLocation.getName(), null, v));
                            } catch (Exception ex) {
                                Logger.getLogger(sessionsGanntServlet.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    } catch (Exception ex) {
                        Logger.getLogger(sessionsGanntServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                long id = 1;
                for (GanntObj ho : ganntList) {
                    ho.setId(id++);
                }
                out.println(new Gson().toJson(ganntList));
                //----------------------------------
            } else if (type.equals("kwh")) {
                List<GanntObj> ganntList = new ArrayList<>();
                Bson myLocationFilter = (location != null && !location.isEmpty()) ? Filters.eq("id", location) : new Document();
                List<Location> myLocations = myMongo.find("locations", myLocationFilter, false, Location.class);
                myLocations.forEach(myLocation -> {
                    try {
                        double kwh = myLocation.getKWH(myMongo, new TimeStamp1(T1), new TimeStamp1(T2));
                        System.out.println("kwh=" + kwh);
                        Map<String, Double> kwhPerHour = myLocation.getKWHPerHour(myMongo, new TimeStamp1(T1), new TimeStamp1(T2));
                        kwhPerHour.forEach((k, v) -> {
                            try {
                                TimeStamp1 endTime = TimeStamp1.fromUnformated_elegant(k);
                                endTime.addHours(1);
                                String _endTime = endTime.getNowUnformated_elegant();
                                //System.out.println(_endTime);                        
                                ganntList.add(new GanntObj(1, "", k, _endTime, myLocation.getName(), null, v));
                            } catch (Exception ex) {
                                Logger.getLogger(sessionsGanntServlet.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    } catch (Exception ex) {
                        Logger.getLogger(sessionsGanntServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                long id = 1;
                for (GanntObj ho : ganntList) {
                    ho.setId(id++);
                }
                out.println(new Gson().toJson(ganntList));
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
