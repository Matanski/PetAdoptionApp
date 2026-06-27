package com.hit.controller;

import com.google.gson.JsonObject;
import com.hit.model.Response;

// interface that all controllers must implement
// each controller knows how to handle requests for its domain (pets / adoptions)
public interface Controller {
    Response handle(String subAction, JsonObject body);
}
