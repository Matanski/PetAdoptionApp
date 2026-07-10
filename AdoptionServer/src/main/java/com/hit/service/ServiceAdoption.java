package com.hit.service;

import com.hit.dao.IDao;
import com.hit.dm.AdoptionRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// handles business logic for adoption requests
public class ServiceAdoption {

    private IDao<AdoptionRequest> dao;
    private IPetStatusUpdater petStatusUpdater;

    public ServiceAdoption(IDao<AdoptionRequest> dao) {
        // no updater: used by the unit tests, where no pet server is running
        this(dao, null);
    }

    public ServiceAdoption(IDao<AdoptionRequest> dao, IPetStatusUpdater petStatusUpdater) {
        this.dao = dao;
        this.petStatusUpdater = petStatusUpdater;
    }

    public void submitRequest(AdoptionRequest request) throws Exception {
        if (dao.get(request.getId()) != null) {
            throw new IllegalArgumentException(
                    "A request with ID " + request.getId() + " already exists");
        }
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

    // approving a request also marks the pet as adopted on the pet server.
    // the pet is updated first: if that fails we do not approve the request,
    // so the two servers cannot disagree about the pet.
    public void approveRequest(int id) throws Exception {
        AdoptionRequest request = dao.get(id);
        if (request == null) {
            throw new IllegalArgumentException("No request with ID " + id);
        }
        if (petStatusUpdater != null) {
            petStatusUpdater.setStatus(request.getPetId(), "adopted");
        }
        request.setStatus("approved");
        dao.update(request);
    }

    public void rejectRequest(int id) throws Exception {
        AdoptionRequest request = dao.get(id);
        if (request == null) {
            throw new IllegalArgumentException("No request with ID " + id);
        }
        request.setStatus("rejected");
        dao.update(request);
    }

    public void deleteRequest(int id) throws Exception {
        dao.delete(id);
    }
}
