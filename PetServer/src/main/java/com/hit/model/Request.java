package com.hit.model;

// generic request that comes from the client as JSON.
// T is the type of the body content (a data model, a JsonObject, etc.), which lets
// the same Request shape carry any payload - see HandleRequest for the TypeToken parse.
public class Request<T> {

    private Headers headers;
    private T body;

    public static class Headers {
        private String action;

        public String getAction() {
            return action;
        }
    }

    public Headers getHeaders() {
        return headers;
    }

    public T getBody() {
        return body;
    }
}
