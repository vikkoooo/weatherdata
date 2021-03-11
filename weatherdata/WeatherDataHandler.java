package algo.weatherdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Retrieves temperature data from a weather station file.
 */
public class WeatherDataHandler
{
	/**
	 * Store data in TreeMap
	 */
	public Map<LocalDateTime, MeasurePoint> treeMeasures = new TreeMap<>();

	/**
	 * Load weather data from file.
	 * 
	 * @param filePath path to file with weather data
	 * @throws IOException if there is a problem while reading the file
	 */
	public void loadData(String filePath) throws IOException
	{
		List<String> fileData = Files.readAllLines(Paths.get(filePath));

		long startTime = System.currentTimeMillis(); // To track system time
		int i = 0; // To see how many elements added

		// Loop through the data input and create MeasurePoint objects from it. Store in
		// TreeMap.
		for (i = 0; i < fileData.size(); i++)
		{
			// Create new MeasurePoint. Constructor in MeasurePoint requires String as input
			// to split the data.
			MeasurePoint currentMeasure = new MeasurePoint(fileData.get(i));
			// Key is DateTime object and value is MeasurePoint object.
			treeMeasures.put(currentMeasure.getDateTime(), currentMeasure);
		}
		// Print elapsed time
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Added " + i + " elements to TreeMap. Time: " + estimatedTime + " ms");

	}

	/**
	 * Search for average temperature for all dates between the two dates
	 * (inclusive). Result is sorted by date (ascending). When searching from
	 * 2000-01-01 to 2000-01-03 the result should be: 2000-01-01 average
	 * temperature: 0.42 degrees Celsius 2000-01-02 average temperature: 2.26
	 * degrees Celsius 2000-01-03 average temperature: 2.78 degrees Celsius
	 * 
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo   end date (YYYY-MM-DD) inclusive
	 * @return average temperature for each date, sorted by date
	 */
	public List<String> averageTemperatures(LocalDate dateFrom, LocalDate dateTo)
	{
		long startTime = System.currentTimeMillis(); // To track system time
		// Since we don't know how many elements to add beforehand use LinkedList
		List<String> avgTemp = new LinkedList<>();

		// We use a TreeMap to calculate
		Map<LocalDate, Double> calcTree = new TreeMap<>();
		// TreeMap as index counter
		Map<LocalDate, Integer> indexTree = new TreeMap<>();

		// Loop through the tree
		for (Map.Entry<LocalDateTime, MeasurePoint> e : treeMeasures.entrySet())
		{
			// When we find a date we are looking for stop and do something
			if (!e.getKey().toLocalDate().isAfter(dateTo) && !e.getKey().toLocalDate().isBefore(dateFrom))
			{
				// String date = e.getValue().getDate().toString();
				// double temp = e.getValue().getTemperature();
				// avgTemp.add(date + " current temp: " + temp + " degrees Celsius");

				LocalDate date = e.getValue().getDate();
				double temp = e.getValue().getTemperature();

				// Date does not exist yet, new date to add
				if (!calcTree.containsKey(date))
				{
					calcTree.put(date, temp);
				} else if (calcTree.containsKey(date))
				{
					double prevTemp = calcTree.get(date);
					calcTree.replace(date, (prevTemp += temp));
				}
			}
		}

		// Loop through the tree again
		for (Map.Entry<LocalDateTime, MeasurePoint> f : treeMeasures.entrySet())
		{
			// Same as before
			if (!f.getKey().toLocalDate().isAfter(dateTo) && !f.getKey().toLocalDate().isBefore(dateFrom))
			{
				LocalDate date = f.getValue().getDate();
				int i = 1;

				// Date does not exist yet, new date to add
				if (!indexTree.containsKey(date))
				{
					indexTree.put(date, i);

				} else if (indexTree.containsKey(date))
				{
					int prevIndex = indexTree.get(date);
					indexTree.replace(date, (prevIndex += i));
				}

			}
		}
		
		
		

		List<Double> calculatingList = new ArrayList<>();
		
		
		for (Map.Entry<LocalDate, Double> h : calcTree.entrySet())
		{
			System.out.println("LocalDate: " + h.getKey() + ", Double: " + h.getValue());
			calculatingList.add(h.getValue());
		}
		
		
		for (int i = 0; i < calculatingList.size(); i++)
		{
			System.out.println(calculatingList.get(i));
		}
		

		for (Map.Entry<LocalDate, Integer> g : indexTree.entrySet())
		{
			int i = 0;
			System.out.println("LocalDate: " + g.getKey() + ", Integer: " + g.getValue());
			double total = calculatingList.get(i);
			double averageTemp = total / g.getValue();
			String date = g.getKey().toString();
			avgTemp.add(date + " medel temp: " + averageTemp + " degress celsius");
			i++;
		}

		


		// Print elapsed time
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Added " + avgTemp.size() + " elements to LinkedList. Time: " + estimatedTime + " ms");

		// Return
		return avgTemp;
	}

	/**
	 * Search for missing values between the two dates (inclusive) assuming there
	 * should be 24 measurement values for each day (once every hour). Result is
	 * sorted by number of missing values (descending). When searching from
	 * 2000-01-01 to 2000-01-03 the result should be: 2000-01-02 missing 1 values
	 * 2000-01-03 missing 1 values 2000-01-01 missing 0 values
	 * 
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo   end date (YYYY-MM-DD) inclusive
	 * @return dates with missing values together with number of missing values for
	 *         each date, sorted by number of missing values (descending)
	 */
	public List<String> missingValues(LocalDate dateFrom, LocalDate dateTo)
	{
		// TODO: Implements method
		return null;
	}

	/**
	 * Search for percentage of approved values between the two dates (inclusive).
	 * When searching from 2000-01-01 to 2000-01-03 the result should be: Approved
	 * values between 2000-01-01 and 2000-01-03: 32.86 %
	 * 
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo   end date (YYYY-MM-DD) inclusive
	 * @return period and percentage of approved values for the period
	 */
	public List<String> approvedValues(LocalDate dateFrom, LocalDate dateTo)
	{
		// TODO: Implements method
		return null;
	}
}