package flightsfx.utils;

import flightsfx.model.Flight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{
    public static List<Flight> loadFlights() throws IOException
    {
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("dd/mm/yyyy hh:mm");

        ArrayList<Flight> flights = new ArrayList<>();

        File file = new File("flights.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ((line = br.readLine()) != null)
        {
            String flightNumber = line.split(";")[0];
            String destination = line.split(";")[1];
            LocalDateTime depTimeAndDate = LocalDateTime.parse(
                    line.split(";")[2], fmt);
            LocalTime flightDuration = LocalTime.parse(line.split(";")[3]);
            flights.add(new Flight(
                    flightNumber, destination, depTimeAndDate, flightDuration));
        }
        return flights;
    }

    public static void saveFlights(List<Flight> flights)
    {

    }
}
