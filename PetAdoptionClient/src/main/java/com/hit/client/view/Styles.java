package com.hit.client.view;

import javafx.scene.control.Button;

// Small helper holding the colours and button styling used across the UI.
// Everything here is plain JavaFX inline CSS - no external libraries.
public class Styles {

    public static final String PRIMARY = "#2563eb";
    public static final String SUCCESS = "#16a34a";
    public static final String DANGER  = "#dc2626";
    public static final String NEUTRAL = "#475569";
    public static final String AMBER   = "#d97706";

    public static final String BG      = "#f1f5f9";
    public static final String CARD    = "#ffffff";
    public static final String BORDER  = "#e2e8f0";
    public static final String TEXT    = "#0f172a";
    public static final String MUTED   = "#64748b";

    // a filled button that darkens slightly while the mouse is over it
    public static Button button(String text, String colour) {
        Button b = new Button(text);
        b.setStyle(base(colour));
        b.setOnMouseEntered(e -> b.setStyle(base(darken(colour))));
        b.setOnMouseExited(e -> b.setStyle(base(colour)));
        return b;
    }

    private static String base(String colour) {
        return "-fx-background-color: " + colour + ";"
             + "-fx-text-fill: white;"
             + "-fx-font-weight: bold;"
             + "-fx-font-size: 12px;"
             + "-fx-padding: 8 16 8 16;"
             + "-fx-background-radius: 6;"
             + "-fx-cursor: hand;";
    }

    // crude but effective: pull each RGB channel ~15% toward black
    private static String darken(String hex) {
        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);
        r = (int) (r * 0.85);
        g = (int) (g * 0.85);
        b = (int) (b * 0.85);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    // colour used for a status word such as "available" / "approved" / "rejected"
    public static String statusColour(String status) {
        if (status == null) return NEUTRAL;
        switch (status.toLowerCase()) {
            case "available":
            case "approved":
                return SUCCESS;
            case "rejected":
                return DANGER;
            case "pending":
                return AMBER;
            default:
                return NEUTRAL;
        }
    }

    public static String textField() {
        return "-fx-background-color: white;"
             + "-fx-border-color: " + BORDER + ";"
             + "-fx-border-radius: 6;"
             + "-fx-background-radius: 6;"
             + "-fx-padding: 7;";
    }

    public static String card() {
        return "-fx-background-color: " + CARD + ";"
             + "-fx-background-radius: 10;"
             + "-fx-border-color: " + BORDER + ";"
             + "-fx-border-radius: 10;";
    }
}
