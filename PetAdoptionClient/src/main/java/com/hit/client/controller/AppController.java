package com.hit.client.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hit.client.model.AdoptionRequest;
import com.hit.client.model.Pet;
import com.hit.client.model.ServerClient;

import java.util.ArrayList;
import java.util.List;

// MVC Controller: mediates between View and Model (ServerClient)
public class AppController {
    private final ServerClient client;
    private final Gson gson = new Gson();

    public AppController(String host, int port) {
        this.client = new ServerClient(host, port);
    }

    public String addPet(Pet pet) {
        try {
            JsonObject body = gson.toJsonTree(pet).getAsJsonObject();
            JsonObject response = client.send("pet/save", body);
            return getMessage(response);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public Pet getPet(int id) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", id);
            JsonObject response = client.send("pet/get", body);
            if (isOk(response)) {
                return gson.fromJson(response.get("data"), Pet.class);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<Pet> getAllPets() {
        List<Pet> pets = new ArrayList<>();
        try {
            JsonObject body = new JsonObject();
            JsonObject response = client.send("pet/getAll", body);
            if (isOk(response)) {
                JsonArray arr = response.get("data").getAsJsonArray();
                for (JsonElement el : arr) {
                    pets.add(gson.fromJson(el, Pet.class));
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return pets;
    }

    public String deletePet(int id) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", id);
            JsonObject response = client.send("pet/delete", body);
            return getMessage(response);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String updatePet(Pet pet) {
        try {
            JsonObject body = gson.toJsonTree(pet).getAsJsonObject();
            JsonObject response = client.send("pet/update", body);
            return getMessage(response);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String submitAdoption(AdoptionRequest request) {
        try {
            JsonObject body = gson.toJsonTree(request).getAsJsonObject();
            JsonObject response = client.send("adoption/submit", body);
            return getMessage(response);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public List<AdoptionRequest> getAllAdoptions() {
        List<AdoptionRequest> list = new ArrayList<>();
        try {
            JsonObject body = new JsonObject();
            JsonObject response = client.send("adoption/getAll", body);
            if (isOk(response)) {
                JsonArray arr = response.get("data").getAsJsonArray();
                for (JsonElement el : arr) {
                    list.add(gson.fromJson(el, AdoptionRequest.class));
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public String approveAdoption(int id) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", id);
            JsonObject response = client.send("adoption/approve", body);
            return getMessage(response);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String rejectAdoption(int id) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("id", id);
            JsonObject response = client.send("adoption/reject", body);
            return getMessage(response);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private boolean isOk(JsonObject response) {
        return response != null && response.get("status").getAsInt() == 200;
    }

    private String getMessage(JsonObject response) {
        if (response == null) return "No response from server";
        return response.get("message").getAsString();
    }
}
