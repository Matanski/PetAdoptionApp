package com.hit.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hit.dm.AdoptionRequest;
import com.hit.model.Response;
import com.hit.service.ServiceAdoption;

import java.util.Collection;

// separation layer between the networking and the ServiceAdoption business logic.
// exposes the adoption API as explicit typed methods; handle() only routes the
// incoming sub-action to the matching method.
public class AdoptionController implements Controller {

    private ServiceAdoption service;
    private Gson gson = new Gson();

    public AdoptionController(ServiceAdoption service) {
        this.service = service;
    }

    // ─── the exposed API ─────────────────────────────────────────────────────

    public Response submitRequest(AdoptionRequest request) {
        try {
            service.submitRequest(request);
            return Response.ok("Request submitted");
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response getRequest(int id) {
        try {
            AdoptionRequest request = service.getRequest(id);
            return request == null ? Response.notFound("Request not found") : Response.ok(request);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response getAllRequests() {
        try {
            Collection<AdoptionRequest> requests = service.getAllRequests();
            return Response.ok(requests);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response approveRequest(int id) {
        try {
            service.approveRequest(id);
            return Response.ok("Request approved");
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response rejectRequest(int id) {
        try {
            service.rejectRequest(id);
            return Response.ok("Request rejected");
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response deleteRequest(int id) {
        try {
            service.deleteRequest(id);
            return Response.ok("Request deleted");
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // ─── routing ─────────────────────────────────────────────────────────────

    @Override
    public Response handle(String subAction, JsonObject body) {
        switch (subAction) {
            case "submit":
                requireField(body, "id");
                return submitRequest(gson.fromJson(body, AdoptionRequest.class));
            case "get":
                return getRequest(requireInt(body, "id"));
            case "getAll":
                return getAllRequests();
            case "approve":
                return approveRequest(requireInt(body, "id"));
            case "reject":
                return rejectRequest(requireInt(body, "id"));
            case "delete":
                return deleteRequest(requireInt(body, "id"));
            default:
                return Response.error("Unknown subAction: " + subAction);
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
