package com.hit.server;

// entry point of the adoption server - the only code here is starting the server
public class ServerDriver {

    public static void main(String[] args) {
        Server server = new Server(34568);
        new Thread(server).start();
    }
}
