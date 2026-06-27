package com.hit.client.model;

public class Pet {
    private int id;
    private String name;
    private String species;
    private String breed;
    private int age;
    private String description;
    private String status;

    public Pet() {}

    public Pet(int id, String name, String species, String breed, int age, String description) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.description = description;
        this.status = "available";
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getBreed() { return breed; }
    public int getAge() { return age; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSpecies(String species) { this.species = species; }
    public void setBreed(String breed) { this.breed = breed; }
    public void setAge(int age) { this.age = age; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " (" + species + " / " + breed + ") - " + status;
    }
}
