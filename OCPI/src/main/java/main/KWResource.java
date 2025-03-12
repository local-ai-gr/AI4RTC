/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package main;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author nsofias
 */
@Path("KW")
@RequestScoped
public class KWResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of KW
     */
    public KWResource() {
    }

    /**
     * Retrieves representation of an instance of dataServlets.KWResource
     *
     * @param location_id
     * @param plan
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{location_id}/{plan}")
    public String getText(@PathParam("location_id") String location_id,
            @PathParam("plan") String plan) {
        return "location_id=" + location_id + ", KW=" + plan;
    }

    /**
     * PUT method for updating or creating an instance of KWResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public void putText(String content
    ) {
    }
}
