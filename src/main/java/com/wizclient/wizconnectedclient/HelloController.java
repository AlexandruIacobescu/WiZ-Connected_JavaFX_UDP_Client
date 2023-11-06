package com.wizclient.wizconnectedclient;

import com.wizclient.wizconnectedclient.classes.Message;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloController implements Initializable {

    @FXML
    private TitledPane tpAddNew, tpEdit, tpAutoscan;

    @FXML
    private Slider tempTabBrightnessSlider;

    @FXML
    private TextField tfTempTabBrgValue, tfIp, tfAlias;

    @FXML
    private Button btnAdd, btnRemove, btnRemoveAll, btnEdit, btnAddAll, btnAddSelected, btnAddWithAlias, btnScan;

    @FXML
    private Label tempTabMsgLabel, lblAddLightMessage, lblEditLightMessage;

    @FXML
    private ComboBox<String> cBoxSelectLight;

    @FXML
    public void sliderScroll(Event event) {
        if (event.getEventType() == javafx.scene.input.ScrollEvent.SCROLL) {
            javafx.scene.input.ScrollEvent scrollEvent = (javafx.scene.input.ScrollEvent) event;

            double minorTickUnit = tempTabBrightnessSlider.getMinorTickCount();
            double newValue = tempTabBrightnessSlider.getValue();

            // Adjust the value based on scroll direction and minor tick unit
            if (scrollEvent.getDeltaY() > 0) {
                newValue = Math.min(tempTabBrightnessSlider.getMax(), newValue + minorTickUnit);
            } else if (scrollEvent.getDeltaY() < 0) {
                newValue = Math.max(tempTabBrightnessSlider.getMin(), newValue - minorTickUnit);
            }

            tempTabBrightnessSlider.setValue(newValue);
            tempTabBrightnessSliderDragDetected();
        }
    }

    public void tfTempTabBrgValueTextChanged() {
        try {
            int value = Integer.parseInt(tfTempTabBrgValue.getText());
            if (value < 10 || value > 100) {
                Message msg = new Message(tempTabMsgLabel, 2000, "Only integers in [10, 100] permitted for this field", Color.RED);
                msg.show();
            } else {
                tempTabBrightnessSlider.setValue(value);
            }
        } catch (NumberFormatException ex) {
            Message msg = new Message(tempTabMsgLabel, 2000, "Only integers in [10, 100] permitted for this field", Color.RED);
            msg.show();
        }
    }

    public void tempTabBrightnessSliderDragDetected() {
        tfTempTabBrgValue.setText(Integer.toString((int) tempTabBrightnessSlider.getValue()));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tpAddNew.setCollapsible(false);
        tpEdit.setCollapsible(false);
        tpAutoscan.setCollapsible(false);

        tfTempTabBrgValue.setText(Integer.toString((int) tempTabBrightnessSlider.getValue()));
    }

    HashMap<String,String> cBoxItem_Ip = new HashMap<>();

    public void btnAddClick(){
        String ipAddress = tfIp.getText();
        String regex = "^(10\\.(\\d{1,3}\\.){2}\\d{1,3}|172\\.(1[6-9]|2\\d|3[0-1])\\.(\\d{1,3}\\.){1,2}\\d{1,3}|192\\.168(\\.\\d{1,3}){2})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ipAddress);

        if (matcher.matches()) {
            String item = String.format("[%s] [%s]", tfAlias.getText(), tfIp.getText());
            if(!cBoxItem_Ip.containsKey(item) && !cBoxItem_Ip.containsValue(tfIp.getText())) {
                cBoxItem_Ip.put(item, tfIp.getText());
                cBoxSelectLight.getItems().add(item);
                Message msg = new Message(lblAddLightMessage, 2000, "Light added successfully.", Color.GREEN);
                msg.show();
            }else{
                Message msg = new Message(lblAddLightMessage, 2000, "Light already in the list.", Color.RED);
                msg.show();
            }
        } else {
            Message msg = new Message(lblAddLightMessage, 2000, "Not a Valid IP address.", Color.RED);
            msg.show();
        }
    }

    public void btnEditClick(){
        if(cBoxSelectLight.getValue() != null){
            String selectedItem = cBoxSelectLight.getValue();

        }else{
            Message msg = new Message(lblEditLightMessage, 2000, "No light source selected.", Color.RED);
            msg.show();
        }
    }

}