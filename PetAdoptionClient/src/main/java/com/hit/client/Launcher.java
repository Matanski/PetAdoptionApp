package com.hit.client;

import com.hit.client.view.MainApp;

// Run THIS class to start the app.
// JavaFX refuses to launch a class that extends Application when the JavaFX jars
// are on the plain classpath. A launcher that does not extend Application avoids
// that check, so we can run using the bundled jars in lib/ without the module path.
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
