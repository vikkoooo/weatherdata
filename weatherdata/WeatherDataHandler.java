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
 * @version 1.0, 2021-03-15
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
		// Load into list as Strings
		List<String> fileData = Files.readAllLines(Paths.get(filePath));

		// Loop through the list and create MeasurePoint objects from it. Store in Map
		for (String entry : fileData)
		{
			MeasurePoint currentMeasure = new MeasurePoint(entry);
			dataMap.put(currentMeasure.getDateTime(), currentMeasure);
		}
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
		// List to store results in. LinkedList is chosen because we don't know how many
		// dates the user will search for. So the length of the list will vary between 1
		// - 50 000 approx which means it could be heavy for an ArrayList to resize so
		// many times.
		List<String> results = new LinkedList<>();

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
		// and add to the list.
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
		// List to store results in. LinkedList is chosen because we don't know how many
		// dates the user will search for. So the length of the list will vary between 1
		// - 50 000 approx which means it could be heavy for an ArrayList to resize so
		// many times.
		List<String> results = new LinkedList<>();

		// To search for missing values we assume all values are missing by default.
		// Whenever we find a value, we update our hypothesis for the current date.
		// Create map and iterate over tree.
		LinkedHashMap<LocalDate, Integer> calcTree = new LinkedHashMap<>();
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
				if (!calcTree.containsKey(date))
				{
					calcTree.put(date, assumedMissing - change);
				}
				// Date already existed just update the index
				else if (calcTree.containsKey(date))
				{
					int prevMissing = calcTree.get(date);
					calcTree.replace(date, (prevMissing -= change));
				}
			}
		}


		List<Map.Entry<LocalDate, Integer>> list = new LinkedList<>(calcTree.entrySet());		
		Collections.sort(list, Entry.comparingByValue(Collections.reverseOrder()));
		List<String> results2 = new LinkedList<>();
		for (Map.Entry<LocalDate, Integer> entry : list)
		{
			results2.add(entry.getKey() + " missing " + entry.getValue() + " values");
		}
		
		
		// OLD METHOD TO SORT!!!
		// Now we have a tree with key = LocalDate and value = missing values. Next step is to sort our results.
		// Create a list and iterate over the tree
		List<MeasureMissing> missing = new LinkedList<>();
		for (Map.Entry<LocalDate, Integer> entry : calcTree.entrySet())
		{
			// Create MeasureMissing objects and add to the missing list
			MeasureMissing currentDate = new MeasureMissing(entry.getKey(), entry.getValue());
			missing.add(currentDate);
		}

		// Sort the list according to preferences
		missing.sort(
				Comparator.comparing(MeasureMissing::getMissing).reversed().thenComparing(MeasureMissing::getDate));

		// Loop through the list and add to String list
		for (MeasureMissing entry : missing)
		{
			results.add(entry.getDate() + " missing " + entry.getMissing() + " values");
		}


		// Return
		return results2;
	}

	// @formatter:off
	/**
	 * Search for percentage of approved values between the two dates (inclusive).
	 * When searching from 2000-01-01 to 2000-01-03 the result should be: Approved
	 * values between 2000-01-01 and 2000-01-03: 32.86 %
	 * 
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo end date (YYYY-MM-DD) inclusive
	 * @return period and percentage of approved values for the period
	 */
	// @formatter:on
	public List<String> approvedValues(LocalDate dateFrom, LocalDate dateTo)
	{
		long startTime = System.currentTimeMillis(); // To track system time

		// Since we don't know how many elements to search for beforehand use
		// LinkedList
		List<String> results = new LinkedList<>();

		if (isDateInData(dateFrom, dateTo))
		{

			// Variables to calculate
			double approved = 0;
			double notApproved = 0;

			// Iterate over the data and search for approved values
			for (Map.Entry<LocalDateTime, MeasurePoint> entry : dataMap.entrySet())
			{
				// When we find a date we are looking for, stop and do something
				if (!entry.getKey().toLocalDate().isAfter(dateTo) && !entry.getKey().toLocalDate().isBefore(dateFrom))
				{
					if (entry.getValue().isApproved())
					{
						approved++;
					}
					else
					{
						notApproved++;
					}
				}
			}

			// Format
			double approvedPercentage = approved / (approved + notApproved);
			NumberFormat percentageFormat = NumberFormat.getPercentInstance();
			percentageFormat.setMinimumFractionDigits(2);

			// Add to list
			results.add("Approved values between " + dateFrom + " and " + dateTo + ": "
					+ percentageFormat.format(approvedPercentage));

			// Print elapsed time
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Added " + results.size() + " elements to LinkedList. Time: " + estimatedTime + " ms");
		}
		return results;
	}

	public boolean isDateInData(LocalDate dateFrom, LocalDate dateTo)
	{
		TreeMap<LocalDateTime, MeasurePoint> treeMap = new TreeMap<>();
		treeMap.putAll(dataMap);
		LocalDateTime first = treeMap.firstKey();
		LocalDateTime last = treeMap.lastKey();

		if (dateFrom.isAfter(dateTo))
		{
			System.out.println("End date appears to be after start date. Try again.");
			return false;
		}
		else if (dateTo.isBefore(dateFrom))
		{
			System.out.println("Start date appears to be before end date. Try again.");
			return false;
		}
		else if (first.toLocalDate().isAfter(dateFrom))
		{
			System.out.println(
					"Start date appears to be before first available data. First data in dataset: " + first.toString());
			return false;
		}
		else if (last.toLocalDate().isBefore(dateFrom))
		{
			System.out.println(
					"Start date appears to be after the last available data. Last data in dataset: " + last.toString());
			return false;
		}
		else if (last.toLocalDate().isBefore(dateTo))
		{
			System.out.println(
					"End date appears to be after the last available data. Last data in dataset: " + last.toString());
			return false;
		}
		else if (first.toLocalDate().isAfter(dateTo))
		{
			System.out.println("End date appears to be before the first available data. First data in dataset: "
					+ first.toString());
			return false;
		}
		return true;
	}

}