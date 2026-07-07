package com.hit.model;

import com.google.gson.JsonObject;

// represents a request that comes from the client in JSON format
// has a headers section with the action and a body with the data
public class Request {

    private Headers headers;
    private JsonObject body;

    public static class Headers {
        private String action;

        public String getAction() {
            return action;
        }
    }

    public Headers getHeaders() {
        return headers;
    }

    public JsonObject getBody() {
        return body;
    }
}
