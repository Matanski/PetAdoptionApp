package com.hit.dao;

import com.hit.dm.AdoptionRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdoptionRequestDaoFileImpl implements IDao<AdoptionRequest> {
    private final String filePath;

    public AdoptionRequestDaoFileImpl(String filePath) {
        this.filePath = filePath;
    }

    @SuppressWarnings("unchecked")
    private List<AdoptionRequest> readAll() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<AdoptionRequest>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void writeAll(List<AdoptionRequest> requests) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();
        if (!file.exists()) file.createNewFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(requests);
        }
    }

    @Override
    public void save(AdoptionRequest request) throws Exception {
        List<AdoptionRequest> requests = readAll();
        requests.add(request);
        writeAll(requests);
    }

    @Override
    public AdoptionRequest get(int id) throws Exception {
        return readAll().stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<AdoptionRequest> getAll() throws Exception {
        return readAll();
    }

    @Override
    public void delete(int id) throws Exception {
        List<AdoptionRequest> requests = readAll();
        requests.removeIf(r -> r.getId() == id);
        writeAll(requests);
    }

    @Override
    public void update(AdoptionRequest request) throws Exception {
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
