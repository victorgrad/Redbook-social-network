module ro.ubb.map.gtsn.guitoysocialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.pdfbox;

    opens ro.ubb.map.gtsn.guitoysocialnetwork to javafx.fxml;
    opens ro.ubb.map.gtsn.guitoysocialnetwork.controllers;

    opens ro.ubb.map.gtsn.guitoysocialnetwork.domain;
    exports ro.ubb.map.gtsn.guitoysocialnetwork;
    exports ro.ubb.map.gtsn.guitoysocialnetwork.bussiness;
    exports ro.ubb.map.gtsn.guitoysocialnetwork.utils.observer;
    exports ro.ubb.map.gtsn.guitoysocialnetwork.domain;
    exports ro.ubb.map.gtsn.guitoysocialnetwork.controllers;
    exports ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos;
    opens ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos;
    exports ro.ubb.map.gtsn.guitoysocialnetwork.domain.models;
    opens ro.ubb.map.gtsn.guitoysocialnetwork.domain.models;

}