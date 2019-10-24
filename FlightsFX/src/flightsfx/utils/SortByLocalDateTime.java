package flightsfx.utils;

import flightsfx.model.Flight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class SortByLocalDateTime implements Comparator<Flight>
{
    @Override
    public int compare(Flight f1, Flight f2)
    {
        LocalDateTime dateF1 = f1.getDepTimeAndDateNoFormat();
        LocalDateTime dateF2 = f2.getDepTimeAndDateNoFormat();

        if (dateF1.isAfter(dateF2))
            return 1;
        if (dateF1.isBefore(dateF2))
            return -1;

        return 0;
    }
}
