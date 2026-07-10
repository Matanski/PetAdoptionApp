package com.hit.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hit.dm.Pet;
import com.hit.model.Response;
import com.hit.service.ServicePet;

import java.util.Collection;

public class PetController implements Controller {

    private ServicePet service;
    private Gson gson = new Gson();

    public PetController(ServicePet service) {
        this.service = service;
    }

    @Override
    public Response handle(String subAction, JsonObject body) {
        try {
            if (subAction.equals("save")) {
                requireField(body, "id");
                Pet pet = gson.fromJson(body, Pet.class);
                service.addPet(pet);
                return Response.ok("Pet saved successfully");

            } else if (subAction.equals("get")) {
                Pet pet = service.getPet(requireInt(body, "id"));
                if (pet == null)
                    return Response.notFound("Pet not found");
                return Response.ok(pet);

            } else if (subAction.equals("getAll")) {
                Collection<Pet> pets = service.getAllPets();
                return Response.ok(pets);

            } else if (subAction.equals("delete")) {
                service.removePet(requireInt(body, "id"));
                return Response.ok("Pet deleted");

            } else if (subAction.equals("update")) {
                requireField(body, "id");
                Pet pet = gson.fromJson(body, Pet.class);
                service.updatePet(pet);
                return Response.ok("Pet updated");

            } else if (subAction.equals("setStatus")) {
                // used by the adoption server when a request is approved
                int id = requireInt(body, "id");
                String status = requireString(body, "status");
                service.setStatus(id, status);
                return Response.ok("Pet status updated to " + status);

            } else {
                return Response.error("Unknown subAction: " + subAction);
            }

        } catch (Exception e) {
            return Response.error("Error in PetController: " + e.getMessage());
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
