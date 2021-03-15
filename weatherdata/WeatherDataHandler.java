package algo.weatherdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
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
	public Map<LocalDateTime, MeasurePoint> dataMap = new TreeMap<>();

	/**
	 * Load weather data from file.
	 * 
	 * @param filePath path to file with weather data
	 * @throws IOException if there is a problem while reading the file
	 */
	public void loadData(String filePath) throws IOException
	{
		long startTime = System.currentTimeMillis(); // To track system time
		List<String> fileData = Files.readAllLines(Paths.get(filePath));
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Added " + fileData.size() + " Strings to List. Time: " + estimatedTime + " ms");

		startTime = System.currentTimeMillis(); // To track system time

		// Loop through the data input and create MeasurePoint objects from it.
		// Store in
		// Map.
		for(int i = 0; i < fileData.size(); i++)
		{
			// Create new MeasurePoint. Constructor in MeasurePoint requires
			// String as input
			// to split the data.
			MeasurePoint currentMeasure = new MeasurePoint(fileData.get(i));
			// Key is LocalDateTime object and value is MeasurePoint object.
			dataMap.put(currentMeasure.getDateTime(), currentMeasure);
		}
		// Print elapsed time
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Converted " + dataMap.size() + " Strings to MeasurePoints and added to Map. Time: "
				+ estimatedTime + " ms");

	}

	// @formatter:off
	/**
	 * Search for average temperature for all dates between the two dates
	 * (inclusive). Result is sorted by date (ascending). When searching from
	 * 2000-01-01 to 2000-01-03 the result should be: 2000-01-01 average
	 * temperature: 0.42 degrees Celsius 2000-01-02 average temperature: 2.26
	 * degrees Celsius 2000-01-03 average temperature: 2.78 degrees Celsius
	 * 
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo end date (YYYY-MM-DD) inclusive
	 * @return average temperature for each date, sorted by date
	 */
	// @formatter:on
	public List<String> averageTemperatures(LocalDate dateFrom, LocalDate dateTo)
	{
		long startTime = System.currentTimeMillis(); // To track system time
		// Since we don't know how many elements to add beforehand use
		// LinkedList
		List<String> results = new LinkedList<>();

		// We iterate over the data and add it to a new tree, that we later use
		// to
		// calculate the average values from
		Map<LocalDate, Stack<Double>> calcTree = new TreeMap<>();
		// Iterate over data
		for(Map.Entry<LocalDateTime, MeasurePoint> entry : dataMap.entrySet())
		{
			// When we find a date we are looking for, stop and do something
			if(!entry.getKey().toLocalDate().isAfter(dateTo) && !entry.getKey().toLocalDate().isBefore(dateFrom))
			{
				// Get data and add to tree
				LocalDate date = entry.getValue().getDate();
				double temp = entry.getValue().getTemperature();

				// Date does not exist yet in tree, new date to add
				if(!calcTree.containsKey(date))
				{
					Stack<Double> temperatures = new Stack<>();
					temperatures.push(temp);
					calcTree.put(date, temperatures);
				}
				// Date already existed just update the stack
				else if(calcTree.containsKey(date))
				{
					calcTree.get(date).push(temp);
				}
			}
		}

		// Iterate over the previous tree
		for(Map.Entry<LocalDate, Stack<Double>> entry : calcTree.entrySet())
		{
			// Get average value from calcAverageOfStack method and round it
			double average = calcAverageOfStack(entry.getValue());
			double rounded = Math.round(average * 100.0) / 100.0;
			// Add rounded result to our results list that we later return
			results.add(entry.getKey() + " average temperature: " + rounded + " degrees Celsius");
		}

		// Print elapsed time
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Added " + results.size() + " elements to LinkedList. Time: " + estimatedTime + " ms");

		// Return
		return results;
	}

	/**
	 * Calculates the average value of all elements in the stack
	 * 
	 * @param stack to calculate from
	 * @return the sum
	 */
	private double calcAverageOfStack(Stack<Double> stack)
	{
		int size = stack.size();
		double result = 0;
		while(!stack.empty())
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
	 * 2000-01-01 to 2000-01-03 the result should be: 2000-01-02 missing 1 values
	 * 2000-01-03 missing 1 values 2000-01-01 missing 0 values
	 * 
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo end date (YYYY-MM-DD) inclusive
	 * @return dates with missing values together with number of missing values for
	 *         each date, sorted by number of missing values (descending)
	 */
	// @formatter:on
	public List<String> missingValues(LocalDate dateFrom, LocalDate dateTo)
	{
		// TODO: Implements method
		long startTime = System.currentTimeMillis(); // To track system time

		// Since we don't know how many elements to search for beforehand use
		// LinkedList
		List<String> results = new LinkedList<>();
		List<MeasureMissing> missing = new LinkedList<>();

		// We iterate over the data and add it to a new tree, that we later use
		// to
		// check missing values from. The values we find is stored as Integer on
		// values
		// slot.
		Map<LocalDate, Integer> calcTree = new TreeMap<>();
		// Iterate over data
		for(Map.Entry<LocalDateTime, MeasurePoint> entry : dataMap.entrySet())
		{
			// When we find a date we are looking for, stop and do something
			if(!entry.getKey().toLocalDate().isAfter(dateTo) && !entry.getKey().toLocalDate().isBefore(dateFrom))
			{
				// Get date and add to tree with index counter as data
				LocalDate date = entry.getValue().getDate();
				int index = 1;
				int assumedFound = 24;

				// Date does not exist yet in tree, new date to add
				if(!calcTree.containsKey(date))
				{
					calcTree.put(date, assumedFound - index);
				}
				// Date already existed just update the index
				else if(calcTree.containsKey(date))
				{
					int prevIndex = calcTree.get(date);
					calcTree.replace(date, (prevIndex -= index));
				}
			}
		}

		// Iterate over the previous tree
		for(Map.Entry<LocalDate, Integer> entry : calcTree.entrySet())
		{
			// Create MeasureMissing objects and add to the missing list
			MeasureMissing currentDate = new MeasureMissing(entry.getKey(), entry.getValue());
			missing.add(currentDate);
		}

		// Sort the list according to preferences
		missing.sort(
				Comparator.comparing(MeasureMissing::getMissing).reversed().thenComparing(MeasureMissing::getDate));

		// Loop through the list and add to String list
		for(MeasureMissing entry : missing)
		{
			results.add(entry.getDate() + " missing " + entry.getMissing() + " values");
		}

		// Print elapsed time
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Added " + results.size() + " elements to LinkedList. Time: " + estimatedTime + " ms");

		// Return
		return results;
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

		if(isDateInData(dateFrom, dateTo))
		{

			// Variables to calculate
			double approved = 0;
			double notApproved = 0;

			// Iterate over the data and search for approved values
			for(Map.Entry<LocalDateTime, MeasurePoint> entry : dataMap.entrySet())
			{
				// When we find a date we are looking for, stop and do something
				if(!entry.getKey().toLocalDate().isAfter(dateTo) && !entry.getKey().toLocalDate().isBefore(dateFrom))
				{
					if(entry.getValue().isApproved())
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

		if(dateFrom.isAfter(dateTo))
		{
			System.out.println("End date appears to be after start date. Try again.");
			return false;
		}
		else if(dateTo.isBefore(dateFrom))
		{
			System.out.println("Start date appears to be before end date. Try again.");
			return false;
		}
		else if(first.toLocalDate().isAfter(dateFrom))
		{
			System.out.println(
					"Start date appears to be before first available data. First data in dataset: " + first.toString());
			return false;
		}
		else if(last.toLocalDate().isBefore(dateFrom))
		{
			System.out.println(
					"Start date appears to be after the last available data. Last data in dataset: " + last.toString());
			return false;
		}
		else if(last.toLocalDate().isBefore(dateTo))
		{
			System.out.println(
					"End date appears to be after the last available data. Last data in dataset: " + last.toString());
			return false;
		}
		else if(first.toLocalDate().isAfter(dateTo))
		{
			System.out.println("End date appears to be before the first available data. First data in dataset: "
					+ first.toString());
			return false;
		}
		return true;
	}

}