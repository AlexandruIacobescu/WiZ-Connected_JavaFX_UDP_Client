package com.wizclient.wizconnectedclient.classes;

import javafx.scene.control.CheckBox;

import java.util.Map;

public class Utils {
    public static boolean checkIfSettingsHaveChanged(Map<String,Boolean> settings, CheckBox... checkBoxesInOrder){
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
