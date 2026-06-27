package com.hit.server;

import com.hit.algorithm.IAlgoTextCompression;
import com.hit.algorithm.LzwAlgoImpl;
import com.hit.controller.AdoptionController;
import com.hit.controller.ControllerFactory;
import com.hit.controller.PetController;
import com.hit.dao.AdoptionRequestDaoFileImpl;
import com.hit.dao.IDao;
import com.hit.dao.PetDaoFileImpl;
import com.hit.dm.AdoptionRequest;
import com.hit.dm.Pet;
import com.hit.service.ServiceAdoption;
import com.hit.service.ServicePet;

public class ServerDriver {

    public static void main(String[] args) {

        // paths for the data files
        String petsFile = "src/main/resources/pets.dat";
        String adoptionsFile = "src/main/resources/adoptions.dat";

        // create the algorithm and DAOs
        IAlgoTextCompression algo = new LzwAlgoImpl();
        IDao<Pet> petDao = new PetDaoFileImpl(petsFile);
        IDao<AdoptionRequest> adoptionDao = new AdoptionRequestDaoFileImpl(adoptionsFile);

        // create the services - inject the dao and algorithm through constructor
        ServicePet servicePet = new ServicePet(petDao, algo);
        ServiceAdoption serviceAdoption = new ServiceAdoption(adoptionDao);

        // register the controllers in the factory - happens once on startup
        ControllerFactory.register("pet", new PetController(servicePet));
        ControllerFactory.register("adoption", new AdoptionController(serviceAdoption));

        // start 2 servers on different ports
        new Thread(new Server(34567)).start();
        new Thread(new Server(34568)).start();

        System.out.println("PetAdoption system is up!");
    }
}
