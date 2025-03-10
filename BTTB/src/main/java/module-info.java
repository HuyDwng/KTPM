module com.bttb.bttb {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.bttb.bttb to javafx.fxml;
    exports com.bttb.bttb;
}
