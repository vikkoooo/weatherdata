package algo.weatherdata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MeasurePoint
{
	/**
	 * Instance variables
	 */
	private LocalDate date;
	private LocalTime time;
	private double temperature;
	private boolean approved;
	private LocalDateTime dateTime;
	
	/**
	 * Constructor
	 * 
	 * @param rowToSplit
	 */
	public MeasurePoint (String rowToSplit)
	{
		String [] splitted = rowToSplit.split(";");
		date = LocalDate.parse(splitted[0]);
		time = LocalTime.parse(splitted[1]);
		temperature = Double.parseDouble(splitted[2]);
		approved = splitted[3].contentEquals("G");
		dateTime = LocalDateTime.of(date, time);
	}

	/**
	 * @return the date
	 */
	public LocalDate getDate()
	{
		return date;
	}

	/**
	 * @return the time
	 */
	public LocalTime getTime()
	{
		return time;
	}

	/**
	 * @return the temperature
	 */
	public double getTemperature()
	{
		return temperature;
	}

	/**
	 * @return the approved
	 */
	public boolean isApproved()
	{
		return approved;
	}

	/**
	 * @return the dateTime
	 */
	public LocalDateTime getDateTime()
	{
		return dateTime;
	}

}
