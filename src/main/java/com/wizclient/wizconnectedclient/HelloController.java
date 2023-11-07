package com.wizclient.wizconnectedclient;

import com.wizclient.wizconnectedclient.classes.Message;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloController implements Initializable {
    private HashMap<String,String> cBoxItem_Ip = new HashMap<>();

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

    public String getAlias(String expression){
        String regex = "\\[(.*?)]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression);

        if (matcher.find()) {
            return matcher.group(1); // Group 1 contains the captured alias
        } else {
            return null; // Return null if no alias is found
        }
    }

    public void btnEditClick(ActionEvent e) throws IOException {
        if(cBoxSelectLight.getValue() != null){
            String selectedItem = cBoxSelectLight.getValue();
            // TODO: impl.



            Stage modalStage = new Stage();
            modalStage.initOwner(btnEdit.getScene().getWindow()); // Set the owner stage
            modalStage.initModality(Modality.APPLICATION_MODAL); // Set the modality

            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditLightDialog.fxml"));
            Parent root;
            try {
                root = loader.load();
                EditLightDialogController editLightDialogController = loader.getController();
                editLightDialogController.getHashMap(cBoxItem_Ip);
                editLightDialogController.getComboBox(cBoxSelectLight);
                editLightDialogController.initIpTextField(cBoxItem_Ip.get(cBoxSelectLight.getValue()));
                editLightDialogController.initAliasTextField(getAlias(cBoxSelectLight.getValue()));
                modalStage.initStyle(StageStyle.UTILITY);
                modalStage.setTitle("Edit Light");
                modalStage.setScene(new Scene(root));
                modalStage.showAndWait(); // Show the modal stage and block access until it's closed

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else{
            Message msg = new Message(lblEditLightMessage, 2000, "No light source selected.", Color.RED);
            msg.show();
        }
    }

    public void btnRemoveClick(){
        if(cBoxSelectLight.getValue() != null){
            String selectedItem = cBoxSelectLight.getValue();
            cBoxItem_Ip.remove(selectedItem);
            cBoxSelectLight.getItems().remove(selectedItem);
            Message msg = new Message(lblEditLightMessage, 2000, "Item remove successfully.", Color.GREEN);
            msg.show();
        }else{
            Message msg = new Message(lblEditLightMessage, 2000, "No light source selected.", Color.RED);
            msg.show();
        }
    }

    public void btnRemoveAllClick(){
        if(!cBoxSelectLight.getItems().isEmpty()){
            cBoxSelectLight.getItems().clear();
            cBoxItem_Ip.clear();
            Message msg = new Message(lblEditLightMessage, 2000, "All items removed successfully.", Color.GREEN);
            msg.show();
        }
        else{
            Message msg = new Message(lblEditLightMessage, 2000, "No items currently added.", Color.ORANGE);
            msg.show();
        }
    }

}