package com.hit.controller;

import java.util.HashMap;
import java.util.Map;

// factory that maps action prefix to the right controller
// for example: "pet/save" -> PetController
public class ControllerFactory {

    private static Map<String, Controller> controllers = new HashMap<>();

    public static void register(String prefix, Controller controller) {
        controllers.put(prefix, controller);
    }

    // get the controller based on the first part of the action (before the "/")
    public static Controller getController(String action) {
        if (action == null || !action.contains("/")) return null;
        String prefix = action.split("/")[0];
        return controllers.get(prefix);
    }

    // get the second part of the action (after the "/")
    public static String getSubAction(String action) {
        if (action == null || !action.contains("/")) return "";
        String[] parts = action.split("/");
        return parts.length > 1 ? parts[1] : "";
    }
}
