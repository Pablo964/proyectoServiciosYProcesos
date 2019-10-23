package flightsfx;

import flightsfx.model.Flight;
import flightsfx.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static flightsfx.utils.FileUtils.loadFlights;
import static flightsfx.utils.FileUtils.saveFlights;

public class FXMLMainViewController
{
    @FXML
    private TableView<Flight> tableFlights;

    @FXML
    private TableColumn<Flight, String> colFlightNumber;

    @FXML
    private TableColumn<Flight, String> colDestination;

    @FXML
    private TableColumn<Flight, LocalDateTime> colDeparture;

    @FXML
    private TableColumn<Flight, LocalTime> colDuration;

    @FXML
    private ChoiceBox<String> filter;

    @FXML
    private TextField flightNumber;

    @FXML
    private TextField destination;

    @FXML
    private TextField departure;

    @FXML
    private TextField duration;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button applyFilter;

    @FXML
    private Text errorText;

    ObservableList<Flight> flights;

    public void initialize() throws IOException
    {
        buttonDelete.setDisable(true);
        flights = FXCollections.observableArrayList(loadFlights());

        filter.setItems(FXCollections.observableArrayList(
                "Show all flights",
                "Show flights to currently selected city",
                "Show long flights", "Show next 5 flights",
                "Show flight duration average"));
        filter.getSelectionModel().selectFirst();

        colFlightNumber.setCellValueFactory(
                new PropertyValueFactory("flightNumber"));
        colDestination.setCellValueFactory(
                new PropertyValueFactory("destination"));
        colDeparture.setCellValueFactory(
                new PropertyValueFactory("depTimeAndDate"));
        colDuration.setCellValueFactory(
                new PropertyValueFactory("flightDuration"));

        saveFlights(flights);
        tableFlights.setItems(flights);

        tableFlights.getSelectionModel().selectedItemProperty().addListener((
                obs, oldSelection, newSelection) ->
        {
            if (newSelection != null)
            {
                buttonDelete.setDisable(false);
            }
        });
    }

    @FXML
    void addFlight(ActionEvent event)
    {
        if (controlError())
        {
            String fieldFlightNumber = flightNumber.getText();
            String fieldDestination = destination.getText();
            LocalDateTime fieldDeparture = LocalDateTime.parse(
                    departure.getText(), FileUtils.fmt);
            LocalTime fieldDuration = LocalTime.parse(duration.getText(),
                    FileUtils.timeFormatter);
            flights.add(new Flight(fieldFlightNumber, fieldDestination,
                    fieldDeparture, fieldDuration));
            saveFlights(flights);
        }
    }
    @FXML
    void deleteFlight(ActionEvent event)
    {

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Confirmation");
        dialog.setHeaderText("Deletion confirmation");
        dialog.setContentText("Are you sure you" +
                " want to delete the selected row?");
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.get() == ButtonType.OK)
        {
            flights.remove(tableFlights.getSelectionModel().getSelectedIndex());
            saveFlights(flights);
        }
    }

    @FXML
    public void applyFilter(ActionEvent event) throws IOException
    {
        if(filter.getValue().equals("Show flights to currently selected city"))
        {
            if (!buttonDelete.isDisable())
            {
                TablePosition pos = tableFlights.getSelectionModel().
                        getSelectedCells().get(0);
                int row = pos.getRow();
                TableColumn col = pos.getTableColumn();
                String selectCity = (String) col.getCellObservableValue(row)
                        .getValue();
                int size = tableFlights.getItems().size();

                //mejorar
                for (int i = 0; i < size ; i++)
                {
                    String destiny = tableFlights.getItems().get(i)
                            .getDestination();
                    if (!destiny.equals(selectCity))
                    {
                        tableFlights.getItems().remove(i);
                        size--;
                        i = 0;
                    }
                }
            }
        }
        if(filter.getValue().equals("Show all flights"))
        {
            flights = FXCollections.observableArrayList(loadFlights());

            tableFlights.setItems(flights);
        }
    }

    public boolean controlError()
    {
        if (flightNumber.getText().equals("") || duration.getText().equals("")
                || destination.getText().equals("")
                || departure.getText().equals(""))
        {
            errorText.setText("the fields cannot be empty");
            return false;
        }

        try
        {
            LocalDateTime depTimeAndDate = LocalDateTime.parse(
                    departure.getText(), FileUtils.fmt);

        }
        catch (DateTimeParseException e)
        {
            errorText.setText("the field departure can be format" +
                    " dd/mm/yyyy hh:mm");
            return false;
        }
        try
        {
            LocalTime flightDuration = LocalTime.parse(duration.getText(),
                    FileUtils.timeFormatter);
        }
        catch (DateTimeException e)
        {
            errorText.setText("the field duration can be format" +
                    " h:mm");
            return false;
        }
        errorText.setText("");
        return true;
    }
}
