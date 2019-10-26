module proyectoPlantilla
{
    requires javafx.fxml;
    requires javafx.controls;
    exports flightsfx.model;
    exports flightsfx.chart;
    opens flightsfx;
    opens flightsfx.chart;
}