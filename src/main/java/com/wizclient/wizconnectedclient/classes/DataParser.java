package com.wizclient.wizconnectedclient.classes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;

import java.io.File;
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

    public static void main(String[] args) {
//        Map<String,String> lights = getLightsFromJson("data\\lights.json");
//        for(var key : lights.keySet()){
//            System.out.println(key + " : " + lights.get(key));
//        }
        Map<String,Boolean> settings = getSettingsFromJson("data\\settings.json");
        for(var key : settings.keySet()){
            System.out.println(key + " : " + settings.get(key));
        }
    }
}
