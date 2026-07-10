package com.hit.dao;

import com.hit.dm.AdoptionRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// saves and loads adoption requests from a file.
// the public methods are synchronized so concurrent client threads cannot
// interleave a read and a write and corrupt the data file.
public class AdoptionRequestDaoFileImpl implements IDao<AdoptionRequest> {

    private String filePath;

    public AdoptionRequestDaoFileImpl(String filePath) {
        this.filePath = filePath;
    }

    private List<AdoptionRequest> readAll() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0)
            return new ArrayList<>();

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
            List<AdoptionRequest> requests = (List<AdoptionRequest>) ois.readObject();
            ois.close();
            return requests;
        } catch (Exception e) {
            System.out.println("Error reading adoptions file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void writeAll(List<AdoptionRequest> requests) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null)
            file.getParentFile().mkdirs();

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
        oos.writeObject(requests);
        oos.close();
    }

    @Override
    public synchronized void save(AdoptionRequest request) throws Exception {
        List<AdoptionRequest> requests = readAll();
        requests.add(request);
        writeAll(requests);
    }

    @Override
    public synchronized AdoptionRequest get(int id) throws Exception {
        for (AdoptionRequest r : readAll()) {
            if (r.getId() == id)
                return r;
        }
        return null;
    }

    @Override
    public synchronized Collection<AdoptionRequest> getAll() throws Exception {
        return readAll();
    }

    @Override
    public synchronized void delete(int id) throws Exception {
        List<AdoptionRequest> requests = readAll();
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getId() == id) {
                requests.remove(i);
                break;
            }
        }
        writeAll(requests);
    }

    @Override
    public synchronized void update(AdoptionRequest request) throws Exception {
        List<AdoptionRequest> requests = readAll();
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getId() == request.getId()) {
                requests.set(i, request);
                break;
            }
        }
        writeAll(requests);
    }
}
