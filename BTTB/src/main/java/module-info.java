module com.bttb.bttb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens com.bttb.bttb to javafx.fxml;
    exports com.bttb.bttb;
    exports com.bttb.services;
    exports com.bttb.pojo;
    
}
