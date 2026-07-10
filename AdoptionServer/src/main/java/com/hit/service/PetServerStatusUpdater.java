package com.hit.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

// talks to the pet server over TCP and asks it to change a pet's status.
// same json protocol the client uses: {"headers":{"action":"pet/setStatus"},"body":{...}}
public class PetServerStatusUpdater implements IPetStatusUpdater {

    private String host;
    private int port;
    private Gson gson = new Gson();

    public PetServerStatusUpdater(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void setStatus(int petId, String status) throws Exception {
        JsonObject headers = new JsonObject();
        headers.addProperty("action", "pet/setStatus");

        JsonObject body = new JsonObject();
        body.addProperty("id", petId);
        body.addProperty("status", status);

        JsonObject request = new JsonObject();
        request.add("headers", headers);
        request.add("body", body);

        try (Socket socket = new Socket(host, port);
             Scanner reader = new Scanner(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            writer.println(gson.toJson(request));
            writer.flush();

            if (!reader.hasNextLine()) {
                throw new Exception("No response from pet server");
            }
            JsonObject response = gson.fromJson(reader.nextLine(), JsonObject.class);
            int code = response.get("status").getAsInt();
            if (code != 200) {
                throw new Exception(response.get("message").getAsString());
            }
        }
    }
}
