package com.hit.server;

import com.google.gson.Gson;
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
        try {
            // wrap the socket streams so we can read/write text easily
            Scanner reader = new Scanner(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            // read the json request
            if (!reader.hasNextLine()) return;
            String json = reader.nextLine();
            System.out.println("Got request: " + json);

            // parse the request and get the action from the header
            Request request = gson.fromJson(json, Request.class);
            String action = request.getHeaders().getAction();

            // find the right controller using the factory
            Controller controller = ControllerFactory.getController(action);
            Response response;

            if (controller == null) {
                System.out.println("No controller for action: " + action);
                response = Response.error("Unknown action: " + action);
            } else {
                String subAction = ControllerFactory.getSubAction(action);
                response = controller.handle(subAction, request.getBody());
            }

            // send response back to client
            writer.println(gson.toJson(response));
            writer.flush();

            reader.close();
            writer.close();
            socket.close();

        } catch (Exception e) {
            System.out.println("Error handling request: " + e.getMessage());
        }
    }
}
