package algo.weatherdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;

/**
 * Projekt - Algoritmer och datastrukturer 2IS206
 * 
 * Retrieves temperature data from a weather station csv file. Class stores the
 * data in a Tree and also contains methods to search in the data.
 * 
 * @author Viktor Lundberg, vilu6614
 * @version 1.4, 2021-03-15
 */

public class WeatherDataHandler
{
	/**
	 * Store data in TreeMap. Key has to be unique so we use LocalDateTime as key.
	 * Value is the object containing all the information about the current
	 * MeasurePoint.
	 */
	private Map<LocalDateTime, MeasurePoint> dataMap = new TreeMap<>();

	/**
	 * Load weather data from file. Create MeasurePoint objects and store in
	 * TreeMap.
	 * 
	 * @param filePath path to file with weather data
	 * @throws IOException if there is a problem while reading the file
	 */
	public void loadData(String filePath) throws IOException
	{
		long start = System.currentTimeMillis();
		// Load into list as Strings
		List<String> fileData = Files.readAllLines(Paths.get(filePath));

		// Loop through the list and create MeasurePoint objects from it. Store in Map
		for (String entry : fileData)
		{
			MeasurePoint currentMeasure = new MeasurePoint(entry);
			dataMap.put(currentMeasure.getDateTime(), currentMeasure);
		}
		long finished = System.currentTimeMillis() - start;
		System.out.println("time: " + finished + " ms");
	}

	// @formatter:off
	/**
	 * Search for average temperature for all dates between the two dates
	 * (inclusive). Result is sorted by date (ascending). When searching from
	 * 2000-01-01 to 2000-01-03 the result should be: 
	 * 2000-01-01 average temperature: 0.42 degrees Celsius 
	 * 2000-01-02 average temperature: 2.26 degrees Celsius
	 * 2000-01-03 average temperature: 2.78 degrees Celsius
	 * 
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo end date (YYYY-MM-DD) inclusive
	 * @return average temperature for each date, sorted by date
	 */
	// @formatter:on
	public List<String> averageTemperatures(LocalDate dateFrom, LocalDate dateTo)
	{
		// Check that the user has entered dates that is present in the dataset
		if (isDateInData(dateFrom, dateTo))
		{
			// We iterate over the data and add what we find to a new tree that we use to
			// calculate the average values from. The temperature data is added in stack for
			// simplicity because push / pull operations is fast and order does not matter
			// in this case.
			Map<LocalDate, Stack<Double>> calcTree = new TreeMap<>();
			for (Map.Entry<LocalDateTime, MeasurePoint> entry : dataMap.entrySet())
			{
				// When this condition is true, we have found a date we are looking for
				if (!entry.getKey().toLocalDate().isAfter(dateTo) && !entry.getKey().toLocalDate().isBefore(dateFrom))
				{
					// Get data and add to tree
					LocalDate date = entry.getValue().getDate();
					double temp = entry.getValue().getTemperature();

					// Date does not exist yet in tree, new date to add
					if (!calcTree.containsKey(date))
					{
						// Create new stack for the date and push temperature to stack. Put in tree
						Stack<Double> temperatures = new Stack<>();
						temperatures.push(temp);
						calcTree.put(date, temperatures);
					}
					// Date already existed in tree, just update the stack
					else if (calcTree.containsKey(date))
					{
						calcTree.get(date).push(temp);
					}
				}
			}

			// Iterate over the previous tree, calculate average temperature for the date
			// and add to a results list. ArrayList is chosen because navigating in the list
			// is fast and we know the size so it will not be full.
			List<String> results = new ArrayList<>(calcTree.size());
			for (Map.Entry<LocalDate, Stack<Double>> entry : calcTree.entrySet())
			{
				// Get average value from calcAverageOfStack method and round it
				double average = calcAverageOfStack(entry.getValue());
				double rounded = Math.round(average * 100.0) / 100.0;
				results.add(entry.getKey() + " average temperature: " + rounded + " degrees Celsius");
			}
			// Return the list
			return results;
		}
		// Return empty list
		List<String> empty = new ArrayList<>();
		return empty;
	}

	/**
	 * Calculates the average value of all values in the stack
	 * 
	 * @param stack to calculate from
	 * @return the sum
	 */
	private double calcAverageOfStack(Stack<Double> stack)
	{
		int size = stack.size();
		double result = 0;
		while (!stack.empty())
		{
			result += stack.pop();
		}
		return (result / size);
	}

