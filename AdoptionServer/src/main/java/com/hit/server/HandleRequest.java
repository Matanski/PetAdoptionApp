package com.hit.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hit.controller.Controller;
import com.hit.controller.ControllerFactory;
import com.hit.model.Request;
import com.hit.model.Response;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class HandleRequest implements Runnable {

    private Socket socket;
    private Gson gson = new Gson();

    public HandleRequest(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // wrap the socket streams so we can read/write text easily
        try (Socket client = socket;
             Scanner reader = new Scanner(new InputStreamReader(client.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()))) {

            if (!reader.hasNextLine()) return;
            String json = reader.nextLine();
            System.out.println("Got request: " + json);

            // always answer the client, even when the request is broken
            Response response = process(json);
            writer.println(gson.toJson(response));
            writer.flush();

        } catch (IOException e) {
            System.out.println("Error handling request: " + e.getMessage());
        }
    }

    // parses the request, finds the controller through the factory and runs it
    private Response process(String json) {
        try {
            Request request = gson.fromJson(json, Request.class);
            if (request == null || request.getHeaders() == null
                    || request.getHeaders().getAction() == null) {
                return Response.error("Malformed request: missing headers.action");
            }

            String action = request.getHeaders().getAction();
            Controller controller = ControllerFactory.getController(action);
            if (controller == null) {
                return Response.error("Unknown action: " + action);
            }

            JsonObject body = request.getBody() != null ? request.getBody() : new JsonObject();
            return controller.handle(ControllerFactory.getSubAction(action), body);

        } catch (Exception e) {
            return Response.error("Bad request: " + e.getMessage());
        }
    }
}
