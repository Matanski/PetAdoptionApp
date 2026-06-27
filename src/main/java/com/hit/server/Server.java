package com.hit.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is running on port " + port);

            // keep listening for new clients
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected on port " + port);
                new Thread(new HandleRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
