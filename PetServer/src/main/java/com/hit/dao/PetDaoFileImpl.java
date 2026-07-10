package com.hit.dao;

import com.hit.dm.Pet;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// saves and loads pets from a file using object serialization.
// the public methods are synchronized so concurrent client threads cannot
// interleave a read and a write and corrupt the data file.
public class PetDaoFileImpl implements IDao<Pet> {

    private String filePath;

    public PetDaoFileImpl(String filePath) {
        this.filePath = filePath;
    }

    // read all pets from the file
    private List<Pet> readAll() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0)
            return new ArrayList<>();

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
            List<Pet> pets = (List<Pet>) ois.readObject();
            ois.close();
            return pets;
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // write all pets back to the file
    private void writeAll(List<Pet> pets) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
        oos.writeObject(pets);
        oos.close();
    }

    @Override
    public synchronized void save(Pet pet) throws Exception {
        List<Pet> pets = readAll();
        pets.add(pet);
        writeAll(pets);
    }

    @Override
    public synchronized Pet get(int id) throws Exception {
        for (Pet p : readAll()) {
            if (p.getId() == id)
                return p;
        }
        return null;
    }

    @Override
    public synchronized Collection<Pet> getAll() throws Exception {
        return readAll();
    }

    @Override
    public synchronized void delete(int id) throws Exception {
        List<Pet> pets = readAll();
        for (int i = 0; i < pets.size(); i++) {
            if (pets.get(i).getId() == id) {
                pets.remove(i);
                break;
            }
        }
        writeAll(pets);
    }

    @Override
    public synchronized void update(Pet pet) throws Exception {
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
