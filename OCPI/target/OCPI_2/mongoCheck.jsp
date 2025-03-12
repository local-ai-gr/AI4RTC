<%-- 
    Document   : locations
    Created on : Jun 30, 2024, 6:02:44 PM
    Author     : nsofias
--%>

<%@page import="jakarta.servlet.jsp.JspWriter"%>
<%@page import="nsofiasLib.databases.Mongo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%

    ServletContext myContext = session.getServletContext();
    // Mongo myMongo = new Mongo("mongodb://nsofias:#1Vasilokori@mongo:27017", "OCPI");    
    Mongo myMongo = (Mongo) myContext.getAttribute("myMongo");
    JspWriter out1 = out;
    if (request.getParameter("remove") != null) {
        String collection = request.getParameter("collection");
        if (collection != null) {
            myMongo.deleteAll(collection);
        } else {
            myMongo.deleteAll("sessions");
            myMongo.deleteAll("locations");
            myMongo.deleteAll("cdrs");
        }
    }


%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>mongoCheck</title>
    </head>
    <body>
        <h1>mongoCheck</h1>
        <%            myMongo.showCollections().forEach(( cn,  
                  
                count)->
            {try {
                    out1.println("<p>" + cn + " " + count);
                } catch (Exception e) {
                }
            }
            );
        %>
        <p><p><a href="mongoCheck.jsp?collection=ANY&remove">remove all from DB</a>
        <p><a href="mongoCheck.jsp">refresh</a>
    </body>
</html>
