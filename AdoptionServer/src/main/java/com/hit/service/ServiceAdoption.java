package com.hit.service;

import com.hit.dao.IDao;
import com.hit.dm.AdoptionRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// handles business logic for adoption requests
public class ServiceAdoption {

    private IDao<AdoptionRequest> dao;

    public ServiceAdoption(IDao<AdoptionRequest> dao) {
        this.dao = dao;
    }

    public void submitRequest(AdoptionRequest request) throws Exception {
        dao.save(request);
    }

    public AdoptionRequest getRequest(int id) throws Exception {
        return dao.get(id);
    }

    public Collection<AdoptionRequest> getAllRequests() throws Exception {
        return dao.getAll();
    }

    public Collection<AdoptionRequest> getRequestsByPet(int petId) throws Exception {
        List<AdoptionRequest> result = new ArrayList<>();
        for (AdoptionRequest r : dao.getAll()) {
            if (r.getPetId() == petId) {
                result.add(r);
            }
        }
        return result;
    }

    public void approveRequest(int id) throws Exception {
        AdoptionRequest request = dao.get(id);
        if (request != null) {
            request.setStatus("approved");
            dao.update(request);
        }
    }

    public void rejectRequest(int id) throws Exception {
        AdoptionRequest request = dao.get(id);
        if (request != null) {
            request.setStatus("rejected");
            dao.update(request);
        }
    }

    public void deleteRequest(int id) throws Exception {
        dao.delete(id);
    }
}
