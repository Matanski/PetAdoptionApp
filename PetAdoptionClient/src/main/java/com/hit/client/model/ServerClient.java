package com.hit.client.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Model layer: handles all socket communication with the server
public class ServerClient {
    private final String host;
    private final int port;
    private final Gson gson = new Gson();

    public ServerClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public JsonObject send(String action, JsonObject body) throws Exception {
        JsonObject headers = new JsonObject();
        headers.addProperty("action", action);

        JsonObject request = new JsonObject();
        request.add("headers", headers);
        request.add("body", body);

        try (Socket socket = new Socket(host, port);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
             Scanner reader = new Scanner(new InputStreamReader(socket.getInputStream()))) {

            writer.println(gson.toJson(request));
            writer.flush();

            if (reader.hasNextLine()) {
                String raw = reader.nextLine();
                return JsonParser.parseString(raw).getAsJsonObject();
            }
        }
        return null;
    }
}
