module com.bttb.bttb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires jakarta.mail;
    
    opens com.bttb.pojo to javafx.base;
    opens com.bttb.controller to javafx.fxml;
    exports com.bttb.controller;
    exports com.bttb.services;
    exports com.bttb.pojo;
//    requires jbcrypt;
    requires jbcrypt;
    
}
