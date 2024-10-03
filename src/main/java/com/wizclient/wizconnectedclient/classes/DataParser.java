package com.wizclient.wizconnectedclient.classes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataParser {
    public static Map<String,String> getLightsFromJson(String path) {
        File lightsJson = new File(path);
        ObjectMapper mapper = new ObjectMapper();
        Map<String,String> ligthsMap = null;
        try{
            ligthsMap = mapper.readValue(lightsJson, new TypeReference<>() {
            });
        }catch (Exception e){
            System.out.println("\033[31mError parsing lights from: " + path);
            e.printStackTrace();
        }
        return ligthsMap;
    }

    public static Map<String,Boolean> getSettingsFromJson(String path){
        File settingsJson = new File(path);
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Boolean> settingsMap = null;
        try{
            settingsMap = mapper.readValue(settingsJson, new TypeReference<>() {
            });
        }catch(Exception ex){
            System.out.println("\033[31mError parsing settings from: " + path);
            ex.printStackTrace();
        }
        return settingsMap;
    }

    public static Map<String, Object> getStateResultMap(String jsonString) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        // Extract the "result" field from the JSON
        JsonNode resultNode = rootNode.get("result");

        if (resultNode != null && resultNode.isObject()) {
            // Convert the "result" field to a Map
            resultMap = convertJsonNodeToMap(resultNode);
        }

        return resultMap;
    }

    private static Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
        Map<String, Object> map = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode valueNode = entry.getValue();

            if (valueNode.isObject()) {
                // Recursively convert nested objects
                map.put(key, convertJsonNodeToMap(valueNode));
            } else if (valueNode.isArray()) {
                // Handle arrays if needed
                // In this example, we assume arrays are not present in the provided JSON
            } else {
                // Add the simple key-value pair
                map.put(key, valueNode.asText());
            }
        }

        return map;
    }



    public static void main(String[] args) throws Exception {
        String hostIp = "192.168.0.107";

    }
}