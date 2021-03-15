package algo.weatherdata;

import java.time.LocalDate;

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
	 * @param date
	 * @param missing
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
	 * @return the missing
	 */
	public int getMissing()
	{
		return missing;
	}

}
