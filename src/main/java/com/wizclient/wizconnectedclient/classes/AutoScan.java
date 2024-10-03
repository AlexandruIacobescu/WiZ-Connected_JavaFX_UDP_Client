package com.wizclient.wizconnectedclient.classes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AutoScan {
    public static String scanHost(String host, int port, String payload, int timeout) {
        DatagramSocket udpSocket = null;

        try {
            udpSocket = new DatagramSocket();
            udpSocket.setSoTimeout(timeout);

            InetAddress address = InetAddress.getByName(host);
            byte[] data = payload.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);
            udpSocket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            udpSocket.receive(receivePacket);

            return host;
        } catch (IOException e) {
            return null;
        } finally {
            if (udpSocket != null) {
                udpSocket.close();
            }
        }
    }

    public static List<String> scanNetwork(int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.connect(InetAddress.getByName("8.8.8.8"), 80);
        String localIP = socket.getLocalAddress().getHostAddress();
        socket.close();

        String[] ipParts = localIP.split("\\.");
        String payload = "{\"method\":\"registration\",\"params\":{\"register\":true}}";

        List<String> activeHosts = new ArrayList<>();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(254); // Adjust as needed
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 1; i <= 254; i++) { // Adjust the range as needed
            String host = String.format("%s.%s.%s.%d", ipParts[0], ipParts[1], ipParts[2], i);
            Future<String> future = executor.submit(() -> scanHost(host, port, payload, 2000));
            futures.add(future);
        }

        for (Future<String> future : futures) {
            try {
                String result = future.get();
                if (result != null) {
                    activeHosts.add(result);
                }
            } catch (Exception e) {
                // Handle any exceptions if necessary
                e.printStackTrace();
            }
        }

        executor.shutdown();
        return activeHosts;
    }

    public static void main(String[] args) throws IOException {
        int portToScan = 38899; // Change this to the desired port
        List<String> activeHosts = scanNetwork(portToScan);
        for (String host : activeHosts) {
            System.out.println(host);
        }
    }
}