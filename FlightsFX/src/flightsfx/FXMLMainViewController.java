package flightsfx;

import flightsfx.model.Flight;
import flightsfx.utils.FileUtils;
import flightsfx.utils.SortByLocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static flightsfx.utils.FileUtils.*;

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

    @FXML
    private Button buttonUpdate;

    ObservableList<Flight> flights;
    int selected;

    public void initialize() throws IOException
    {
        buttonDelete.setDisable(true);
        buttonUpdate.setDisable(true);

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
                buttonUpdate.setDisable(false);
            }
            else
            {
                buttonDelete.setDisable(true);
                buttonUpdate.setDisable(true);
            }
            Flight flightSelected =
                    tableFlights.getSelectionModel().getSelectedItem();
            flightNumber.setText(flightSelected.getFlightNumber());
            destination.setText(flightSelected.getDestination());
            departure.setText(flightSelected.getDepTimeAndDate());
            duration.setText(flightSelected.getFlightDuration());

            selected = tableFlights.getSelectionModel().getSelectedIndex();
        });
    }

    public List<Flight> getFlights()
    {
        return flights;
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
                    timeFormatter);
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
    void update(ActionEvent event)
    {

        flights.get(selected).setFlightNumber(flightNumber.getText());
        flights.get(selected).setDestination(destination.getText());
        flights.get(selected).setDepTimeAndDate(LocalDateTime.parse(
            departure.getText(), FileUtils.fmt));
        flights.get(selected).setFlightDuration(LocalTime.parse(
                duration.getText(), FileUtils.timeFormatter));
        tableFlights.setItems(flights);
        tableFlights.refresh();
        saveFlights(flights);
    }

    @FXML
    public void applyFilter(ActionEvent event) throws IOException
    {
        switch (filter.getValue())
        {
            case "Show all flights":
                tableFlights.setItems(flights);
                break;

            case "Show flights to currently selected city":
                try
                {
                    TablePosition pos = tableFlights.getSelectionModel().
                            getSelectedCells().get(0);
                    int row = pos.getRow();
                    TableColumn col = pos.getTableColumn();
                    String selectCity = (String) col.getCellObservableValue(row)
                            .getValue();

                    tableFlights.setItems(flights.filtered(p ->
                            p.getDestination().equals(selectCity)));

                    buttonDelete.setDisable(true);
                }
                catch(Exception e)
                {
                    Alert dialog = new Alert(Alert.AlertType.ERROR);
                    dialog.setTitle("ERROR");
                    dialog.setHeaderText("No row selected");
                    dialog.show();
                }
                break;

            case "Show long flights":
                tableFlights.setItems(flights.filtered(f -> Integer.parseInt(
                        f.getFlightDuration().split(":")[0]) > 3));
                break;

            case "Show next 5 flights":
                SimpleDateFormat formatter =
                        new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date= new Date();
                LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(),
                        ZoneId.systemDefault());

                tableFlights.setItems(flights.stream().filter(f ->
                        f.getDepTimeAndDateNoFormat().isAfter(ldt))
                        .limit(5).collect(Collectors.collectingAndThen(
                        Collectors.toList(), FXCollections::observableArrayList)));

                Collections.sort(flights, new SortByLocalDateTime());
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
                        +average.format(DateTimeFormatter.ofPattern("hh:mm:ss")));
                dialog.show();
                break;
        }
    }

    @FXML
    void goToChar(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/flightsfx/chart/FXMLChart.fxml"));
        Parent view1 = loader.load();
        Scene view1Scene = new Scene(view1);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(view1Scene);
        stage.show();
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
                    timeFormatter);
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
