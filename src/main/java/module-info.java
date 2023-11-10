module com.wizclient.wizconnectedclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens com.wizclient.wizconnectedclient to javafx.fxml;
    exports com.wizclient.wizconnectedclient;
}