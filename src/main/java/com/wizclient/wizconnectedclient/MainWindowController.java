package com.wizclient.wizconnectedclient;

import com.wizclient.wizconnectedclient.classes.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainWindowController implements Initializable {
    private HashMap<String,String> cBoxItem_Ip = new HashMap<>();
    private Map<String,Boolean> settings, pendingSettings;
    private Map<String,String> lights;

    @FXML
    private TitledPane tpAddNew, tpEdit, tpAutoscan;

    @FXML
    private TabPane tabPane;

    @FXML
    private Slider tempTabBrightnessSlider, tempSlider, colorTabBrightnessSlider, rSlider, bSlider, gSlider, wSlider;

    @FXML
    private TextField tempTabBrgValueTextField, tfIp, tfAlias, tempTabTempTextField, colorTabBrgValueTextField, rTextField, gTextField, bTextField, wTextField;

    @FXML
    private Button btnAdd, btnRemove, btnRemoveAll, btnEdit, btnAddAll, btnAddSelected, btnScan, tempTabSetButton, tempTabTurnOffButton, tempTabTurnOnButton, settingsTabSaveButton, settingsTabDefaultsButton;

    @FXML
    private Label tempTabMsgLabel, lblAddLightMessage, lblEditLightMessage, lblAutoScanMessage, settingsTabMessageLabel, warmestLabel, warmlabel, daylightLabel, coolLabel, tempTabCurrentStateLabel, colorTabMessageLabel, colorTabCurrentStateLabel;

    @FXML
    public ComboBox<String> cBoxSelectLight, tempTabSelectedLightComboBox, colorTabSelectedLightComboBox;

    @FXML
    public ListView<String> listViewFoundLights;

    @FXML
    public CheckBox persistLightsCheckBox, automaticAdjustmentCheckBox;

    @FXML
    public Rectangle rectangle;

    public void tabPaneTabClick() throws Exception {
        int tabsCount = tabPane.getTabs().size();
        for (int i = 0; i <= tabsCount; i++) {
            boolean isSelected = tabPane.getSelectionModel().isSelected(i);
            if(isSelected){
                switch (i){
                    case 0 -> {
                        if(tempTabSelectedLightComboBox.getValue() != null) {
                            String ip = cBoxItem_Ip.get(tempTabSelectedLightComboBox.getValue());
                            try {
                                tempTabBrightnessSlider.setValue(Functions.getCurrentStateBrightness(ip, Functions.DEFAULT_PORT));
                                tempTabBrgValueTextField.setText(String.valueOf((int) tempTabBrightnessSlider.getValue()));
                            }catch(Exception ignored){
                                tempTabCurrentStateLabel.setTextFill(Color.DARKORANGE);
                                tempTabCurrentStateLabel.setText("UNKNOWN");
                                tempTabCurrentStateLabel.setVisible(true);
                            }
                            return;
                        }
                    }
                    case 1 -> {
                        if(colorTabSelectedLightComboBox.getValue() != null) {
                            String ip = cBoxItem_Ip.get(colorTabSelectedLightComboBox.getValue());
                            try {
                                colorTabBrightnessSlider.setValue(Functions.getCurrentStateBrightness(ip, Functions.DEFAULT_PORT));
                                colorTabBrgValueTextField.setText(String.valueOf((int) colorTabBrightnessSlider.getValue()));
                            }catch(Exception ignored){
                                tempTabCurrentStateLabel.setTextFill(Color.DARKORANGE);
                                tempTabCurrentStateLabel.setText("UNKNOWN");
                                tempTabCurrentStateLabel.setVisible(true);
                            }
                            return;
                        }
                    }
                    case 2 -> {

                    }
                    case 3 -> {

                    }
                }
            }
        }
    }

    @FXML
    public void tempTabBrightnessSliderScroll(Event event) {
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
    public void colorTabBrightnessSliderScroll(Event event) {
        if (event.getEventType() == javafx.scene.input.ScrollEvent.SCROLL) {
            javafx.scene.input.ScrollEvent scrollEvent = (javafx.scene.input.ScrollEvent) event;

            double minorTickUnit = colorTabBrightnessSlider.getMinorTickCount();
            double newValue = colorTabBrightnessSlider.getValue();

            // Adjust the value based on scroll direction and minor tick unit
            if (scrollEvent.getDeltaY() > 0) {
                newValue = Math.min(colorTabBrightnessSlider.getMax(), newValue + minorTickUnit);
            } else if (scrollEvent.getDeltaY() < 0) {
                newValue = Math.max(colorTabBrightnessSlider.getMin(), newValue - minorTickUnit);
            }

            colorTabBrightnessSlider.setValue(newValue);
            colorTabBrightnessSliderDragDetected();
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

    public void tempTabBrgValueTextFieldTextChanged() {
        try {
            int value = Integer.parseInt(tempTabBrgValueTextField.getText());
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
        tempTabBrgValueTextField.setText(Integer.toString((int) tempTabBrightnessSlider.getValue()));
    }

    public void colorTabBrightnessSliderDragDetected() {
        colorTabBrgValueTextField.setText(Integer.toString((int) colorTabBrightnessSlider.getValue()));
    }

    public void tempTabTempSliderDragDetected(){
        tempTabTempTextField.setText(Integer.toString((int) tempSlider.getValue() * 100));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tpAddNew.setCollapsible(false);
        tpEdit.setCollapsible(false);
        tpAutoscan.setCollapsible(false);

        tempTabBrgValueTextField.setText(Integer.toString((int) tempTabBrightnessSlider.getValue()));
        tempTabTempTextField.setText(Integer.toString((int) tempSlider.getValue() * 100));
        colorTabBrgValueTextField.setText(String.valueOf((int)colorTabBrightnessSlider.getValue()));

        lights = DataParser.getLightsFromJson("data\\lights.json");
        for(var key : lights.keySet()){
            String cBoxItem = String.format("[%s] [%s]", lights.get(key), key);
            cBoxItem_Ip.put(cBoxItem,key);
            cBoxSelectLight.getItems().add(cBoxItem);
        }
        updateAllComboBoxes(cBoxSelectLight);

        settings = DataParser.getSettingsFromJson("data\\settings.json");
        for(var key : settings.keySet()){
            switch (key){
                case "preserve_lights_on_exit" -> {
                    if(settings.get(key)){
                        persistLightsCheckBox.setSelected(true);
                    }
                }
                case "adjust_lights_state_automatically" -> {
                    if(settings.get(key)){
                        automaticAdjustmentCheckBox.setSelected(true);
                    }
                }
            }
        }
        pendingSettings = settings;

        rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
    }



    public void btnAddClick(){
        String ipAddress = tfIp.getText();
        String regex = "^(10\\.(\\d{1,3}\\.){2}\\d{1,3}|172\\.(1[6-9]|2\\d|3[0-1])\\.(\\d{1,3}\\.){1,2}\\d{1,3}|192\\.168(\\.\\d{1,3}){2})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ipAddress);

        if (matcher.matches()) {
            try{
                Functions.isLightOn(tfIp.getText(), Functions.DEFAULT_PORT);
            }catch (Exception ignored){
                new Message(lblAddLightMessage, 3000, "Could not connect to this light.", Color.RED).show();
                return;
            }
            String item = String.format("[%s] [%s]", tfAlias.getText(), tfIp.getText());
            if(!cBoxItem_Ip.containsKey(item) && !cBoxItem_Ip.containsValue(tfIp.getText())) {
                cBoxItem_Ip.put(item, tfIp.getText());
                cBoxSelectLight.getItems().add(item);
                updateAllComboBoxes(cBoxSelectLight);
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
        colorTabSelectedLightComboBox.getItems().clear();
        colorTabSelectedLightComboBox.getItems().addAll(comboBox.getItems());
        // TODO: update other tabs' combo boxes
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
            updateAllComboBoxes(cBoxSelectLight);
            Message msg = new Message(lblEditLightMessage, 2000, "Item removed successfully.", Color.GREEN);
            msg.show();
            tempTabCurrentStateLabel.setVisible(false);
        }else{
            Message msg = new Message(lblEditLightMessage, 2000, "No light source selected.", Color.RED);
            msg.show();
        }
    }

    public void btnRemoveAllClick(){
        if(!cBoxSelectLight.getItems().isEmpty()){
            cBoxSelectLight.getItems().clear();
            updateAllComboBoxes(cBoxSelectLight);
            cBoxItem_Ip.clear();
            Message msg = new Message(lblEditLightMessage, 2000, "All items removed successfully.", Color.GREEN);
            msg.show();
            tempTabCurrentStateLabel.setVisible(false);
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
        boolean couldNotConnectToSomeLights = false;
        List<String> items = listViewFoundLights.getItems();
        List<String> removableItems = new ArrayList<>();
        if(listViewFoundLights.getItems() != null){
            for(var item : items){
                if(!cBoxItem_Ip.containsValue(item)){
                    try{
                        Functions.isLightOn(item, Functions.DEFAULT_PORT);
                    }catch (Exception ignored){
                        couldNotConnectToSomeLights = true;
                    }
                    cBoxItem_Ip.put(String.format("[%s] [%s]", "", item), item);
                    cBoxSelectLight.getItems().add(String.format("[%s] [%s]", "", item));
                    removableItems.add(item);
                }
                else{
                    existingFound = true;
                }
            }
            updateAllComboBoxes(cBoxSelectLight);
            if(existingFound && !couldNotConnectToSomeLights){
                new Message(lblAutoScanMessage, 2000, "Lights already added were skipped.", Color.ORANGERED).show();
            }
            if(!existingFound && couldNotConnectToSomeLights){
                new Message(lblAutoScanMessage, 3000, "Could not connect to some lights. Skipped.", Color.ORANGERED).show();
            }
            if(existingFound && couldNotConnectToSomeLights){
                new Message(lblAutoScanMessage, 3000, "Lights already added were skipped.\nCould not connect to some lights. Skipped.", Color.RED).show();
            }
            for(var item : removableItems){
                listViewFoundLights.getItems().remove(item);
            }
        }
    }

    public void setBtnAddSelectedClick(){
        boolean existingFound = false;
        boolean couldNotConnectToSomeLights = false;
        List<String> items = listViewFoundLights.getSelectionModel().getSelectedItems();
        List<String> removableItems = new ArrayList<>();
        if(listViewFoundLights.getItems() != null){
            for(var item : items){
                try{
                    Functions.isLightOn(item, Functions.DEFAULT_PORT);
                }catch(Exception ignored){
                    couldNotConnectToSomeLights = true;
                }
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
            if(existingFound && !couldNotConnectToSomeLights){
                new Message(lblAutoScanMessage, 2000, "Light already added. Skipped.", Color.ORANGERED).show();
            }
            if(!existingFound && couldNotConnectToSomeLights){
                new Message(lblAutoScanMessage, 3000, "Could not connect to this light. Skipped.", Color.ORANGERED).show();
            }
            if(existingFound && couldNotConnectToSomeLights){
                new Message(lblAutoScanMessage, 3000, "Light already added. skipped.\nCould not connect to this light. Skipped.", Color.RED).show();
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
            try{
                if(Functions.isLightOn(ip, Functions.DEFAULT_PORT)){
                    tempTabCurrentStateLabel.setText("ON");
                    tempTabCurrentStateLabel.setTextFill(Color.GREEN);
                    tempTabCurrentStateLabel.setVisible(true);
                }
            }catch (Exception ignored){
                tempTabCurrentStateLabel.setTextFill(Color.DARKORANGE);
                tempTabCurrentStateLabel.setText("UNKNOWN");
                tempTabCurrentStateLabel.setVisible(true);
            }
        }else{
            new Message(tempTabMsgLabel, 2000, "No light source selected.", Color.RED).show();
        }
    }

    public void tempTabTurnOffButtonClick(){
        if(tempTabSelectedLightComboBox.getValue() != null){
            String ip = cBoxItem_Ip.get(tempTabSelectedLightComboBox.getValue());
            Functions.turnOff(ip, Functions.DEFAULT_PORT);
            try{
                if(!Functions.isLightOn(ip, Functions.DEFAULT_PORT)) {
                    tempTabCurrentStateLabel.setText("OFF");
                    tempTabCurrentStateLabel.setTextFill(Color.RED);
                    tempTabCurrentStateLabel.setVisible(true);
                }
            }catch (Exception ignored){
                tempTabCurrentStateLabel.setTextFill(Color.DARKORANGE);
                tempTabCurrentStateLabel.setText("UNKNOWN");
                tempTabCurrentStateLabel.setVisible(true);
            }
        }else{
            new Message(tempTabMsgLabel, 2000, "No light source selected.", Color.RED).show();
        }
    }

    public void tempTabTurnOnButtonClick(){
        if(tempTabSelectedLightComboBox.getValue() != null){
            String ip = cBoxItem_Ip.get(tempTabSelectedLightComboBox.getValue());
            Functions.turnOn(ip, Functions.DEFAULT_PORT);
            try{
                if(Functions.isLightOn(ip, Functions.DEFAULT_PORT)) {
                    tempTabCurrentStateLabel.setText("ON");
                    tempTabCurrentStateLabel.setTextFill(Color.GREEN);
                    tempTabCurrentStateLabel.setVisible(true);
                }
            }catch (Exception ignored){
                tempTabCurrentStateLabel.setTextFill(Color.DARKORANGE);
                tempTabCurrentStateLabel.setText("UNKNOWN");
                tempTabCurrentStateLabel.setVisible(true);
            }
        }else{
            new Message(tempTabMsgLabel, 2000, "No light source selected.", Color.RED).show();
        }
    }

    public void warmestLabelClick(){
        tempSlider.setValue(22);
        tempTabTempTextField.setText(String.valueOf((int)tempSlider.getValue() * 100));
    }

    public void warmLabelClick(){
        tempSlider.setValue(27);
        tempTabTempTextField.setText(String.valueOf((int)tempSlider.getValue() * 100));
    }

    public void daylightLabelClick(){
        tempSlider.setValue(42);
        tempTabTempTextField.setText(String.valueOf((int)tempSlider.getValue() * 100));
    }

    public void coolLabelClick(){
        tempSlider.setValue(65);
        tempTabTempTextField.setText(String.valueOf((int)tempSlider.getValue() * 100));
    }

    public void tempSliderClick(){
        tempTabTempTextField.setText(String.valueOf((int)tempSlider.getValue() * 100));
    }

    public void tempTabBrightnessSliderClick(){
        tempTabBrgValueTextField.setText(String.valueOf((int)tempTabBrightnessSlider.getValue()));
    }

    public void colorTabBrightnessSliderClick(){
        colorTabBrgValueTextField.setText(String.valueOf((int)colorTabBrightnessSlider.getValue()));
    }

    public void tempTabComboBoxIndexChanged() {
        String item = tempTabSelectedLightComboBox.getValue();
        String ip = cBoxItem_Ip.get(item);

        try{
            boolean isOn = Functions.isLightOn(ip, Functions.DEFAULT_PORT);
            if(isOn){
                tempTabCurrentStateLabel.setTextFill(Color.GREEN);
                tempTabCurrentStateLabel.setText("ON");
                tempTabCurrentStateLabel.setVisible(true);
            }else{
                tempTabCurrentStateLabel.setTextFill(Color.RED);
                tempTabCurrentStateLabel.setText("OFF");
                tempTabCurrentStateLabel.setVisible(true);
            }

            if(Functions.isLightInTemperatureMode(ip, Functions.DEFAULT_PORT)){
                int temp = Functions.getCurrentStateTemperatureKelvins(ip, Functions.DEFAULT_PORT);
                tempSlider.setValue((double) temp / 100);
                tempTabTempTextField.setText(String.valueOf(temp));
            }
            int brightness = Functions.getCurrentStateBrightness(ip, Functions.DEFAULT_PORT);
            tempTabBrightnessSlider.setValue(brightness);
            tempTabBrgValueTextField.setText(String.valueOf(brightness));
        }catch(Exception ignored){
            tempTabCurrentStateLabel.setTextFill(Color.DARKORANGE);
            tempTabCurrentStateLabel.setText("UNKNOWN");
            tempTabCurrentStateLabel.setVisible(true);
        }
    }

    // ----------- COLOR TAB -----------

    public void colorTabBrgValueTextFieldTextChanged(){
        try {
            int value = Integer.parseInt(colorTabBrgValueTextField.getText());
            if (value < 10 || value > 100) {
                Message msg = new Message(colorTabMessageLabel, 2000, "Only integers in [10, 100] permitted for this field", Color.RED);
                msg.show();
            } else {
                colorTabBrightnessSlider.setValue(value);
            }
        } catch (NumberFormatException ex) {
            Message msg = new Message(colorTabMessageLabel, 2000, "Only integers in [10, 100] permitted for this field", Color.RED);
            msg.show();
        }
    }

    public void rSliderDragDetected(){
        rTextField.setText(Integer.toString((int) rSlider.getValue()));
        rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
    }

    public void rSliderClick(){
        rTextField.setText(Integer.toString((int) rSlider.getValue()));
        rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
    }

    public void rTextFieldTextChanged(){
        try {
            int value = Integer.parseInt(rTextField.getText());
            if (value < 1 || value > 255) {
                Message msg = new Message(colorTabMessageLabel, 2000, "Only integers in [1, 255] permitted for this field", Color.RED);
                msg.show();
            } else {
                rSlider.setValue(value);
                rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
            }
        } catch (NumberFormatException ex) {
            Message msg = new Message(colorTabMessageLabel, 2000, "Only integers in [1, 255] permitted for this field", Color.RED);
            msg.show();
        }
    }

    @FXML
    public void rSliderScroll(Event event){
        if (event.getEventType() == javafx.scene.input.ScrollEvent.SCROLL) {
            javafx.scene.input.ScrollEvent scrollEvent = (javafx.scene.input.ScrollEvent) event;

            double minorTickUnit = rSlider.getMinorTickCount();
            double newValue = rSlider.getValue();

            if (scrollEvent.getDeltaY() > 0) {
                newValue = Math.min(rSlider.getMax(), newValue + minorTickUnit);
            } else if (scrollEvent.getDeltaY() < 0) {
                newValue = Math.max(rSlider.getMin(), newValue - minorTickUnit);
            }

            rSlider.setValue(newValue);
            rSliderDragDetected();
        }
    }

    public void gSliderDragDetected(){
        gTextField.setText(Integer.toString((int) gSlider.getValue()));
        rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
    }

    public void gSliderClick(){
        gTextField.setText(Integer.toString((int) gSlider.getValue()));
        rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
    }

    @FXML
    public void gSliderScroll(Event event){
        if (event.getEventType() == javafx.scene.input.ScrollEvent.SCROLL) {
            javafx.scene.input.ScrollEvent scrollEvent = (javafx.scene.input.ScrollEvent) event;

            double minorTickUnit = gSlider.getMinorTickCount();
            double newValue = gSlider.getValue();

            // Adjust the value based on scroll direction and minor tick unit
            if (scrollEvent.getDeltaY() > 0) {
                newValue = Math.min(gSlider.getMax(), newValue + minorTickUnit);
            } else if (scrollEvent.getDeltaY() < 0) {
                newValue = Math.max(gSlider.getMin(), newValue - minorTickUnit);
            }

            gSlider.setValue(newValue);
            gSliderDragDetected();
        }
    }

    public void gTextFieldTextChanged(){
        try {
            int value = Integer.parseInt(gTextField.getText());
            if (value < 1 || value > 255) {
                Message msg = new Message(colorTabMessageLabel, 2000, "Only integers in [1, 255] permitted for this field", Color.RED);
                msg.show();
            } else {
                gSlider.setValue(value);
                rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
            }
        } catch (NumberFormatException ex) {
            Message msg = new Message(colorTabMessageLabel, 2000, "Only integers in [1, 255] permitted for this field", Color.RED);
            msg.show();
        }
    }

    public void bSliderDragDetected(){
        bTextField.setText(Integer.toString((int) bSlider.getValue()));
        rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
    }

    public void bSliderClick(){
        bTextField.setText(Integer.toString((int) bSlider.getValue()));
        rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
    }

    @FXML
    public void bSliderScroll(Event event){
        if (event.getEventType() == javafx.scene.input.ScrollEvent.SCROLL) {
            javafx.scene.input.ScrollEvent scrollEvent = (javafx.scene.input.ScrollEvent) event;

            double minorTickUnit = bSlider.getMinorTickCount();
            double newValue = bSlider.getValue();

            // Adjust the value based on scroll direction and minor tick unit
            if (scrollEvent.getDeltaY() > 0) {
                newValue = Math.min(bSlider.getMax(), newValue + minorTickUnit);
            } else if (scrollEvent.getDeltaY() < 0) {
                newValue = Math.max(bSlider.getMin(), newValue - minorTickUnit);
            }

            bSlider.setValue(newValue);
            bSliderDragDetected();
        }
    }

    public void bTextFieldTextChanged(){
        try {
            int value = Integer.parseInt(bTextField.getText());
            if (value < 1 || value > 255) {
                Message msg = new Message(colorTabMessageLabel, 2000, "Only integers in [1, 255] permitted for this field", Color.RED);
                msg.show();
            } else {
                bSlider.setValue(value);
                rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
            }
        } catch (NumberFormatException ex) {
            Message msg = new Message(colorTabMessageLabel, 2000, "Only integers in [1, 255] permitted for this field", Color.RED);
            msg.show();
        }
    }

    public void wSliderDragDetected(){
        wTextField.setText(Integer.toString((int) wSlider.getValue()));
    }

    public void wSliderClick(){
        wTextField.setText(Integer.toString((int) wSlider.getValue()));
    }

    @FXML
    public void wSliderScroll(Event event){
        if (event.getEventType() == javafx.scene.input.ScrollEvent.SCROLL) {
            javafx.scene.input.ScrollEvent scrollEvent = (javafx.scene.input.ScrollEvent) event;

            double minorTickUnit = wSlider.getMinorTickCount();
            double newValue = wSlider.getValue();

            // Adjust the value based on scroll direction and minor tick unit
            if (scrollEvent.getDeltaY() > 0) {
                newValue = Math.min(wSlider.getMax(), newValue + minorTickUnit);
            } else if (scrollEvent.getDeltaY() < 0) {
                newValue = Math.max(wSlider.getMin(), newValue - minorTickUnit);
            }

            wSlider.setValue(newValue);
            wSliderDragDetected();
        }
    }

    public void wTextFieldTextChanged(){
        try {
            int value = Integer.parseInt(wTextField.getText());
            if (value < 1 || value > 255) {
                Message msg = new Message(colorTabMessageLabel, 2000, "Only integers in [1, 255] permitted for this field", Color.RED);
                msg.show();
            } else {
                wSlider.setValue(value);
                rectangle.setFill(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
            }
        } catch (NumberFormatException ex) {
            Message msg = new Message(colorTabMessageLabel, 2000, "Only integers in [1, 255] permitted for this field", Color.RED);
            msg.show();
        }
    }

    // ----------- SETTINGS TAB -----------

    public void settingsTabSaveButtonClick(){

    }

    public void settingsTabLoadDefaultsButtonClick(){

    }

    public void preserveLightsOnExitCheckBoxCheckedChanged(){
        boolean checked = persistLightsCheckBox.isSelected();
        if(Utils.checkIfSettingsHaveChanged(settings, persistLightsCheckBox, automaticAdjustmentCheckBox)){
            settingsTabMessageLabel.setVisible(true);
            settingsTabMessageLabel.setTextFill(Color.ORANGERED);
            settingsTabMessageLabel.setText("There are unsaved changes.");
            // TODO: update pendingSettings
        }else{
            settingsTabMessageLabel.setVisible(false);
        }
    }

    public void adjustLightsStateAutomaticallyCheckBoxCheckedChanged(){

    }
}