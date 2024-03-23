package application;

//1210773-Rasha Mansour
import java.util.Objects;

public class Month implements Comparable<Month> {
	// attributes
	private int month;
	private AVLTree<Day> tree;

	// constructor
	public Month(int month) {
		this.month = month;
		this.tree = new AVLTree<>();
	}

	// getters & setters
	public String getMonth() {
		switch (month) {
		case 1:
			return "January";
		case 2:
			return "February";
		case 3:
			return "March";
		case 4:
			return "April";
		case 5:
			return "May";
		case 6:
			return "June";
		case 7:
			return "July";
		case 8:
			return "August";
		case 9:
			return "September";
		case 10:
			return "October";
		case 11:
			return "November";
		case 12:
			return "December";
		default:
			return ""; // Handle invalid month
		}
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public AVLTree<Day> getTree() {
		return tree;
	}

	// compareTo method
	@Override
	public int compareTo(Month otherMonth) {
		return Integer.compare(this.month, otherMonth.month);
	}

	// equals method
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Month other = (Month) obj;
		return month == other.month;
	}

	// to string method
	@Override
	public String toString() {
		return String.valueOf(month);
	}
}
