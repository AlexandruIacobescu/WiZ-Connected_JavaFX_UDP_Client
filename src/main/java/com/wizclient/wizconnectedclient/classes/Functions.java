package com.wizclient.wizconnectedclient.classes;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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


}
