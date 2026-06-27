package com.hit.service;

import com.hit.dao.AdoptionRequestDaoFileImpl;
import com.hit.dao.IDao;
import com.hit.dm.AdoptionRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class ServiceAdoptionTest {
    private static final String DATASOURCE = "src/main/resources/test_adoptions.dat";
    private ServiceAdoption service;

    @Before
    public void setUp() throws Exception {
        IDao<AdoptionRequest> dao = new AdoptionRequestDaoFileImpl(DATASOURCE);
        service = new ServiceAdoption(dao);
        for (AdoptionRequest r : service.getAllRequests()) {
            service.deleteRequest(r.getId());
        }
    }

    @Test
    public void testSubmitAndGet() throws Exception {
        AdoptionRequest request = new AdoptionRequest(1, 10, 5, "I love dogs");
        service.submitRequest(request);

        AdoptionRequest retrieved = service.getRequest(1);
        Assert.assertNotNull(retrieved);
        Assert.assertEquals(10, retrieved.getUserId());
        Assert.assertEquals(5, retrieved.getPetId());
        Assert.assertEquals("I love dogs", retrieved.getMessage());
    }

    @Test
    public void testDefaultStatusIsPending() throws Exception {
        service.submitRequest(new AdoptionRequest(2, 11, 6, "Would love to adopt"));
        AdoptionRequest retrieved = service.getRequest(2);
        Assert.assertEquals("pending", retrieved.getStatus());
    }

    @Test
    public void testApproveRequest() throws Exception {
        service.submitRequest(new AdoptionRequest(3, 12, 7, "Great home"));
        service.approveRequest(3);

        AdoptionRequest retrieved = service.getRequest(3);
        Assert.assertEquals("approved", retrieved.getStatus());
    }

    @Test
    public void testRejectRequest() throws Exception {
        service.submitRequest(new AdoptionRequest(4, 13, 8, "Small apartment"));
        service.rejectRequest(4);

        AdoptionRequest retrieved = service.getRequest(4);
        Assert.assertEquals("rejected", retrieved.getStatus());
    }

    @Test
    public void testDeleteRequest() throws Exception {
        service.submitRequest(new AdoptionRequest(5, 14, 9, "Temporary request"));
        service.deleteRequest(5);

        AdoptionRequest retrieved = service.getRequest(5);
        Assert.assertNull(retrieved);
    }

    @Test
    public void testGetAllRequests() throws Exception {
        service.submitRequest(new AdoptionRequest(1, 10, 1, "First request"));
        service.submitRequest(new AdoptionRequest(2, 11, 2, "Second request"));

        Collection<AdoptionRequest> all = service.getAllRequests();
        Assert.assertEquals(2, all.size());
    }
}
