package com.hit.dao;

import com.hit.dm.Pet;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PetDaoFileImpl implements IDao<Pet> {
    private final String filePath;

    public PetDaoFileImpl(String filePath) {
        this.filePath = filePath;
    }

    @SuppressWarnings("unchecked")
    private List<Pet> readAll() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<Pet>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void writeAll(List<Pet> pets) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();
        if (!file.exists()) file.createNewFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(pets);
        }
    }

    @Override
    public void save(Pet pet) throws Exception {
        List<Pet> pets = readAll();
        pets.add(pet);
        writeAll(pets);
    }

    @Override
    public Pet get(int id) throws Exception {
        return readAll().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<Pet> getAll() throws Exception {
        return readAll();
    }

    @Override
    public void delete(int id) throws Exception {
        List<Pet> pets = readAll();
        pets.removeIf(p -> p.getId() == id);
        writeAll(pets);
    }

    @Override
    public void update(Pet pet) throws Exception {
        List<Pet> pets = readAll();
        for (int i = 0; i < pets.size(); i++) {
            if (pets.get(i).getId() == pet.getId()) {
                pets.set(i, pet);
                break;
            }
        }
        writeAll(pets);
    }
}
