package com.hit.model;

// represents the response that the server sends back to the client
// status 200 = ok, 404 = not found, 500 = error
public class Response {

    private int status;
    private String message;
    private Object data;

    public Response(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static Response ok(Object data) {
        return new Response(200, "OK", data);
    }

    public static Response notFound(String message) {
        return new Response(404, message, null);
    }

    public static Response error(String message) {
        return new Response(500, message, null);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
