package com.hit.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hit.dm.Pet;
import com.hit.model.Response;
import com.hit.service.ServicePet;

import java.util.Collection;

// separation layer between the networking and the ServicePet business logic.
// exposes the pet API as explicit typed methods; handle() only routes the
// incoming sub-action to the matching method.
public class PetController implements Controller {

    private ServicePet service;
    private Gson gson = new Gson();

    public PetController(ServicePet service) {
        this.service = service;
    }

    // ─── the exposed API ─────────────────────────────────────────────────────

    public Response savePet(Pet pet) {
        try {
            service.addPet(pet);
            return Response.ok("Pet saved successfully");
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response getPet(int id) {
        try {
            Pet pet = service.getPet(id);
            return pet == null ? Response.notFound("Pet not found") : Response.ok(pet);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response getAllPets() {
        try {
            Collection<Pet> pets = service.getAllPets();
            return Response.ok(pets);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response updatePet(Pet pet) {
        try {
            service.updatePet(pet);
            return Response.ok("Pet updated");
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response deletePet(int id) {
        try {
            service.removePet(id);
            return Response.ok("Pet deleted");
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response setPetStatus(int id, String status) {
        try {
            service.setStatus(id, status);
            return Response.ok("Pet status updated to " + status);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // ─── routing ─────────────────────────────────────────────────────────────

    @Override
    public Response handle(String subAction, JsonObject body) {
        switch (subAction) {
            case "save":
                requireField(body, "id");
                return savePet(gson.fromJson(body, Pet.class));
            case "get":
                return getPet(requireInt(body, "id"));
            case "getAll":
                return getAllPets();
            case "update":
                requireField(body, "id");
                return updatePet(gson.fromJson(body, Pet.class));
            case "delete":
                return deletePet(requireInt(body, "id"));
            case "setStatus":
                return setPetStatus(requireInt(body, "id"), requireString(body, "status"));
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

    private String requireString(JsonObject body, String field) {
        requireField(body, field);
        return body.get(field).getAsString();
    }
}
