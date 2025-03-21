/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package dataServlets;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import main.GanntObj;
import model.Location;
import model.Session;
import nsofiasLib.databases.Mongo;
import nsofiasLib.utils.Counters;
import org.bson.Document;

/**
 *
 * @author nsofias
 */
public class RealTimeKW extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        ServletContext myContext = request.getServletContext();
        try (PrintWriter out = response.getWriter()) {
            Mongo myMongo = (Mongo) myContext.getAttribute("myMongo");
            Collection<Location> locations = myMongo.find("locations", new Document(), false, Location.class);
            Collection<Session> activeSessions = myMongo.find("sessions", Filters.eq("status", "ACTIVE"), true, Session.class);
            Counters KW = new Counters();
            activeSessions.stream().forEach(s -> {
                Location myLocation = s.getLocation(locations);
                KW.updateCounters(myLocation.getName(), s.getKwh());
            });
            List<StatsObj> l = new ArrayList<>();
            KW.getValuesMap().forEach((K, V) -> {
                StatsObj myStatsObj = new StatsObj();
                myStatsObj.label = K;
                myStatsObj.val = V;
                if (myStatsObj.label != null) {
                    l.add(myStatsObj);
                }
            });
            List<StatsObj> sorted = l.stream().sorted(Comparator.comparing((StatsObj v) -> v.val).reversed()).collect(Collectors.toList());
            List<StatsObj> sorted1 = sorted.stream().sorted(Comparator.comparing((StatsObj v) -> v.val)).collect(Collectors.toList());
            out.println(new Gson().toJson(sorted1));
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

class StatsObj {

    String label;
    double val;
}
