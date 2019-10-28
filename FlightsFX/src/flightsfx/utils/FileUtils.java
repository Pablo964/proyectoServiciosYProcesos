package flightsfx.utils;

import flightsfx.model.Flight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * class used to load and save flight data
 */
public class FileUtils
{
    public static DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("H:mm");
    public static DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * load flights for the file
     * @return return a list of flights
     * @throws IOException
     */
    public static List<Flight> loadFlights() throws IOException
    {
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
            LocalTime flightDuration = LocalTime.parse(line.split(";")[3],
                    timeFormatter);
            flights.add(new Flight(
                    flightNumber, destination, depTimeAndDate, flightDuration));

        }
        return flights;
    }

    /**
     * save the flights from the list in the file
     * @param flights
     */
    public static void saveFlights(List<Flight> flights)
    {
        try(PrintWriter pw = new PrintWriter("flights.txt"))
        {
            flights.stream().forEach(f -> pw.println(f.toString()));
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
