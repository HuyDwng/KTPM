module com.bttb.bttb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires jakarta.mail;
    
    opens com.bttb.pojo to javafx.base;
    opens com.bttb.bttb to javafx.fxml;
    exports com.bttb.bttb;
    exports com.bttb.services;
    exports com.bttb.pojo;
    
}
