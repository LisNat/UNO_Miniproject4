module org.example.eiscuno {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    //requires org.junit.jupiter.api;


    opens org.example.eiscuno to javafx.fxml;
    opens org.example.eiscuno.controller to javafx.fxml;
    exports org.example.eiscuno;
}