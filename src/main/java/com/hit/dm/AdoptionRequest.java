package com.hit.dm;

import java.io.Serializable;

public class AdoptionRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private int petId;
    private String status;
    private String message;

    public AdoptionRequest(int id, int userId, int petId, String message) {
        this.id = id;
        this.userId = userId;
        this.petId = petId;
        this.message = message;
        this.status = "pending";
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getPetId() { return petId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setPetId(int petId) { this.petId = petId; }
    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "AdoptionRequest{id=" + id + ", userId=" + userId +
               ", petId=" + petId + ", status='" + status + "'}";
    }
}
