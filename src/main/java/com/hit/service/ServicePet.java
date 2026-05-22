package com.hit.service;

import com.hit.algorithm.IAlgoTextCompression;
import com.hit.dao.IDao;
import com.hit.dm.Pet;

import java.util.Collection;

public class ServicePet {
    private final IDao<Pet> dao;
    private final IAlgoTextCompression algo;

    public ServicePet(IDao<Pet> dao, IAlgoTextCompression algo) {
        this.dao = dao;
        this.algo = algo;
    }

    public void addPet(Pet pet) throws Exception {
        pet.setDescription(algo.compress(pet.getDescription()));
        dao.save(pet);
    }

    public Pet getPet(int id) throws Exception {
        Pet pet = dao.get(id);
        if (pet != null) {
            pet.setDescription(algo.decompress(pet.getDescription()));
        }
        return pet;
    }

    public Collection<Pet> getAllPets() throws Exception {
        Collection<Pet> pets = dao.getAll();
        for (Pet pet : pets) {
            pet.setDescription(algo.decompress(pet.getDescription()));
        }
        return pets;
    }

    public void removePet(int id) throws Exception {
        dao.delete(id);
    }

    public void updatePet(Pet pet) throws Exception {
        pet.setDescription(algo.compress(pet.getDescription()));
        dao.update(pet);
    }
}
