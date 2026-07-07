package com.hit.server;

// entry point of the pet server - the only code here is starting the server
public class ServerDriver {

    public static void main(String[] args) {
        Server server = new Server(34567);
        new Thread(server).start();
    }
}
