package com.hit.server;

import com.hit.algorithm.IAlgoTextCompression;
import com.hit.algorithm.LzwAlgoImpl;
import com.hit.controller.ControllerFactory;
import com.hit.controller.PetController;
import com.hit.dao.IDao;
import com.hit.dao.PetDaoFileImpl;
import com.hit.dm.Pet;
import com.hit.service.ServicePet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// pet server - exposes the pet API over TCP
// this server needs the algorithm module to compress pet descriptions
public class Server implements Runnable {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        // initialize all the components this server needs (spec: done inside run())
        IAlgoTextCompression algo = new LzwAlgoImpl();
        IDao<Pet> petDao = new PetDaoFileImpl("src/main/resources/pets.dat");
        ServicePet servicePet = new ServicePet(petDao, algo);

        // register the pet controller in the factory (happens once on startup)
        ControllerFactory.register("pet", new PetController(servicePet));

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Pet server is running on port " + port);

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
