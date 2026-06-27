package com.hit.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hit.dm.AdoptionRequest;
import com.hit.model.Response;
import com.hit.service.ServiceAdoption;

import java.util.Collection;

public class AdoptionController implements Controller {

    private ServiceAdoption service;
    private Gson gson = new Gson();

    public AdoptionController(ServiceAdoption service) {
        this.service = service;
    }

    @Override
    public Response handle(String subAction, JsonObject body) {
        try {
            if (subAction.equals("submit")) {
                AdoptionRequest request = gson.fromJson(body, AdoptionRequest.class);
                service.submitRequest(request);
                return Response.ok("Request submitted");

            } else if (subAction.equals("get")) {
                int id = body.get("id").getAsInt();
                AdoptionRequest request = service.getRequest(id);
                if (request == null)
                    return Response.notFound("Request not found");
                return Response.ok(request);

            } else if (subAction.equals("getAll")) {
                Collection<AdoptionRequest> requests = service.getAllRequests();
                return Response.ok(requests);

            } else if (subAction.equals("approve")) {
                int id = body.get("id").getAsInt();
                service.approveRequest(id);
                return Response.ok("Request approved");

            } else if (subAction.equals("reject")) {
                int id = body.get("id").getAsInt();
                service.rejectRequest(id);
                return Response.ok("Request rejected");

            } else if (subAction.equals("delete")) {
                int id = body.get("id").getAsInt();
                service.deleteRequest(id);
                return Response.ok("Request deleted");

            } else {
                return Response.error("Unknown subAction: " + subAction);
            }

        } catch (Exception e) {
            return Response.error("Error in AdoptionController: " + e.getMessage());
        }
    }
}
