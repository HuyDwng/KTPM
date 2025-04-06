module com.bttb.bttb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    
    opens com.bttb.pojo to javafx.base;
    opens com.bttb.bttb to javafx.fxml;
    exports com.bttb.bttb;
}
