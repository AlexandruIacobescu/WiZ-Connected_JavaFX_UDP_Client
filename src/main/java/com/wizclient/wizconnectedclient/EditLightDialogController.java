package com.wizclient.wizconnectedclient;

import com.wizclient.wizconnectedclient.classes.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditLightDialogController {

    @FXML
    private Label lblMessage;

    @FXML
    private Button saveButton, cancelButton;

    @FXML
    private TextField ipTextField, aliasTextField;

    private String initialIP, initialAlias;

    private HashMap<String,String> cBoxItem_Ip;
    private ComboBox<String> comboBox;
    public void getComboBox(ComboBox<String> cBox){
        comboBox = cBox;
    }
    public void getHashMap(HashMap<String,String> cBoxItem_ip){
        this.cBoxItem_Ip = cBoxItem_ip;
    }

    public void initIpTextField(String text){
        ipTextField.setText(text);
        initialIP = text;
    }

    public void initAliasTextField(String text){
        aliasTextField.setText(text);
        initialAlias = text;
    }

    public void saveButtonClick(){
        String ipAddress = ipTextField.getText();
        String regex = "^(10\\.(\\d{1,3}\\.){2}\\d{1,3}|172\\.(1[6-9]|2\\d|3[0-1])\\.(\\d{1,3}\\.){1,2}\\d{1,3}|192\\.168(\\.\\d{1,3}){2})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ipAddress);

        if(matcher.matches()){
            comboBox.getItems().remove(String.format("[%s] [%s]", initialAlias, initialIP));
            cBoxItem_Ip.remove(String.format("[%s] [%s]", initialAlias, initialIP));
            String item = String.format("[%s] [%s]", aliasTextField.getText(), ipTextField.getText());
            if(!cBoxItem_Ip.containsKey(item) && !cBoxItem_Ip.containsValue(ipTextField.getText()) ) {
                cBoxItem_Ip.put(item, ipTextField.getText());
                comboBox.getItems().add(item);
                closeWindow();
            }else{
                cBoxItem_Ip.put(String.format("[%s] [%s]", initialAlias, initialIP), initialIP);
                comboBox.getItems().add(String.format("[%s] [%s]", initialAlias, initialIP));
                Message msg = new Message(lblMessage, 2000, "IP already used for another light.", Color.RED);
                msg.show();
            }
        }else{
            Message message = new Message(lblMessage, 2000, "Not a Valid IP Address.", Color.RED);
            message.show();
        }
    }

    public void closeWindow(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
