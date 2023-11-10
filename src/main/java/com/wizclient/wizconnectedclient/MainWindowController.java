package com.wizclient.wizconnectedclient;

import com.wizclient.wizconnectedclient.classes.AutoScan;
import com.wizclient.wizconnectedclient.classes.DataParser;
import com.wizclient.wizconnectedclient.classes.Functions;
import com.wizclient.wizconnectedclient.classes.Message;
import javafx.application.Platform;
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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainWindowController implements Initializable {
    private HashMap<String,String> cBoxItem_Ip = new HashMap<>();

    @FXML
    private TitledPane tpAddNew, tpEdit, tpAutoscan;

    @FXML
    private Slider tempTabBrightnessSlider, tempSlider;

    @FXML
    private TextField tfTempTabBrgValue, tfIp, tfAlias, tempTabTempTextField;

    @FXML
    private Button btnAdd, btnRemove, btnRemoveAll, btnEdit, btnAddAll, btnAddSelected, btnAddWithAlias, btnScan, tempTabSetButton, tempTabTurnOffButton, tempTabTurnOnButton;

    @FXML
    private Label tempTabMsgLabel, lblAddLightMessage, lblEditLightMessage, lblAutoScanMessage;

    @FXML
    public ComboBox<String> cBoxSelectLight, tempTabSelectedLightComboBox;

    @FXML
    public ListView<String> listViewFoundLights;

    @FXML
    public void brightnessSliderScroll(Event event) {
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

    @FXML
    public void tempSliderScroll(Event event){
        if (event.getEventType() == javafx.scene.input.ScrollEvent.SCROLL) {
            javafx.scene.input.ScrollEvent scrollEvent = (javafx.scene.input.ScrollEvent) event;

            double minorTickUnit = tempSlider.getMinorTickCount();
            double newValue = tempSlider.getValue();

            // Adjust the value based on scroll direction and minor tick unit
            if (scrollEvent.getDeltaY() > 0) {
                newValue = Math.min(tempSlider.getMax(), newValue + minorTickUnit);
            } else if (scrollEvent.getDeltaY() < 0) {
                newValue = Math.max(tempSlider.getMin(), newValue - minorTickUnit);
            }

            tempSlider.setValue(newValue);
            tempTabTempSliderDragDetected();
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

    public void tempTabTempSliderDragDetected(){
        tempTabTempTextField.setText(Integer.toString((int) tempSlider.getValue() * 100));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tpAddNew.setCollapsible(false);
        tpEdit.setCollapsible(false);
        tpAutoscan.setCollapsible(false);

        tfTempTabBrgValue.setText(Integer.toString((int) tempTabBrightnessSlider.getValue()));
        tempTabTempTextField.setText(Integer.toString((int) tempSlider.getValue() * 100));

        Map<String,String> lights = DataParser.getLightsFromJson("data\\lights.json");
        for(var key : lights.keySet()){
            String cBoxItem = String.format("[%s] [%s]", lights.get(key), key);
            cBoxItem_Ip.put(cBoxItem,key);
            cBoxSelectLight.getItems().add(cBoxItem);
        }
        updateAllComboBoxes(cBoxSelectLight);
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
                tempTabSelectedLightComboBox.getItems().add(item);
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

    public void updateAllComboBoxes(ComboBox<String> comboBox){
        tempTabSelectedLightComboBox.getItems().clear();
        tempTabSelectedLightComboBox.getItems().addAll(comboBox.getItems());
    }

    public void btnEditClick(ActionEvent e) throws IOException {
        if(cBoxSelectLight.getValue() != null){
            String selectedItem = cBoxSelectLight.getValue();
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
                // TODO: update other tabs' comboboxes
                updateAllComboBoxes(cBoxSelectLight);
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
            tempTabSelectedLightComboBox.getItems().remove(selectedItem);
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
            tempTabSelectedLightComboBox.getItems().clear();
            cBoxItem_Ip.clear();
            Message msg = new Message(lblEditLightMessage, 2000, "All items removed successfully.", Color.GREEN);
            msg.show();
        }
        else{
            Message msg = new Message(lblEditLightMessage, 2000, "No items currently added.", Color.ORANGERED);
            msg.show();
        }
    }

    public void btnScanClick() {
        Platform.runLater(() -> {
            listViewFoundLights.getItems().clear();
            lblAutoScanMessage.setVisible(true);
            lblAutoScanMessage.setTextFill(Color.DARKORANGE);
            lblAutoScanMessage.setText("Scanning...");
            Thread thread = new Thread(() -> {
                try {
                    listViewFoundLights.getItems().addAll(AutoScan.scanNetwork(Functions.DEFAULT_PORT));
                    new Message(lblAutoScanMessage, 2000, "Scanning completed.", Color.GREEN).show();
                } catch (IOException e) {
                    lblAutoScanMessage.setVisible(false);
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        });
    }

    public void btnAddAllClick(){
        boolean existingFound = false;
        List<String> items = listViewFoundLights.getItems();
        List<String> removableItems = new ArrayList<>();
        if(listViewFoundLights.getItems() != null){
            for(var item : items){
                if(!cBoxItem_Ip.containsValue(item)){
                    cBoxItem_Ip.put(String.format("[%s] [%s]", "", item), item);
                    cBoxSelectLight.getItems().add(String.format("[%s] [%s]", "", item));
                    removableItems.add(item);
                }
                else{
                    existingFound = true;
                }
            }
            updateAllComboBoxes(cBoxSelectLight);
            if(existingFound){
                new Message(lblAutoScanMessage, 2000, "Lights already added were skipped.", Color.ORANGERED).show();
            }
            for(var item : removableItems){
                listViewFoundLights.getItems().remove(item);
            }
        }
    }

    public void setBtnAddSelectedClick(){
        boolean existingFound = false;
        List<String> items = listViewFoundLights.getSelectionModel().getSelectedItems();
        List<String> removableItems = new ArrayList<>();
        if(listViewFoundLights.getItems() != null){
            for(var item : items){
                if(!cBoxItem_Ip.containsValue(item)){
                    cBoxItem_Ip.put(String.format("[%s] [%s]", "", item), item);
                    cBoxSelectLight.getItems().add(String.format("[%s] [%s]", "", item));
                    removableItems.add(item);
                }
                else{
                    existingFound = true;
                }
            }
            updateAllComboBoxes(cBoxSelectLight);
            if(existingFound){
                new Message(lblAutoScanMessage, 2000, "Lights already added were skipped.", Color.ORANGERED).show();
            }
            for(var item : removableItems){
                listViewFoundLights.getItems().remove(item);
            }
        }
    }

    // ----------- TEMPERATURE TAB -----------

    public void tempTabTempTextFieldTextChanged(){
        try {
            int value = Integer.parseInt(tempTabTempTextField.getText());
            if (value < 2200 || value > 6500 || value % 100 != 0) {
                Message msg = new Message(tempTabMsgLabel, 3800, "Only integers multiple of 100 in [2200, 6500] permitted for this field.", Color.RED);
                msg.show();
            } else {
                tempSlider.setValue((double) value / 100);
            }
        } catch (NumberFormatException ex) {
            Message msg = new Message(tempTabMsgLabel, 3800, "Only integers multiple of 100 in [2200, 6500] permitted for this field.", Color.RED);
            msg.show();
        }
    }

    public void tempSetButtonClick(){
        if(tempTabSelectedLightComboBox.getValue() != null){
            String ip = cBoxItem_Ip.get(tempTabSelectedLightComboBox.getValue());
            Functions.setTemp(ip, Functions.DEFAULT_PORT, ((int) tempSlider.getValue() * 100));
            Functions.setBrightness(ip, Functions.DEFAULT_PORT, (int) tempTabBrightnessSlider.getValue());
        }else{
            new Message(tempTabMsgLabel, 2000, "No light source selected.", Color.RED).show();
        }
    }

    public void tempTabTurnOffButtonClick(){
        if(tempTabSelectedLightComboBox.getValue() != null){
            String ip = cBoxItem_Ip.get(tempTabSelectedLightComboBox.getValue());
            Functions.turnOff(ip, Functions.DEFAULT_PORT);
        }else{
            new Message(tempTabMsgLabel, 2000, "No light source selected.", Color.RED).show();
        }
    }

    public void tempTabTurnOnButtonClick(){
        if(tempTabSelectedLightComboBox.getValue() != null){
            String ip = cBoxItem_Ip.get(tempTabSelectedLightComboBox.getValue());
            Functions.turnOn(ip, Functions.DEFAULT_PORT);
        }else{
            new Message(tempTabMsgLabel, 2000, "No light source selected.", Color.RED).show();
        }
    }
}