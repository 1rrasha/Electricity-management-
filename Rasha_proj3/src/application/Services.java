package application;

//1210773-Rasha Mansour
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Iterator;
import java.util.LinkedList;

public class Services {

	// attributes
	private AVLTree<Year> years;

	// constructor
	public Services() {
		years = new AVLTree<>();

	}

	// setters & getters
	public AVLTree<Year> getYears() {
		return years;
	}

	public void setYears(AVLTree<Year> years) {
		this.years = years;
	}

	public AVLTree<Year> getYearsTree() {
		return years;
	}

	public AVLTree<Month> getMonthTree(int year) {
		Year yearNode = years.search(new Year(year));
		return (yearNode != null) ? yearNode.getTree() : null;
	}

	public AVLTree<Day> getDayTree(int year, int month) {
		Year yearNode = years.search(new Year(year));
		if (yearNode != null) {
			Month monthNode = yearNode.getTree().search(new Month(month));
			return (monthNode != null) ? monthNode.getTree() : null;
		}
		return null;
	}

	// methods

	// search
	public ElectricityRecords search(LocalDate date) {
		int yearValue = date.getYear();
		Year year = years.search(new Year(yearValue));

		if (year != null) {

			AVLTree<Month> months = year.getTree();
			int monthValue = date.getMonthValue();
			Month month = months.search(new Month(monthValue));
			System.out.println("Found Year: " + yearValue);

			if (month != null) {
				System.out.println("Found month: " + monthValue);

				AVLTree<Day> days = month.getTree();
				int dayValue = date.getDayOfMonth();
				Day day = days.search(new Day(dayValue));
				System.out.println("Found Day: " + dayValue);
				days.inOrderTraversal();
				if (day != null) {
					return day.getElectricityRecords();
				}
			}
		}

		return null; // Not found
	}

	// add
	public void addElectricityInfo(ElectricityRecords electricityInfo) {
		int yearValue = electricityInfo.getDate().getYear();
		Year yearToFind = new Year(yearValue);
		Year year = years.search(yearToFind);

		if (year == null) {
			// If the year is not found, create a new Year object
			year = new Year(yearValue);
			years.insert(year);
		}

		int monthValue = electricityInfo.getDate().getMonthValue();
		Month monthToFind = new Month(monthValue);
		Month month = year.getTree().search(monthToFind);

		if (month == null) {
			// If the month is not found, create a new Month object
			month = new Month(monthValue);
			year.getTree().insert(month);
		}

		int dayValue = electricityInfo.getDate().getDayOfMonth();
		Day day = new Day(electricityInfo, dayValue);
		month.getTree().insert(day);
	}

	// Delete
	public boolean deleteElectricityInfo(LocalDate date) {
		if (date != null) {
			Year year = years.search(new Year(date.getYear()));
			if (year != null) {
				Month month = year.getTree().search(new Month(date.getMonthValue()));
				if (month != null) {
					Day day = month.getTree().search(new Day(date.getDayOfMonth()));
					if (day != null) {
						month.getTree().delete(day);
						return true;
					} else {
						System.out.println("Cannot delete record. Day not found.");
					}
				} else {
					System.out.println("Cannot delete record. Month not found.");
				}
			} else {
				System.out.println("Cannot delete record. Year not found.");
			}
		} else {
			System.out.println("Cannot delete record. Input date is null.");
		}

		return false;
	}

