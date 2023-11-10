package com.wizclient.wizconnectedclient.classes;

import javafx.scene.control.CheckBox;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

public class Functions {

    public static final int DEFAULT_PORT = 38899;

    private static void sendUdpPayload(String host, int port, String payload) {
        DatagramSocket udpSocket = null;
        try {
            // Create a UDP socket
            udpSocket = new DatagramSocket();

            // Convert the payload to bytes
            byte[] payloadBytes = payload.getBytes();

            // Create a DatagramPacket to send the payload
            InetAddress destinationAddress = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(payloadBytes, payloadBytes.length, destinationAddress, port);

            // Send the packet
            udpSocket.send(packet);

            System.out.println("Payload sent to " + host + ":" + port + ": " + payload);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());

        } finally {
            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.close();
            }
        }
    }

    // ----------- LIGHT ADJUSTMENT FUNCTIONS -----------
    public static void setBrightness(String host, int port, int brightness){
        if(brightness < 10 || brightness > 100){
            throw new IllegalArgumentException("brightness value must be integer in [10, 100].");
        }
        String payload = String.format("{\"method\":\"setPilot\",\"params\":{\"state\":true,\"dimming\":%d}}", brightness);
        sendUdpPayload(host, port, payload);
    }

    public static void setRGBW(String host, int port, int r, int g, int b, int w){
        if(!((r >= 1 && r <= 255) && (g >= 1 && g <= 255) && (b >= 1 && b <= 255))){
            throw new IllegalArgumentException("all R.G.B. values must be integers in [1, 255].");
        }
        String payload = String.format("{\"method\":\"setPilot\",\"params\":{\"state\":true,\"r\":%d,\"g\":%d,\"b\":%d,\"w\":%d}}", r, g, b, w);
        sendUdpPayload(host, port, payload);
    }

    public static void setTemp(String host, int port, int temperature){
        if(temperature < 2200 || temperature > 6500 || temperature % 100 != 0){
            throw new IllegalArgumentException("Temperature argument must be integer multiple of 100 in [2200, 6500]. given: " + temperature);
        }
        String payload = String.format("{\"method\":\"setPilot\",\"params\":{\"state\":true,\"temp\":%d}}", temperature);
        sendUdpPayload(host, port, payload);
    }

    public static void turnOff(String host, int port){
        String payload = "{\"method\":\"setPilot\",\"params\":{\"state\":false}}";
        sendUdpPayload(host, port, payload);
    }

    public static void turnOn(String host, int port){
        String payload = "{\"method\":\"setPilot\",\"params\":{\"state\":true}}";
        sendUdpPayload(host, port, payload);
    }

    // ----------- CONTROLLER MISC FUNCTIONS -----------

    public static boolean checkIfSettingsHaveChanged(Map<String,Boolean> settings, CheckBox ... checkBoxesInOrder){
        Map<String,Boolean> defaults = DataParser.getSettingsFromJson("data\\default-settings.json");
        for(var key : defaults.keySet()){
            switch(key){
                case "preserve_lights_on_exit" -> {
                    if(checkBoxesInOrder[0].isSelected() != defaults.get(key))
                        return true;
                }
                case "adjust_lights_state_automatically" -> {
                    if(checkBoxesInOrder[1].isSelected() != defaults.get(key))
                        return true;
                }
            }
        }
        return false;
    }
}
