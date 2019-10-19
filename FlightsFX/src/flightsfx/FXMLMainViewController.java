package flightsfx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FXMLMainViewController
{
    @FXML
    private TableView<?> tableFlights;

    @FXML
    private ChoiceBox<?> filter;

    @FXML
    private TextField flightNumber, destination, departure, duration, buttonAdd,
            buttonDelete, buttonFilter;
}