	// Update
	public void updateElectricityInfo(LocalDate date, ElectricityRecords updatedInfo) {
		if (date == null || updatedInfo == null) {
			System.out.println("Invalid input for updateElectricityInfo.");
			return;
		}

		// delete the record for the date
		deleteElectricityInfo(date);

		// add the updated information
		addElectricityInfo(updatedInfo);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public ElectricityStats getStats(String selectedOption, String selectedColumn, DataElement selectedData) {
		double sum = 0;
		double avg = 0;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		switch (selectedOption.toLowerCase()) {
		case "day":
			if (selectedData.getData() instanceof Day) {
				Day selectedDay = (Day) selectedData.getData();
				ElectricityRecords electricityRecords = selectedDay.getElectricityRecords();
				double columnValue = getColumnValue(electricityRecords, selectedColumn);

				// Update sum, min, and max
				sum += columnValue;
				min = Math.min(min, columnValue);
				max = Math.max(max, columnValue);
			}
			break;

		case "month":
			if (selectedData.getData() instanceof Month) {
				Month selectedMonth = (Month) selectedData.getData();
				AVLTree<Day> days = selectedMonth.getTree();
				for (Day day : days) {
					ElectricityRecords electricityRecords = day.getElectricityRecords();
					double columnValue = getColumnValue(electricityRecords, selectedColumn);

					// Update sum, min, and max
					sum += columnValue;
					min = Math.min(min, columnValue);
					max = Math.max(max, columnValue);
				}
			}
			break;

		case "year":
			if (selectedData.getData() instanceof Year) {
				Year selectedYear = (Year) selectedData.getData();
				AVLTree<Month> months = selectedYear.getTree();
				for (Month month : months) {
					AVLTree<Day> days = month.getTree();
					for (Day day : days) {
						ElectricityRecords electricityRecords = day.getElectricityRecords();
						double columnValue = getColumnValue(electricityRecords, selectedColumn);

						// Update sum, min, and max
						sum += columnValue;
						min = Math.min(min, columnValue);
						max = Math.max(max, columnValue);
					}
				}
			}
			break;

		case "all data":
			// Calculate total stats for all data
			for (Year year : years) {
				AVLTree<Month> months = year.getTree();
				for (Month month : months) {
					AVLTree<Day> days = month.getTree();
					for (Day day : days) {
						ElectricityRecords electricityRecords = day.getElectricityRecords();
						double columnValue = getColumnValue(electricityRecords, selectedColumn);

						// Update sum, min, and max
						sum += columnValue;
						min = Math.min(min, columnValue);
						max = Math.max(max, columnValue);
					}
				}
			}
			break;

		default:
			throw new IllegalArgumentException("Invalid option: " + selectedOption);
		}

		// Average
		int totalElements = getTotalElements(selectedOption, selectedData);
		avg = sum / totalElements;

		// Give the value
		ElectricityStats electricityStats = new ElectricityStats();
		electricityStats.setSum(sum);
		electricityStats.setAvg(avg);
		electricityStats.setMin(min);
		electricityStats.setMax(max);

		return electricityStats;
	}

	private int getTotalElements(String selectedOption, DataElement selectedData) {
		int totalElements = 0;

		switch (selectedOption.toLowerCase()) {
		case "day":
			totalElements = 1;
			break;
		case "month":
			if (selectedData.getData() instanceof Month) {
				Month selectedMonth = (Month) selectedData.getData();
				totalElements = selectedMonth.getTree().size();
			}
			break;
		case "year":
			if (selectedData.getData() instanceof Year) {
				Year selectedYear = (Year) selectedData.getData();
				totalElements = selectedYear.getTree().size() * 31;
			}
			break;
		case "all data":
			totalElements = years.size() * 12 * 31;
			break;
		default:
			throw new IllegalArgumentException("Invalid option: " + selectedOption);
		}

		return totalElements;
	}

	// method to get column
	private double getColumnValue(ElectricityRecords electricityRecords, String selectedColumn) {

		switch (selectedColumn) {
		case "Israeli_Lines_MWs":
			return electricityRecords.getIsraeli_Lines_MWs();
		case "Gaza_Power_Plant_MWs":
			return electricityRecords.getGaza_Power_Plant_MWs();
		case "Egyptian Lines":
			return electricityRecords.getEgyptian_Lines_MWs();
		case "Total daily Supply available":
			return electricityRecords.getTotal_daily_Supply_available_in_MWs();
		case "Overall demand":
			return electricityRecords.getOverall_demand_in_MWs();
		case "Power Cuts hours day":
			return electricityRecords.getPower_Cuts_hours_day_400mg();
		case "Temp":
			return electricityRecords.getTemp();
		default:
			throw new IllegalArgumentException("Invalid column name: " + selectedColumn);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////

	// method to get updated data as a table
	public String getUpdatedData() {
		StringBuilder updatedData = new StringBuilder();

		// Add column headers
		updatedData.append("Date\t\tIsraeli_Lines_MWs\tGaza_Power_Plant_MWs\tEgyptian_Lines_MWs\t"
				+ "Total_daily_Supply_available_in_MWs\tOverall_demand_in_MWs\t" + "Power_Cuts_hours_day_400mg\tTemp\n")
				.append("                                                                                                                       ");

		// Iterate through years
		for (Year year : years) {
			AVLTree<Month> months = year.getTree();

			// Iterate through months
			for (Month month : months) {
				AVLTree<Day> days = month.getTree();

				// Iterate through days
				for (Day day : days) {
					ElectricityRecords electricityRecords = day.getElectricityRecords();
					updatedData.append(formatElectricityRecords(electricityRecords)).append("\n");
				}

			}
		}

		return updatedData.toString();
	}

	// Helper method to format ElectricityRecords

	private String formatElectricityRecords(ElectricityRecords electricityRecords) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
		String formattedDate = electricityRecords.getDate().format(formatter);

		return String.format("\t%s\t\t%-20s\t%-20s\t%-20s\t%-30s\t%-20s\t%-20s\t%s", formattedDate,
				electricityRecords.getIsraeli_Lines_MWs(), electricityRecords.getGaza_Power_Plant_MWs(),
				electricityRecords.getEgyptian_Lines_MWs(), electricityRecords.getTotal_daily_Supply_available_in_MWs(),
				electricityRecords.getOverall_demand_in_MWs(), electricityRecords.getPower_Cuts_hours_day_400mg(),
				electricityRecords.getTemp());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
//	private String formatElectricityRecords(ElectricityRecords electricityRecords) {
//	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
//	String formattedDate = electricityRecords.getDate().format(formatter);
//
//	return String.format("%s\t\t%-25s\t%-30s\t%-20s\t%-30s\t%-20s\t%-25s\t%s", formattedDate,
//			electricityRecords.getIsraeli_Lines_MWs(), electricityRecords.getGaza_Power_Plant_MWs(),
//			electricityRecords.getEgyptian_Lines_MWs(), electricityRecords.getTotal_daily_Supply_available_in_MWs(),
//			electricityRecords.getOverall_demand_in_MWs(), electricityRecords.getPower_Cuts_hours_day_400mg(),
//			electricityRecords.getTemp());
//}
}