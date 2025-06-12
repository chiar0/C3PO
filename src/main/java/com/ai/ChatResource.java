package com.ai;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/chat")
public class ChatResource {

    private static final Logger LOG = Logger.getLogger(ChatResource.class);

    @Inject
    MyAiService aiService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response chat(ChatRequest request) {
        try {
            LOG.info("Ricevuta richiesta chat: " + request.getMessage());
            String response = aiService.generateText(request.getMessage());
            LOG.info("Risposta generata: " + response);
            return Response.ok(new ChatResponse(response)).build();
        } catch (Exception e) {
            LOG.error("Errore durante la generazione della risposta", e);
            String errorMessage = "Il servizio è momentaneamente occupato. Riprova tra qualche secondo.";
            if (e.getMessage().contains("timeout")) {
                errorMessage = "Il modello AI è occupato. Riprova tra qualche istante.";
            }
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ChatResponse(errorMessage))
                .build();
        }
    }

    public static class ChatRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ChatResponse {
        private String reply;

        public ChatResponse() {}

        public ChatResponse(String reply) {
            this.reply = reply;
        }

        public String getReply() {
            return reply;
        }

        public void setReply(String reply) {
            this.reply = reply;
        }
    }
}