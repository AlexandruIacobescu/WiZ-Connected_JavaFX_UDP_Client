package com.wizclient.wizconnectedclient.classes;

import javafx.scene.control.CheckBox;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

import static com.wizclient.wizconnectedclient.classes.DataParser.getStateResultMap;

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

    public static String getState(String hostIp, int port) throws Exception {
        String payload = "{\"method\": \"getPilot\",\"params\":{}}";
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(1000); // Set timeout to 5 seconds

        try {
            InetAddress serverAddress = InetAddress.getByName(hostIp);
            byte[] sendData = payload.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
            socket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (java.net.SocketTimeoutException e) {
            throw new Exception("Timeout: No response received within 1 second.");
        } finally {
            socket.close();
        }
    }

    public static boolean isLightOn(String hostIp, int port) throws Exception {
        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);

        for(var key : stateResult.keySet()){
            if(key.equals("state"))
                return stateResult.get(key).equals("true");
        }
        return false;
    }

    public static boolean isLightInColorMode(String hostIp, int port) throws Exception {
        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);
        return stateResult.containsKey("r");
    }

    public static int[] getCurrentStateColorRGBW(String hostIp, int port) throws Exception {
        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);

        int r = Integer.parseInt((String) stateResult.get("r"));
        int g = Integer.parseInt((String) stateResult.get("g"));
        int b = Integer.parseInt((String) stateResult.get("b"));
        int w = Integer.parseInt((String) stateResult.get("w"));

        return new int[] {r, g, b, w};
    }

    public static boolean isLightInTemperatureMode(String hostIp, int port) throws Exception {
        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);
        return stateResult.containsKey("temp");
    }

    public static int getCurrentStateTemperatureKelvins(String hostIp, int port) throws Exception {
        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);

        int k = Integer.parseInt((String) stateResult.get("temp"));
        if(k % 100 != 0) {
            if (k % 100 < 50)
                k = k / 100 * 100;
            else {
                k /= 100;
                k++;
                k *= 100;
            }
        }

        return k;
    }

    public static boolean isLightInSceneMode(String hostIp, int port) throws Exception {
        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);

        return !stateResult.get("sceneId").equals("0");
    }

    public static boolean isLightInStaticSceneMode(String hostIp, int port) throws Exception {
        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);

        if(stateResult.get("sceneId").equals("0"))
            return false;

        int sceneId = Integer.parseInt((String) stateResult.get("sceneId"));

        switch(sceneId){
            case 29, 35, 5, 9, 10, 14, 6, 17, 16, 15, 18, 19 -> { return true; }
            default -> { return false; }
        }
    }

    public static boolean isLightInDynamicSceneMode(String hostIp, int port) throws Exception {
        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);

        if(stateResult.get("sceneId").equals("0"))
            return false;

        int sceneId = Integer.parseInt((String) stateResult.get("sceneId"));

        switch(sceneId){
            case 31, 30, 32, 5, 22, 26, 3, 2, 4, 8, 20, 21, 7, 24, 25, 1, 23, 27, 33, 28 -> { return true; }
            default -> { return false; }
        }
    }

    public static int getCurrentStateSceneId(String hostIp, int port) throws Exception {
        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);

        return Integer.parseInt((String)stateResult.get("sceneId"));
    }

    public static int getCurrentStateSpeed(String hostIp, int port) throws Exception {
        if(isLightInStaticSceneMode(hostIp, port))
            throw new Exception("The light is not in a dynamic scene mode.");

        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);

        return Integer.parseInt((String)stateResult.get("speed"));
    }

    public static int getCurrentStateBrightness(String hostIp, int port) throws Exception {
        String state = getState(hostIp, port);
        Map<String,Object> stateResult = getStateResultMap(state);

        return Integer.parseInt((String)stateResult.get("dimming"));
    }

    public static void main(String[] args) throws Exception {
        String ip = "192.168.0.107";
//        Map<String,Object> stateResult = getStateResultMap(getState("192.168.0.107", Functions.DEFAULT_PORT));
//
//        for(var k : stateResult.keySet()){
//            System.out.printf("%s: %s\n", k, stateResult.get(k));
//        }
//
//        System.out.println(getCurrentStateBrightness(ip, Functions.DEFAULT_PORT));

        try{
            isLightOn(ip, DEFAULT_PORT);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
