package flightsfx;

import flightsfx.model.Flight;
import flightsfx.utils.FileUtils;
import flightsfx.utils.SortByLocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
                buttonDelete.setDisable(false);
            else
                buttonDelete.setDisable(true);
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
        switch (filter.getValue())
        {
            case "Show all flights":
                flights = FXCollections.observableArrayList(loadFlights());
                tableFlights.setItems(flights);
                break;

            case "Show flights to currently selected city":
                if (!buttonDelete.isDisable())
                {
                    TablePosition pos = tableFlights.getSelectionModel().
                            getSelectedCells().get(0);
                    int row = pos.getRow();
                    TableColumn col = pos.getTableColumn();
                    String selectCity = (String) col.getCellObservableValue(row)
                            .getValue();

                    flights.removeIf(f -> !f.getDestination()
                            .equals(selectCity));
                    buttonDelete.setDisable(true);
                }
                break;

            case "Show long flights":
                flights.removeIf(f -> Integer.parseInt(
                        f.getFlightDuration().split(":")[0]) < 3);
                break;

            case "Show next 5 flights":
                SimpleDateFormat formatter =
                        new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date= new Date();
                LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(),
                        ZoneId.systemDefault());
                flights.removeIf(f ->
                    f.getDepTimeAndDateNoFormat().isBefore(ldt));
                Collections.sort(flights, new SortByLocalDateTime());
                while (flights.size() > 5)
                    flights.remove(flights.size()-1);
                break;
            case "Show flight duration average":
                long nanoSum = flights.get(0)
                        .getFlightDurationNoFormat().toNanoOfDay();
                for (Flight f : flights)
                {
                    nanoSum += f.getFlightDurationNoFormat().toNanoOfDay();
                }
                LocalTime average = LocalTime.ofNanoOfDay(
                        nanoSum/(1+flights.size()));

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                dialog.setTitle("Average");
                dialog.setHeaderText("Average flights time");
                dialog.setContentText("The average of flights duration is: "
                        +average);
                dialog.show();
                break;
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
