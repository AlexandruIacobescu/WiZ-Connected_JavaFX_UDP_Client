package com.wizclient.wizconnectedclient.classes;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Message {
    private final Label label;
    private final long showDuration;
    private final String message;
    private final Color color;

    public Message(Label label, long showDuration, String message, Color color) {
        this.label = label;
        this.showDuration = showDuration;
        this.message = message;
        this.color = color;
    }

    public void show() {
        Platform.runLater(() -> {
            label.setTextFill(color);
            label.setText(message);
            label.setVisible(true);

            // Schedule a task to hide the label after the specified duration
            new Thread(() -> {
                try {
                    Thread.sleep(showDuration);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Platform.runLater(() -> {
                    label.setVisible(false);
                });
            }).start();
        });
    }
}
