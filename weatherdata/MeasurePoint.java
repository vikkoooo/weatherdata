package algo.weatherdata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Projekt - Algoritmer och datastrukturer 2IS206
 * 
 * MeasurePoint class that stores all valuable information from input data. Date
 * (LocalDate), time (LocalTime), temperature (double), approved (boolean) and
 * dateTime (LocalDateTime)
 * 
 * @author Viktor Lundberg, vilu6614
 * @version 1.4, 2021-03-15
 */

public class MeasurePoint
{
	/**
	 * Instance variables
	 */
	private String[] splitted;
	private LocalDate date;
	private LocalTime time;
	private double temperature;
	private boolean approved;
	private LocalDateTime dateTime;

	/**
	 * Constructor. String to split 4 times with ; as delimiter
	 * 
	 * @param rowToSplit, input tuple we want to split and create object from
	 */
	public MeasurePoint(String rowToSplit)
	{
		splitted = rowToSplit.split(";", 4);
		date = LocalDate.parse(splitted[0]);
		time = LocalTime.parse(splitted[1]);
		temperature = Double.parseDouble(splitted[2]);
		approved = splitted[3].contentEquals("G");
		dateTime = LocalDateTime.of(date, time);
	}

	/**
	 * @return the date (LocalDate)
	 */
	public LocalDate getDate()
	{
		return date;
	}

	/**
	 * @return the time (LocalTime)
	 */
	public LocalTime getTime()
	{
		return time;
	}

	/**
	 * @return the temperature (degrees)
	 */
	public double getTemperature()
	{
		return temperature;
	}

	/**
	 * @return true if MeasurePoint was marked as approved (G). false otherwise (Y).
	 */
	public boolean isApproved()
	{
		return approved;
	}

	/**
	 * @return the dateTime (LocalDateTime)
	 */
	public LocalDateTime getDateTime()
	{
		return dateTime;
	}

}
