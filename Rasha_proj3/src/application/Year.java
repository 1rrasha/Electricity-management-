package application;
//1210773-Rasha Mansour

public class Year implements Comparable<Year> {
	// attributes
	private int year;
	private AVLTree<Month> tree;

	// constructor
	public Year(int year) {
		this.year = year;
		this.tree = new AVLTree<>();
	}

	// getters & setters
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public AVLTree<Month> getTree() {
		return tree;
	}

	// compareTo method
	@Override
	public int compareTo(Year otherYear) {
		return Integer.compare(this.year, otherYear.year);
	}

	// equals method
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Year other = (Year) obj;
		return year == other.year;
	}

	// to string method
	@Override
	public String toString() {
		return String.valueOf(year);
	}
}
