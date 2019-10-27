package flightsfx.chart;

import flightsfx.FXMLMainViewController;
import flightsfx.model.Flight;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChartController
{
    @FXML
    private PieChart chart;

    public void initialize()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/flightsfx/FXMLMainView.fxml"));

        try
        {
            Parent root = (Parent)loader.load();
        } catch (Exception e) {}
        FXMLMainViewController controller = (FXMLMainViewController)loader
                .getController();

        List<Flight> food = controller.getFlights();

        chart.getData().clear();

        Map<String, Long> result = food.stream()
                .collect(Collectors.groupingBy(
                flight -> flight.getDestination(), Collectors.counting()));
        result.forEach((cat, sum) ->
        {
            chart.getData().add(new PieChart.Data(cat, sum));
        });
    }


    @FXML
    void returnToMain(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                "/flightsfx/FXMLMainView.fxml"));
        Parent view1 = loader.load();
        Scene view1Scene = new Scene(view1);
        view1Scene.getStylesheets().add(getClass().getResource(
                "/flightsfx/style.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(view1Scene);
        stage.show();
    }
}