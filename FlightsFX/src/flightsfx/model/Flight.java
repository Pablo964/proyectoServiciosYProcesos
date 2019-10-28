package flightsfx.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * class to create flights
 */
public class Flight
{
    protected String flightNumber;
    protected String destination;
    protected LocalDateTime depTimeAndDate;
    protected LocalTime flightDuration;

    /**
     * Create a flight with only flight number
     * @param flightNumber
     */
    public Flight(String flightNumber)
    {
        this.flightNumber = flightNumber;
    }

    /**
     * Create a flight with everyone properties
     * @param flightNumber number of identifation flight
     * @param destination city where the flight will land
     * @param depTimeAndDate take-off date and time
     * @param flightDuration duration in hours and minutes of flight
     */
    public Flight(String flightNumber, String destination,
                  LocalDateTime depTimeAndDate, LocalTime flightDuration)
    {
        this.flightNumber = flightNumber;
        this.destination = destination;
        this.depTimeAndDate = depTimeAndDate;
        this.flightDuration = flightDuration;
    }

    /**
     *
     * @return return a flight number
     */
    public String getFlightNumber()
    {
        return flightNumber;
    }

    /**
     * set the flight number
     * @param flightNumber
     */
    public void setFlightNumber(String flightNumber)
    {
        this.flightNumber = flightNumber;
    }

    /**
     *
     * @return return a destiny of flight
     */
    public String getDestination()
    {
        return destination;
    }

    /**
     * set the destiny of flight
     * @param destination
     */
    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    /**
     *
     * @return return a date and time of the flight take-off in String
     */
    public String getDepTimeAndDate()
    {
        DateTimeFormatter form = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return  depTimeAndDate.format(form);
    }

    /**
     *
     * @return return a date and time of the flight take-off
     */
    public LocalDateTime getDepTimeAndDateNoFormat()
    {
        return this.depTimeAndDate;
    }

    /**
     * set date and time of the flight take-off
     * @param depTimeAndDate
     */
    public void setDepTimeAndDate(LocalDateTime depTimeAndDate)
    {
        this.depTimeAndDate = depTimeAndDate;
    }

    /**
     *
     * @return return a flight duration in String
     */
    public String getFlightDuration()
    {
        DateTimeFormatter form = DateTimeFormatter.ofPattern("H:mm");
        return  flightDuration.format(form);
    }

    /**
     *
     * @return return a time of duration flight
     */
    public LocalTime getFlightDurationNoFormat()
    {
        return this.flightDuration;
    }

    /**
     * set a flight duration
     * @param flightDuration
     */
    public void setFlightDuration(LocalTime flightDuration)
    {
        this.flightDuration = flightDuration;
    }

    /**
     * return a string with a every properties of the flight
     * @return
     */
    @Override
    public String toString()
    {
        return flightNumber + ';' + destination + ';' + getDepTimeAndDate() +';'+
                getFlightDuration();
    }
}
