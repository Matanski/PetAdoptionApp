package com.hit.service;

import com.hit.algorithm.IAlgoTextCompression;
import com.hit.dao.IDao;
import com.hit.dm.Pet;

import java.util.Collection;

// handles all the business logic for pets
// uses the algorithm to compress/decompress the description before saving
public class ServicePet {

    private IDao<Pet> dao;
    private IAlgoTextCompression algo;

    // inject the dao and algorithm from outside (strategy pattern)
    public ServicePet(IDao<Pet> dao, IAlgoTextCompression algo) {
        this.dao = dao;
        this.algo = algo;
    }

    public void addPet(Pet pet) throws Exception {
        if (dao.get(pet.getId()) != null) {
            throw new IllegalArgumentException("A pet with ID " + pet.getId() + " already exists");
        }
        // a new pet is always available. json parsing bypasses the constructor,
        // so the default is applied here rather than trusting the client to send it.
        if (pet.getStatus() == null || pet.getStatus().isEmpty()) {
            pet.setStatus("available");
        }
        // compress the description before saving to save space
        pet.setDescription(algo.compress(pet.getDescription()));
        dao.save(pet);
    }

    public Pet getPet(int id) throws Exception {
        Pet pet = dao.get(id);
        if (pet != null) {
            // decompress the description when reading
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
        if (dao.get(id) == null) {
            throw new IllegalArgumentException("No pet with ID " + id);
        }
        dao.delete(id);
    }

    public void updatePet(Pet pet) throws Exception {
        Pet existing = dao.get(pet.getId());
        if (existing == null) {
            throw new IllegalArgumentException("No pet with ID " + pet.getId());
        }
        // the status is not part of the edit form - it only changes through
        // setStatus (for example when an adoption is approved), so keep the
        // stored value instead of the one the client sent.
        pet.setStatus(existing.getStatus());
        pet.setDescription(algo.compress(pet.getDescription()));
        dao.update(pet);
    }

    // changes only the status field. the pet is read straight from the dao so its
    // description stays compressed - re-compressing an already compressed value
    // would corrupt it.
    public void setStatus(int id, String status) throws Exception {
        Pet pet = dao.get(id);
        if (pet == null) {
            throw new IllegalArgumentException("No pet with ID " + id);
        }
        pet.setStatus(status);
        dao.update(pet);
    }
}
