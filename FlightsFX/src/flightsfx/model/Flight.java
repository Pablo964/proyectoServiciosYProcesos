package flightsfx.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Flight
{
    protected String flightNumber;
    protected String destination;
    protected LocalDateTime depTimeAndDate;
    protected LocalTime flightDuration;

    public Flight(String flightNumber)
    {
        this.flightNumber = flightNumber;
    }

    public Flight(String flightNumber, String destination,
                  LocalDateTime depTimeAndDate, LocalTime flightDuration)
    {
        this.flightNumber = flightNumber;
        this.destination = destination;
        this.depTimeAndDate = depTimeAndDate;
        this.flightDuration = flightDuration;
    }

    public String getFlightNumber()
    {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber)
    {
        this.flightNumber = flightNumber;
    }

    public String getDestination()
    {
        return destination;
    }

    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    public LocalDateTime getDepTimeAndDate()
    {
        return depTimeAndDate;
    }

    public void setDepTimeAndDate(LocalDateTime depTimeAndDate)
    {
        this.depTimeAndDate = depTimeAndDate;
    }

    public LocalTime getFlightDuration()
    {
        return flightDuration;
    }

    public void setFlightDuration(LocalTime flightDuration)
    {
        this.flightDuration = flightDuration;
    }

    @Override
    public String toString()
    {
        return "Flight{" +
                "flightNumber='" + flightNumber + '\'' +
                ", destination='" + destination + '\'' +
                ", depTimeAndDate=" + depTimeAndDate +
                ", flightDuration=" + flightDuration +
                '}';
    }
}
