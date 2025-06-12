package com.ai;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/ai")
public class App {

    @Inject
    MyAiService ai;

    @GET
    @Path("/fact")
    @Produces(MediaType.TEXT_PLAIN)
    public String fact() {
        return ai.funFact("space");
    }
}