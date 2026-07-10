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
                requireField(body, "id");
                AdoptionRequest request = gson.fromJson(body, AdoptionRequest.class);
                service.submitRequest(request);
                return Response.ok("Request submitted");

            } else if (subAction.equals("get")) {
                AdoptionRequest request = service.getRequest(requireInt(body, "id"));
                if (request == null)
                    return Response.notFound("Request not found");
                return Response.ok(request);

            } else if (subAction.equals("getAll")) {
                Collection<AdoptionRequest> requests = service.getAllRequests();
                return Response.ok(requests);

            } else if (subAction.equals("approve")) {
                service.approveRequest(requireInt(body, "id"));
                return Response.ok("Request approved");

            } else if (subAction.equals("reject")) {
                service.rejectRequest(requireInt(body, "id"));
                return Response.ok("Request rejected");

            } else if (subAction.equals("delete")) {
                service.deleteRequest(requireInt(body, "id"));
                return Response.ok("Request deleted");

            } else {
                return Response.error("Unknown subAction: " + subAction);
            }

        } catch (Exception e) {
            return Response.error("Error in AdoptionController: " + e.getMessage());
        }
    }

    private void requireField(JsonObject body, String field) {
        if (body == null || !body.has(field) || body.get(field).isJsonNull()) {
            throw new IllegalArgumentException("Missing field: " + field);
        }
    }

    private int requireInt(JsonObject body, String field) {
        requireField(body, field);
        return body.get(field).getAsInt();
    }
}
