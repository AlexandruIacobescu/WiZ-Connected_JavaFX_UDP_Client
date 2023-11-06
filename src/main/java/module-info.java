module com.wizclient.wizconnectedclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.wizclient.wizconnectedclient to javafx.fxml;
    exports com.wizclient.wizconnectedclient;
}