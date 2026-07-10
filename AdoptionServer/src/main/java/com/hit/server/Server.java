package com.hit.server;

import com.hit.controller.AdoptionController;
import com.hit.controller.ControllerFactory;
import com.hit.dao.AdoptionRequestDaoFileImpl;
import com.hit.dao.IDao;
import com.hit.dm.AdoptionRequest;
import com.hit.service.IPetStatusUpdater;
import com.hit.service.PetServerStatusUpdater;
import com.hit.service.ServiceAdoption;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// adoption server - exposes the adoption API over TCP
// this server does NOT need the algorithm module
public class Server implements Runnable {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        // initialize all the components this server needs (spec: done inside run())
        IDao<AdoptionRequest> adoptionDao =
                new AdoptionRequestDaoFileImpl("src/main/resources/adoptions.dat");

        // approving a request marks the pet adopted on the pet server (port 34567)
        IPetStatusUpdater petStatusUpdater = new PetServerStatusUpdater("localhost", 34567);
        ServiceAdoption serviceAdoption = new ServiceAdoption(adoptionDao, petStatusUpdater);

        // register the adoption controller in the factory (happens once on startup)
        ControllerFactory.register("adoption", new AdoptionController(serviceAdoption));

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Adoption server is running on port " + port);

            // keep listening for new clients, handle each on its own thread
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