	// @formatter:off
	/**
	 * Search for missing values between the two dates (inclusive) assuming there
	 * should be 24 measurement values for each day (once every hour). Result is
	 * sorted by number of missing values (descending). When searching from
	 * 2000-01-01 to 2000-01-03 the result should be: 
	 * 2000-01-02 missing 1 values
	 * 2000-01-03 missing 1 values 
	 * 2000-01-01 missing 0 values
	 * 
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo end date (YYYY-MM-DD) inclusive
	 * @return dates with missing values together with number of missing values for
	 *         each date, sorted by number of missing values (descending)
	 */
	// @formatter:on
	public List<String> missingValues(LocalDate dateFrom, LocalDate dateTo)
	{
		// Check that the user has entered dates that is present in the dataset
		if (isDateInData(dateFrom, dateTo))
		{
			// To search for missing values we assume all values are missing by default.
			// Whenever we find a value, we update our hypothesis for the current date.
			// Create a LinkedHashMap and iterate over tree.
			Map<LocalDate, Integer> missingValues = new LinkedHashMap<>();
			for (Map.Entry<LocalDateTime, MeasurePoint> entry : dataMap.entrySet())
			{
				// When this condition is true, we have found a date we are looking for
				if (!entry.getKey().toLocalDate().isAfter(dateTo) && !entry.getKey().toLocalDate().isBefore(dateFrom))
				{
					// Get the date and add to the tree. Update assumption.
					LocalDate date = entry.getValue().getDate();
					int change = 1;
					int assumedMissing = 24;

					// Date does not exist yet in tree, new date to add
					if (!missingValues.containsKey(date))
					{
						missingValues.put(date, assumedMissing - change);
					}
					// Date already existed just update the index
					else if (missingValues.containsKey(date))
					{
						int prevMissing = missingValues.get(date);
						missingValues.replace(date, (prevMissing -= change));
					}
				}
			}

			// Now we have a map with key = LocalDate and value = missing values. Next step
			// is to sort our results. LinkedHashMap is sorted in the order that we added
			// data and does not change order. So we know it is sorted by Date ascending.
			// Create a set view of the map in a list
			List<Map.Entry<LocalDate, Integer>> sorted = new ArrayList<>(missingValues.entrySet());
			// Sort by comparing value (missing values) in reverse order
			Collections.sort(sorted, Entry.comparingByValue(Collections.reverseOrder()));
			// Iterate over the map and add results to list. ArrayList is chosen because we
			// know the size and navigating in it is fast.
			List<String> results = new ArrayList<>(sorted.size());
			for (Map.Entry<LocalDate, Integer> entry : sorted)
			{
				results.add(entry.getKey() + " missing " + entry.getValue() + " values");
			}
			// Return the list
			return results;
		}
		// Return empty list
		List<String> empty = new ArrayList<>();
		return empty;
	}

	// @formatter:off
	/**
	 * Search for percentage of approved values between the two dates (inclusive).
	 * When searching from 2000-01-01 to 2000-01-03 the result should be: 
	 * Approved values between 2000-01-01 and 2000-01-03: 32.86 %
	 * 
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo end date (YYYY-MM-DD) inclusive
	 * @return period and percentage of approved values for the period
	 */
	// @formatter:on
	public List<String> approvedValues(LocalDate dateFrom, LocalDate dateTo)
	{
		// Standard size list. It will only contain one value anyway.
		List<String> results = new ArrayList<>();

		// Check that the user has entered dates that is present in the dataset
		if (isDateInData(dateFrom, dateTo))
		{
			// Variables to calculate
			double approved = 0;
			double notApproved = 0;

			// Iterate over the data and search for approved values
			for (Map.Entry<LocalDateTime, MeasurePoint> entry : dataMap.entrySet())
			{
				// When this condition is true, we have found a date we are looking for
				if (!entry.getKey().toLocalDate().isAfter(dateTo) && !entry.getKey().toLocalDate().isBefore(dateFrom))
				{
					// If isApproved returns true, we have an approved value
					if (entry.getValue().isApproved())
					{
						approved++;
					}
					// Otherwise it is not approved
					else
					{
						notApproved++;
					}
				}
			}

			// Format results
			double approvedPercentage = approved / (approved + notApproved);
			NumberFormat percentageFormat = NumberFormat.getPercentInstance();
			percentageFormat.setMinimumFractionDigits(2);

			// Add to list
			results.add("Approved values between " + dateFrom + " and " + dateTo + ": "
					+ percentageFormat.format(approvedPercentage));
			// Return list
			return results;
		}
		// Return empty list
		List<String> empty = new ArrayList<>();
		return empty;
	}

	/**
	 * Method that checks that the user is searching for dates that are present in
	 * the data.
	 * 
	 * @param dateFrom (date to search from)
	 * @param dateTo   (date to search to)
	 * @return true if date is present in data, false if not.
	 */
	public boolean isDateInData(LocalDate dateFrom, LocalDate dateTo)
	{
		// Copy over data from dataMap to a new TreeMap
		TreeMap<LocalDateTime, MeasurePoint> treeMap = new TreeMap<>();
		treeMap.putAll(dataMap);
		// Find out first and last date
		LocalDateTime first = treeMap.firstKey();
		LocalDateTime last = treeMap.lastKey();

		// End date is before start date
		if (dateFrom.isAfter(dateTo))
		{
			System.out.println("End date appears to be before start date. Try again.");
			return false;
		}
		// Start date outside of dataset (before)
		else if (first.toLocalDate().isAfter(dateFrom))
		{
			System.out.println(
					"Start date appears to be before first available data. First data in dataset: " + first.toString());
			return false;
		}
		// Start date outside of dataset (after)
		else if (last.toLocalDate().isBefore(dateFrom))
		{
			System.out.println(
					"Start date appears to be after the last available data. Last data in dataset: " + last.toString());
			return false;
		}
		// End date outside of dataset (after)
		else if (last.toLocalDate().isBefore(dateTo))
		{
			System.out.println(
					"End date appears to be after the last available data. Last data in dataset: " + last.toString());
			return false;
		}
		// If none of these conditions before were met, return true.
		return true;
	}

}