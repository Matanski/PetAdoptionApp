package com.hit.service;

import com.hit.algorithm.IAlgoTextCompression;
import com.hit.algorithm.LzwAlgoImpl;
import com.hit.dao.IDao;
import com.hit.dao.PetDaoFileImpl;
import com.hit.dm.Pet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class ServicePetTest {
    private static final String DATASOURCE = "src/main/resources/datasource.txt";
    private ServicePet service;

    @Before
    public void setUp() throws Exception {
        IAlgoTextCompression algo = new LzwAlgoImpl();
        IDao<Pet> dao = new PetDaoFileImpl(DATASOURCE);
        service = new ServicePet(dao, algo);
        for (Pet p : service.getAllPets()) {
            service.removePet(p.getId());
        }
    }

    @Test
    public void testAddAndGetPet() throws Exception {
        Pet pet = new Pet(1, "Buddy", "Dog", "Labrador", 3,
                "Friendly and energetic dog, loves to play fetch");
        service.addPet(pet);

        Pet retrieved = service.getPet(1);
        Assert.assertNotNull(retrieved);
        Assert.assertEquals("Buddy", retrieved.getName());
        Assert.assertEquals("Dog", retrieved.getSpecies());
        Assert.assertEquals("Friendly and energetic dog, loves to play fetch",
                retrieved.getDescription());
    }

    @Test
    public void testDeletePet() throws Exception {
        Pet pet = new Pet(2, "Whiskers", "Cat", "Persian", 2, "Calm indoor cat");
        service.addPet(pet);
        service.removePet(2);

        Pet retrieved = service.getPet(2);
        Assert.assertNull(retrieved);
    }

    @Test
    public void testGetAllPets() throws Exception {
        service.addPet(new Pet(1, "Buddy", "Dog", "Lab", 3, "Good dog good dog"));
        service.addPet(new Pet(2, "Kitty", "Cat", "Siamese", 1, "Cute cat cute cat"));

        Collection<Pet> pets = service.getAllPets();
        Assert.assertEquals(2, pets.size());
    }

    @Test
    public void testUpdatePet() throws Exception {
        Pet pet = new Pet(1, "Buddy", "Dog", "Lab", 3, "Old description");
        service.addPet(pet);

        pet.setDescription("Updated description");
        service.updatePet(pet);

        Pet updated = service.getPet(1);
        Assert.assertEquals("Updated description", updated.getDescription());
    }

    @Test
    public void testCompressionRoundTrip() throws Exception {
        String original = "TOBEORNOTTOBEORTOBEORNOT";
        Pet pet = new Pet(10, "TestPet", "Dog", "Mix", 1, original);
        service.addPet(pet);

        Pet retrieved = service.getPet(10);
        Assert.assertEquals(original, retrieved.getDescription());
    }

    @Test
    public void testPetStatusDefault() throws Exception {
        Pet pet = new Pet(3, "Fido", "Dog", "Beagle", 4, "Playful beagle");
        service.addPet(pet);

        Pet retrieved = service.getPet(3);
        Assert.assertEquals("available", retrieved.getStatus());
    }
}
