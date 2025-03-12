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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import main.GanntObj;
import model.Location;
import model.Session;
import nsofiasLib.databases.Mongo;
import nsofiasLib.time.TimeStamp1;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author gsofi
 */
public class sessionsGanntServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

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
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        String t1 = "20240519T000000";
        String t2 = new TimeStamp1().getNowUnformated().substring(0, 15);
        try (PrintWriter out = response.getWriter()) {
            ServletContext myContext = request.getServletContext();
            Mongo myMongo = (Mongo) myContext.getAttribute("myMongo");
            Collection<Location> locations = myMongo.find("locations", new Document(),false, Location.class);
            //Comparator<Session> myComparator = Comparator.comparing((Session v) -> v.getEnd_date_time()).reversed();
            //------filtering ---------------    
            String location = request.getParameter("location") != null && !request.getParameter("location").isEmpty() ? request.getParameter("location") : "";
            Bson myFilter = (location != null && !location.isEmpty()) ? Filters.eq("location_id", location) : new Document();
            //-----------------------------------------
            Collection<Session> sessions = myMongo.find("sessions", myFilter, false, Session.class);
            //
            String type = request.getParameter("type");
            //-----------------------------------------------------
            if (type == null || type.isEmpty() || type.equals("gannt")) {
                List<GanntObj> ganntList = sessions.stream().
                        filter(s -> s.getEnd_date_time() != null && s.getStart_date_time() != null).
                        map(s -> {
                            try {
                                TimeStamp1.fromUnformated_elegant(s.getStart_date_time());
                                TimeStamp1.fromUnformated_elegant(s.getEnd_date_time());
                                Location myLocation = locations.stream().filter(l->l.getId().equals(s.getLocation_id())).findAny().orElse(null);
                                if (myLocation != null) {
                                    String Kwh = String.valueOf(s.getKwh());
                                    List<String> ewses = myLocation.getEvses().stream().map(ev -> ev.getUid()).collect(toList());
                                    int ewseIndex = ewses.indexOf(s.getEvse_uid()) + 1;
                                    String connector = s.getConnector_id();
                                    return new GanntObj(1, Kwh + " Kwh", s.getStart_date_time(), s.getEnd_date_time(), myLocation.getName() + ":" + ewseIndex + ":" + connector, 0.0);
                                } else {
                                    return null;
                                }
                            } catch (Exception ex) {
                                return null;
                            }
                        }).
                        filter(g -> g != null).
                        collect(Collectors.toList());
                long id = 1;
                for (GanntObj ho : ganntList) {
                    ho.setId(id++);
                }
                out.println(new Gson().toJson(ganntList));
                //----------------------------------
            } else if (type.equals("erlangs")) {
                List<GanntObj> ganntList = new ArrayList<>();
                System.out.println("\n\n--- erlaqngs ---");
                Bson myLocationFilter = (location != null && !location.isEmpty()) ? Filters.eq("id", location) : new Document();
                List<Location> myLocations = myMongo.find("locations", myLocationFilter, false, Location.class);
                myLocations.forEach(myLocation -> {
                    double erlangs = myLocation.getErlangs(myMongo, new TimeStamp1(t1), new TimeStamp1(t2));
                    System.out.println("erlangs=" + erlangs);
                    Map<String, Double> erlangsPerHour = myLocation.getErlangsPerHour(myMongo, new TimeStamp1(t1), new TimeStamp1(t2));
                    erlangsPerHour.forEach((k, v) -> {
                        try {
                            TimeStamp1 endTime = TimeStamp1.fromUnformated_elegant(k);
                            endTime.addHours(1);
                            String _endTime = endTime.getNowUnformated_elegant();
                            //System.out.println(_endTime);
                            ganntList.add(new GanntObj(1, "", k, _endTime, myLocation.getName(), v));
                        } catch (Exception ex) {
                            Logger.getLogger(sessionsGanntServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
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
                    double kwh = myLocation.getKWH(myMongo, new TimeStamp1(t1), new TimeStamp1(t2));
                    System.out.println("kwh=" + kwh);
                    Map<String, Double> kwhPerHour = myLocation.getKWHPerHour(myMongo, new TimeStamp1(t1), new TimeStamp1(t2));
                    kwhPerHour.forEach((k, v) -> {
                        try {
                            TimeStamp1 endTime = TimeStamp1.fromUnformated_elegant(k);
                            endTime.addHours(1);
                            String _endTime = endTime.getNowUnformated_elegant();
                            //System.out.println(_endTime);                        
                            ganntList.add(new GanntObj(1, "", k, _endTime, myLocation.getName(), v));
                        } catch (Exception ex) {
                            Logger.getLogger(sessionsGanntServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
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
