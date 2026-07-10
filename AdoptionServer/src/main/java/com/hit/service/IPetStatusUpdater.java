package com.hit.service;

// the adoption service needs to mark a pet as adopted, but pets live on the other
// server. we hide that call behind this interface so ServiceAdoption does not depend
// on sockets, and so tests can pass a fake implementation instead.
public interface IPetStatusUpdater {
    void setStatus(int petId, String status) throws Exception;
}
