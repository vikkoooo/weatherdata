package algo.weatherdata;

import java.time.LocalDate;

/**
 * Projekt - Algoritmer och datastrukturer 2IS206
 * 
 * MeasureMissing class. It is needed to be able to sort results according to
 * preferences.
 * 
 * @author Viktor Lundberg, vilu6614
 * @version 1.0, 2021-03-15
 */

public class MeasureMissing
{
	/**
	 * Instance variables
	 */
	private LocalDate date;
	private int missing;

	/**
	 * Constructor
	 * 
	 * @param date    (LocalDate the date)
	 * @param missing (number of missing values)
	 */
	public MeasureMissing(LocalDate date, int missing)
	{
		this.date = date;
		this.missing = missing;
	}

	/**
	 * @return the date
	 */
	public LocalDate getDate()
	{
		return date;
	}

	/**
	 * @return number of missing values
	 */
	public int getMissing()
	{
		return missing;
	}

}
