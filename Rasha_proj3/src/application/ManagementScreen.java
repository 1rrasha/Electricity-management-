package application;

//1210773-Rasha Mansour
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Scanner;

public class ManagementScreen extends Application {

	// attributes
	private DatePicker datePicker;
	private TextField searchTextField;
	private Services services;
	TextField dateTextField;
	TextField israeliLinesTextField;
	TextField gazaPowerPlantTextField;
	TextField egyptianLinesTextField;
	TextField totalDailySupplyTextField;
	TextField overallDemandTextField;
	TextField powerCutsTextField;
	TextField tempTextField;
	SaveScreen saveScreen = new SaveScreen();
	TextArea resultTextArea;
	AVLTree<Year> avlTree = new AVLTree<>();

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) {

		services = new Services();

		primaryStage.setTitle(" Rasha Management Screen");

		// Title
		Label titleLabel = new Label("Management");
		titleLabel.setStyle(
				"-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

		HBox titleBox = new HBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY)));

		// File Section
		Label fileNameLabel = new Label("File Name:");
		fileNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black; -fx-font-weight: bold;");

		TextField fileTextField = new TextField();
		fileTextField.setEditable(false); // Make it not editable

		Button loadDataButton = new Button("Load Data");
		loadDataButton.setStyle(loadDataButton.getStyle() + "-fx-font-weight: bold;");
		loadDataButton.setOnAction(e -> loadDataFromFile(fileTextField));
		HBox fileBox = new HBox(fileNameLabel, fileTextField, loadDataButton);
		fileBox.setSpacing(10);

		// Buttons Section
		Button updateButton = createStyledButton("Update", "-fx-background-color: magenta;-fx-text-fill: white;");
		updateButton.setStyle(updateButton.getStyle() + "-fx-font-weight: bold;");
		updateButton.setOnAction(e -> updateData());

		Button insertButton = createStyledButton("Insert", "-fx-background-color: blue;-fx-text-fill: white;");
		insertButton.setStyle(insertButton.getStyle() + "-fx-font-weight: bold;");
		insertButton.setOnAction(e -> insertData());

		Button traverseButton = createStyledButton("Traverse", "-fx-background-color: purple; -fx-text-fill: white;");
		traverseButton.setStyle(traverseButton.getStyle() + "-fx-font-weight: bold;");
		traverseButton.setOnAction(e -> traverseAndDisplay());

		Button traverseAndDisplayYear = createStyledButton("Traverse Year",
				"-fx-background-color: cyan; -fx-text-fill: black;");
		traverseAndDisplayYear.setStyle(traverseAndDisplayYear.getStyle() + "-fx-font-weight: bold;");
		traverseAndDisplayYear.setOnAction(e -> traverseAndDisplayYear());

		Button traverseMonthButton = createStyledButton("Traverse month",
				"-fx-background-color: yellow; -fx-text-fill: black;");
		traverseMonthButton.setStyle(traverseMonthButton.getStyle() + "-fx-font-weight: bold;");
		traverseMonthButton.setOnAction(e -> traverseAndDisplaySelectedYear());

		Button traverseAndDisplaySelectedDay = createStyledButton("Traverse days",
				"-fx-background-color: lightgreen; -fx-text-fill: black;");
		traverseAndDisplaySelectedDay.setStyle(traverseAndDisplaySelectedDay.getStyle() + "-fx-font-weight: bold;");
		traverseAndDisplaySelectedDay.setOnAction(e -> traverseAndDisplaySelectedDay());

		Button displayHeightButton = createStyledButton("Height",
				"-fx-background-color: orange; -fx-text-fill: white;");
		displayHeightButton.setStyle(displayHeightButton.getStyle() + "-fx-font-weight: bold;");
		displayHeightButton.setOnAction(e -> displayTreeHeight());

		Button displayYearsHeightButton = createStyledButton("Years Height",
				"-fx-background-color: gray; -fx-text-fill: black;");
		displayYearsHeightButton.setStyle(displayYearsHeightButton.getStyle() + "-fx-font-weight: bold;");
		displayYearsHeightButton.setOnAction(e -> displayYearsTreeHeight());

		Button displayMonthsHeightButton = createStyledButton("Months Height",
				"-fx-background-color: pink; -fx-text-fill: black;");
		displayMonthsHeightButton.setStyle(displayMonthsHeightButton.getStyle() + "-fx-font-weight: bold;");
		displayMonthsHeightButton.setOnAction(e -> displayMonthsTreeHeight());

		Button displayDaysHeightButton = createStyledButton("Days Height",
				"-fx-background-color: lightblue; -fx-text-fill: black;");
		displayDaysHeightButton.setStyle(displayDaysHeightButton.getStyle() + "-fx-font-weight: bold;");
		displayDaysHeightButton.setOnAction(e -> displayDaysTreeHeight());

		Button deleteButton = createStyledButton("Delete", "-fx-background-color: red; -fx-text-fill: white;");
		deleteButton.setStyle(deleteButton.getStyle() + "-fx-font-weight: bold;");
		deleteButton.setOnAction(e -> deleteRecord());

		Button saveButton = createStyledButton("Save", "-fx-background-color: green;-fx-text-fill: white;");
		saveButton.setStyle(saveButton.getStyle() + "-fx-font-weight: bold;");
		saveButton.setOnAction(e -> saveDataToFile());
		HBox buttonsBox = new HBox(updateButton, insertButton, deleteButton, saveButton);
		buttonsBox.setSpacing(10);

		HBox saveBox = new HBox(traverseButton, traverseAndDisplayYear, traverseMonthButton,
				traverseAndDisplaySelectedDay);

		saveBox.setSpacing(20);

		HBox heightButtonsBox = new HBox(displayHeightButton, displayYearsHeightButton, displayMonthsHeightButton,
				displayDaysHeightButton);
		heightButtonsBox.setSpacing(10);

		// Date Search Section
		Label dateLabel = new Label("Date:");
		dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black; -fx-font-weight: bold;");
		searchTextField = new TextField();
		Button searchButton = new Button("Search");
		searchButton.setStyle(searchButton.getStyle() + "-fx-font-weight: bold;");
		searchButton.setOnAction(e -> searchData());
		HBox searchBox = new HBox(dateLabel, searchTextField, searchButton);
		searchBox.setSpacing(10);

		// Calendar Section
		datePicker = new DatePicker();
		datePicker.setShowWeekNumbers(true);
		HBox calendarBox = new HBox(datePicker);
		calendarBox.setSpacing(10);

		// Form Section
		GridPane form = createForm();
		VBox formBox = new VBox(form);
		// Result Section
		resultTextArea = new TextArea();
		resultTextArea.setEditable(false);
		resultTextArea.setWrapText(true);
		resultTextArea.setPrefHeight(100);

		// main layout
		VBox mainLayout = new VBox(titleBox, fileBox, searchBox, calendarBox, formBox, buttonsBox, saveBox,
				heightButtonsBox, resultTextArea);
		mainLayout.setSpacing(10);
		mainLayout.setPadding(new Insets(10));

		Scene scene = new Scene(mainLayout, 800, 700);
		Image icon = new Image(
				new File("C:\\Users\\user\\Desktop\\java\\Rasha_proj3\\mangment.png").toURI().toString());

		// Set the icon for the stage
		primaryStage.getIcons().add(icon);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// methods for help

	private void saveDataToFile() {

		String updatedData = services.getUpdatedData();

		SaveScreen saveScreen = new SaveScreen();

		saveScreen.show();

		saveScreen.setTextFieldText(updatedData);

		saveScreen.saveDataToFile();

		showAlert1("Success", "Data saved successfully!");

		// Close the SaveScreen
		saveScreen.close();
	}

	private Button createStyledButton(String text, String style) {
		Button button = new Button(text);
		button.setStyle(style);
		button.setOnAction(e -> handleButtonAction(text));
		return button;
	}

	private GridPane createForm() {
		GridPane form = new GridPane();
		form.setHgap(10);
		form.setVgap(10);

		// Date
		Label dateLabel = new Label("Date:");
		dateTextField = new TextField();
		form.addRow(0, dateLabel, dateTextField);

		// Israeli Lines MWs
		Label israeliLinesLabel = new Label("Israeli Lines MWs:");
		israeliLinesTextField = new TextField();
		form.addRow(1, israeliLinesLabel, israeliLinesTextField);

		// Gaza Power Plant MWs
		Label gazaPowerPlantLabel = new Label("Gaza Power Plant MWs:");
		gazaPowerPlantTextField = new TextField();
		form.addRow(2, gazaPowerPlantLabel, gazaPowerPlantTextField);

		// Egyptian Lines MWs
		Label egyptianLinesLabel = new Label("Egyptian Lines MWs:");
		egyptianLinesTextField = new TextField();
		form.addRow(3, egyptianLinesLabel, egyptianLinesTextField);

		// Total Daily Supply Available in MWs
		Label totalDailySupplyLabel = new Label("Total Daily Supply (MWs):");
		totalDailySupplyTextField = new TextField();
		form.addRow(4, totalDailySupplyLabel, totalDailySupplyTextField);

		// Overall Demand in MWs
		Label overallDemandLabel = new Label("Overall Demand (MWs):");
		overallDemandTextField = new TextField();
		form.addRow(5, overallDemandLabel, overallDemandTextField);

		// Power Cuts Hours per Day (400mg)
		Label powerCutsLabel = new Label("Power Cuts Hours per Day (400mg):");
		powerCutsTextField = new TextField();
		form.addRow(6, powerCutsLabel, powerCutsTextField);

		// Temp
		Label tempLabel = new Label("Temperature:");
		tempTextField = new TextField();
		form.addRow(7, tempLabel, tempTextField);

		return form;
	}

	// method to handle each button a method
	private void handleButtonAction(String buttonName) {
		switch (buttonName) {
		case "Update":
			updateData();
			break;
		case "Insert":
			insertData();
			break;
		case "Delete":
			deleteRecord();
			break;
		case "Search":
			searchData();
			break;
		default:
			System.out.println("Unhandled button: " + buttonName);
		}
	}

	// method to load data from the file
	private void loadDataFromFile(TextField fileTextField) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Electricity Data File");
		File file = fileChooser.showOpenDialog(null);

		if (file != null) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String headerLine = reader.readLine();
				if (headerLine != null) {
					String[] headers = headerLine.split("\t");
					if (headers.length != 8) {
						showAlert("Error!", "Invalid file format. Please check the number of columns.");
						return;
					}
				}

				while (reader.ready()) {
					String line = reader.readLine();
					System.out.println(line);
					String[] data = line.split("\t");

					if (data.length == 8) {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
						LocalDate date = LocalDate.parse(data[0], formatter);
						double israeliLinesMWs = Double.parseDouble(data[1]);
						double gazaPowerPlantMWs = Double.parseDouble(data[2]);
						double egyptianLinesMWs = Double.parseDouble(data[3]);
						double totalDailySupplyMWs = Double.parseDouble(data[4]);
						double overallDemandMWs = Double.parseDouble(data[5]);
						double powerCutsHoursPerDay = Double.parseDouble(data[6]);
						double temperature = Double.parseDouble(data[7]);

						// Check if the Year node already exists
						Year yearNode = new Year(date.getYear());
						if (!avlTree.contains(yearNode)) {
							avlTree.insert(yearNode);
						} else {
							yearNode = avlTree.search(yearNode);
						}

						// Check if the Month node already exists
						Month monthNode = new Month(date.getMonthValue());
						AVLTree<Month> monthTree = yearNode.getTree();
						if (!monthTree.contains(monthNode)) {
							monthTree.insert(monthNode);
						} else {
							monthNode = monthTree.search(monthNode);
						}

						// Check if the Day node already exists
						Day dayNode = new Day(date.getDayOfMonth());
						AVLTree<Day> dayTree = monthNode.getTree();
						if (!dayTree.contains(dayNode)) {
							dayTree.insert(dayNode);
						} else {
							// Update the existing Day node with the new ElectricityRecords data
							dayNode = dayTree.search(dayNode);
							dayNode.setElectricityRecords(
									new ElectricityRecords(date, israeliLinesMWs, gazaPowerPlantMWs, egyptianLinesMWs,
											totalDailySupplyMWs, overallDemandMWs, powerCutsHoursPerDay, temperature));
						}

						// Add ElectricityRecords to your services
						services.addElectricityInfo(
								new ElectricityRecords(date, israeliLinesMWs, gazaPowerPlantMWs, egyptianLinesMWs,
										totalDailySupplyMWs, overallDemandMWs, powerCutsHoursPerDay, temperature));
					} else {
						System.out.println("Invalid line format: " + line);
					}
				}

				fileTextField.setText(file.getName());
				showAlert1("Success^-^", "Data loaded successfully!");
			} catch (IOException e) {
				e.printStackTrace();
				showAlert("Error!", "An error occurred while reading the file.");
			} catch (DateTimeParseException | NumberFormatException e) {
				e.printStackTrace();
				showAlert("Error!", "Error parsing the file. Please check the file format.");
			}
		}
	}

	// method to search if there is data for the selected date
	private void searchData() {
		try {
			LocalDate date;
			if (datePicker.getValue() != null) {
				date = datePicker.getValue();
			} else {
				String dateString = searchTextField.getText();
				if (dateString.isEmpty()) {
					showAlert("Error!", "Please enter a date for search.");
					return;
				}
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
				date = LocalDate.parse(dateString, inputFormatter);
			}

			DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
			String formattedDate = date.format(dataFormatter);

			System.out.println("Searching for date: " + formattedDate);

			ElectricityRecords result = services.search(date);

			if (result != null) {
				updateForm(result);
				showAlert1("Success^-^", "Data found for the selected date.");

			} else {
				showAlert("Not Found!", "No data found for the selected date.");
			}
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			showAlert("Error!", "Invalid date format. Please enter a valid date.");
		}
	}

	// method to update the records
	private void updateData() {
		LocalDate dateToUpdate = datePicker.getValue();

		// Check if the date is null
		if (dateToUpdate == null && searchTextField.getText().isEmpty()) {
			showAlert("Error!", "Please select a date for update.");
			return;
		}

		// Check if the date is null in datePicker and searchTextField
		if (dateToUpdate == null) {
			try {
				// If the datePicker is null, try to get the date from the searchTextField
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
				dateToUpdate = LocalDate.parse(searchTextField.getText(), inputFormatter);
			} catch (DateTimeParseException e) {
				showAlert("Error", "Invalid date format. Please select a valid date.");
				return;
			}
		}

		ElectricityRecords updatedInfo = createElectricityRecords();

		// Validate the fields before updating
		if (validateFields(updatedInfo)) {
			// Update the data in the tree
			Year yearNode = new Year(dateToUpdate.getYear());
			Month monthNode = new Month(dateToUpdate.getMonthValue());
			Day dayNode = new Day(dateToUpdate.getDayOfMonth());

			if (avlTree.contains(yearNode)) {
				yearNode = avlTree.search(yearNode);

				if (yearNode.getTree().contains(monthNode)) {
					monthNode = yearNode.getTree().search(monthNode);

					if (monthNode.getTree().contains(dayNode)) {
						dayNode = monthNode.getTree().search(dayNode);
						dayNode.setElectricityRecords(updatedInfo);

						// Update the data in your services
						services.updateElectricityInfo(dateToUpdate, updatedInfo);

						showAlert1("Success", "Data updated successfully!");
						clearDateFields();
						return;
					}
				}
			}

			showAlert("Error", "No data found for the selected date.");
		}
	}

	// method to insert the records
	private void insertData() {
		LocalDate date = datePicker.getValue();

		// Check if the date is null in datePicker and searchTextField
		if (date == null && searchTextField.getText().isEmpty()) {
			showAlert("Error!", "Please select a date for insert.");
			return;
		}

		// Check if the date is null in datePicker and searchTextField
		if (date == null) {
			try {
				// If the datePicker is null, try the date from the searchTextField
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
				date = LocalDate.parse(searchTextField.getText(), inputFormatter);
			} catch (DateTimeParseException e) {
				showAlert("Error!", "Invalid date format. Please select a valid date.");
				return;
			}
		}

		ElectricityRecords newRecord = createElectricityRecords();

		// Validate the fields before inserting
		if (validateFields(newRecord)) {
			// Check if the data already exists
			if (services.search(date) != null) {
				showAlert("Error!", "Data for the selected date already exists.");
				return;
			}

			// Insert the data into the tree
			Year yearNode = new Year(date.getYear());
			Month monthNode = new Month(date.getMonthValue());
			Day dayNode = new Day(date.getDayOfMonth());

			if (!avlTree.contains(yearNode)) {
				avlTree.insert(yearNode);
			}

			yearNode = avlTree.search(yearNode);

			if (!yearNode.getTree().contains(monthNode)) {
				yearNode.getTree().insert(monthNode);
			}

			monthNode = yearNode.getTree().search(monthNode);

			if (!monthNode.getTree().contains(dayNode)) {
				monthNode.getTree().insert(dayNode);
			}

			dayNode = monthNode.getTree().search(dayNode);
			dayNode.setElectricityRecords(newRecord);

			// Add ElectricityRecords
			services.addElectricityInfo(newRecord);

			showAlert1("Success", "Data inserted successfully!");
			clearDateFields();
		}
	}

	// Validate fields
	private boolean validateFields(ElectricityRecords record) {
		if (record.getIsraeli_Lines_MWs() < 0 || record.getGaza_Power_Plant_MWs() < 0
				|| record.getEgyptian_Lines_MWs() < 0 || record.getTotal_daily_Supply_available_in_MWs() < 0
				|| record.getOverall_demand_in_MWs() < 0 || record.getPower_Cuts_hours_day_400mg() < 0
				|| record.getTemp() < 0) {
			showAlert("Error!", "Invalid input. Please enter non-negative values.");
			return false;
		}
		return true;
	}

	// method to delete the records
	private void deleteRecord() {
		LocalDate dateToDelete = datePicker.getValue();

		// Check if the date is null
		if (dateToDelete == null && searchTextField.getText().isEmpty()) {
			showAlert("Error!", "Please select a date for delete.");
			return;
		}

		// Check if the date is null in datePicker and searchTextField
		if (dateToDelete == null) {
			try {
				// If the datePicker is null, try to parse the date from the searchTextField
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
				dateToDelete = LocalDate.parse(searchTextField.getText(), inputFormatter);
			} catch (DateTimeParseException e) {
				showAlert("Error", "Invalid date format. Please select a valid date.");
				return;
			}
		}

		// Check if the record exists in services
		boolean success = services.deleteElectricityInfo(dateToDelete);

		if (success) {
			// Delete the nodes in the AVL tree
			Year yearNode = new Year(dateToDelete.getYear());
			Month monthNode = new Month(dateToDelete.getMonthValue());
			Day dayNode = new Day(dateToDelete.getDayOfMonth());

			if (avlTree.contains(yearNode)) {
				yearNode = avlTree.search(yearNode);

				if (yearNode.getTree().contains(monthNode)) {
					monthNode = yearNode.getTree().search(monthNode);

					if (monthNode.getTree().contains(dayNode)) {
						monthNode.getTree().delete(dayNode);

						showAlert1("Success^-^", "Data deleted successfully!");

						dateTextField.clear();
						israeliLinesTextField.clear();
						gazaPowerPlantTextField.clear();
						egyptianLinesTextField.clear();
						totalDailySupplyTextField.clear();
						overallDemandTextField.clear();
						powerCutsTextField.clear();
						tempTextField.clear();

						clearDateFields();
						return;
					}
				}
			}
		}

		showAlert("Not Found!", "No data found for the selected date.");
	}

	// method to update form
	private void updateForm(ElectricityRecords data) {

		dateTextField.setText(String.valueOf(data.getDate()));
		israeliLinesTextField.setText(String.valueOf(data.getIsraeli_Lines_MWs()));
		gazaPowerPlantTextField.setText(String.valueOf(data.getGaza_Power_Plant_MWs()));
		egyptianLinesTextField.setText(String.valueOf(data.getEgyptian_Lines_MWs()));
		totalDailySupplyTextField.setText(String.valueOf(data.getTotal_daily_Supply_available_in_MWs()));
		overallDemandTextField.setText(String.valueOf(data.getOverall_demand_in_MWs()));
		powerCutsTextField.setText(String.valueOf(data.getPower_Cuts_hours_day_400mg()));
		tempTextField.setText(String.valueOf(data.getTemp()));
	}

	// method to create a electricity record form
	private ElectricityRecords createElectricityRecords() {
		LocalDate date = datePicker.getValue();

		// Check if the date is null in datePicker and searchTextField
		if (date == null && !searchTextField.getText().isEmpty()) {
			try {

				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
				date = LocalDate.parse(searchTextField.getText(), inputFormatter);
			} catch (DateTimeParseException e) {
				showAlert("Error!", "Invalid date format. Please select a valid date.");
				return null;
			}
		}

		if (date == null) {
			showAlert("Error!", "Please select a date.");
			return null;
		}

		double israeliLinesMWs = Double.parseDouble(israeliLinesTextField.getText());
		double gazaPowerPlantMWs = Double.parseDouble(gazaPowerPlantTextField.getText());
		double egyptianLinesMWs = Double.parseDouble(egyptianLinesTextField.getText());
		double totalDailySupplyMWs = Double.parseDouble(totalDailySupplyTextField.getText());
		double overallDemandMWs = Double.parseDouble(overallDemandTextField.getText());
		double powerCutsHoursPerDay = Double.parseDouble(powerCutsTextField.getText());
		double temperature = Double.parseDouble(tempTextField.getText());

		return new ElectricityRecords(date, israeliLinesMWs, gazaPowerPlantMWs, egyptianLinesMWs, totalDailySupplyMWs,
				overallDemandMWs, powerCutsHoursPerDay, temperature);
	}

////////////////////////////////////////////////////////////////////////////
	// traverse mathod
	private void traverseAndDisplay() {
		StringBuilder result = new StringBuilder();

		// Traverse and display Years AVL Tree
		result.append("Years AVL Tree:\n");
		for (Year yearNode : avlTree) {
			result.append(yearNode).append(" ");
		}

		// Traverse and display Months AVL Trees within each Year
		for (Year yearNode : avlTree) {
			AVLTree<Month> monthTree = yearNode.getTree();
			result.append("\nMonths AVL Tree for Year ").append(yearNode.getYear()).append(":\n");
			for (Month monthNode : monthTree) {
				result.append(monthNode).append(" ");
			}

			// Traverse and display Days AVL Trees within each Month and Year
			for (Month monthNode : monthTree) {
				AVLTree<Day> dayTree = monthNode.getTree();
				result.append("\nDays AVL Tree for Year ").append(yearNode.getYear()).append(", Month ")
						.append(monthNode.getMonth()).append(":\n");
				for (Day dayNode : dayTree) {
					result.append(dayNode).append(" ");
				}
			}
		}

		// Display the result in the TextArea
		resultTextArea.setText(result.toString());
		System.out.println(result.toString());
	}

//////////////////////////////////////////////////////////////////////////////////////
	// traverse method FOR YEARS
	private void traverseAndDisplayYear() {
		StringBuilder result = new StringBuilder();

		Queue<AVLNode<Year>> queue = new LinkedList<>();
		queue.offer(avlTree.getRoot());

		while (!queue.isEmpty()) {
			int levelSize = queue.size();

			for (int i = 0; i < levelSize; i++) {
				AVLNode<Year> currentNode = queue.poll();
				result.append(currentNode.data).append(" ");

				// Enqueue left child
				if (currentNode.left != null) {
					queue.offer(currentNode.left);
				}

				// Enqueue right child
				if (currentNode.right != null) {
					queue.offer(currentNode.right);
				}
			}

			result.append("\n");
		}

		// Display the result in the TextArea
		resultTextArea.setText(result.toString());
		System.out.println(result.toString());
	}

	//////////////////////////////////////////////////////////////

	// traverse method FOR MONTHS

	private void traverseAndDisplayOneYear(int selectedYear) {
		Year yearNode = new Year(selectedYear);

		if (avlTree.contains(yearNode)) {
			yearNode = avlTree.search(yearNode);
			AVLTree<Month> monthTree = yearNode.getTree();

			StringBuilder result = new StringBuilder();
			Queue<AVLNode<Month>> queue = new LinkedList<>();
			queue.offer(monthTree.getRoot());

			while (!queue.isEmpty()) {
				int levelSize = queue.size();

				for (int i = 0; i < levelSize; i++) {
					AVLNode<Month> currentNode = queue.poll();
					result.append(currentNode.data.getMonth()).append(" "); // Use getMonthName()

					// Enqueue left child
					if (currentNode.left != null) {
						queue.offer(currentNode.left);
					}

					// Enqueue right child
					if (currentNode.right != null) {
						queue.offer(currentNode.right);
					}
				}

				result.append("\n");
			}

			// Display the result in the TextArea
			resultTextArea.setText(result.toString());
		} else {
			showAlert("Error", "No data found for the selected year.");
		}
	}

	private void traverseAndDisplaySelectedYear() {
		try {
			int selectedYear = Integer.parseInt(showInputYearDialog());
			traverseAndDisplayOneYear(selectedYear);
		} catch (NumberFormatException e) {
			showAlert("Error", "Invalid year format. Please enter a valid number.");
		}
	}

//////////////////////////////////////////////////////////////////////////////
	// traverse method FOR DAYS

	private void traverseAndDisplaySelectedDay() {
		try {
			int selectedYear = Integer.parseInt(showInputYearDialog());
			int selectedMonth = Integer.parseInt(showInputMonthDialog());

			traverseAndDisplayOneMonth(selectedYear, selectedMonth);
		} catch (NumberFormatException e) {
			showAlert("Error", "Invalid year or month format. Please enter valid numbers.");
		}
	}

	private String showInputYearDialog() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Enter Year");
		dialog.setHeaderText(null);
		dialog.setContentText("Please enter the year:");

		Optional<String> result = dialog.showAndWait();
		return result.orElse("");
	}

	private String showInputMonthDialog() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Enter Month");
		dialog.setHeaderText(null);
		dialog.setContentText("Please enter the month:");

		Optional<String> result = dialog.showAndWait();
		return result.orElse("");
	}

	private void traverseAndDisplayOneMonth(int selectedYear, int selectedMonth) {
		Year yearNode = new Year(selectedYear);
		Month monthNode = new Month(selectedMonth);

		if (avlTree.contains(yearNode)) {
			yearNode = avlTree.search(yearNode);
			AVLTree<Month> monthTree = yearNode.getTree();

			if (monthTree.contains(monthNode)) {
				monthNode = monthTree.search(monthNode);

				StringBuilder result = new StringBuilder();
				Queue<AVLNode<Day>> queue = new LinkedList<>();
				queue.offer(monthNode.getTree().getRoot());

				while (!queue.isEmpty()) {
					int levelSize = queue.size();

					for (int i = 0; i < levelSize; i++) {
						AVLNode<Day> currentNode = queue.poll();
						result.append(currentNode.data).append(" ");

						// Enqueue left child
						if (currentNode.left != null) {
							queue.offer(currentNode.left);
						}

						// Enqueue right child
						if (currentNode.right != null) {
							queue.offer(currentNode.right);
						}
					}

					result.append("\n");
				}

				// Display the result in the TextArea
				resultTextArea.setText(result.toString());
			} else {
				showAlert("Error", "No data found for the selected month in the specified year.");
			}
		} else {
			showAlert("Error", "No data found for the selected year.");
		}
	}

	///////////////////////////////////////////////////////////////////////////////
	// trees height method
	private void displayTreeHeight() {
		StringBuilder result = new StringBuilder();

		// Display height of Years AVL Tree
		result.append("Years AVL Tree Height: ").append(avlTree.avlTreeHeight());

		// Display height of Months AVL Trees within each Year
		for (Year yearNode : avlTree) {
			AVLTree<Month> monthTree = yearNode.getTree();
			result.append("\nMonths AVL Tree Height for Year ").append(yearNode.getYear()).append(": ")
					.append(monthTree.avlTreeHeight());

			// Display height of Days AVL Trees within each Month and Year
			for (Month monthNode : monthTree) {
				AVLTree<Day> dayTree = monthNode.getTree();
				result.append("\nDays AVL Tree Height for Year ").append(yearNode.getYear()).append(", Month ")
						.append(monthNode.getMonth()).append(": ").append(dayTree.avlTreeHeight());
			}
		}

		// Display the result in the TextArea
		resultTextArea.setText(result.toString());
		System.out.println(result.toString());
	}

	////////////////////////////////////////////////////////////////
	private void displayYearsTreeHeight() {
		// Display height of Years AVL Tree
		resultTextArea.setText("Years AVL Tree Height: " + avlTree.avlTreeHeight());
	}

	private void displayMonthsTreeHeight() {
		try {
			int selectedYear = Integer.parseInt(showInputYearDialog());
			Year yearNode = new Year(selectedYear);

			if (avlTree.contains(yearNode)) {
				yearNode = avlTree.search(yearNode);
				AVLTree<Month> monthTree = yearNode.getTree();

				// Display height of Months AVL Tree for the selected Year
				resultTextArea.setText(
						"Months AVL Tree Height for Year " + yearNode.getYear() + ": " + monthTree.avlTreeHeight());
			} else {
				showAlert("Error", "No data found for the selected year.");
			}
		} catch (NumberFormatException e) {
			showAlert("Error", "Invalid year format. Please enter a valid number.");
		}
	}

	private void displayDaysTreeHeight() {
		try {
			int selectedYear = Integer.parseInt(showInputYearDialog());
			int selectedMonth = Integer.parseInt(showInputMonthDialog());

			Year yearNode = new Year(selectedYear);
			Month monthNode = new Month(selectedMonth);

			if (avlTree.contains(yearNode)) {
				yearNode = avlTree.search(yearNode);
				AVLTree<Month> monthTree = yearNode.getTree();

				if (monthTree.contains(monthNode)) {
					monthNode = monthTree.search(monthNode);
					AVLTree<Day> dayTree = monthNode.getTree();

					// Display height of Days AVL Tree for the selected Year and Month
					resultTextArea.setText("Days AVL Tree Height for Year " + yearNode.getYear() + ", Month "
							+ monthNode.getMonth() + ": " + dayTree.avlTreeHeight());
				} else {
					showAlert("Error", "No data found for the selected month in the specified year.");
				}
			} else {
				showAlert("Error", "No data found for the selected year.");
			}
		} catch (NumberFormatException e) {
			showAlert("Error", "Invalid year or month format. Please enter valid numbers.");
		}
	}

//////////////////////////////////////////////////////////////////////
	private String showInputDayDialog() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Enter Day");
		dialog.setHeaderText(null);
		dialog.setContentText("Please enter the day:");

		Optional<String> result = dialog.showAndWait();
		return result.orElse("");
	}

	// method to clear the date field
	private void clearDateFields() {
		datePicker.setValue(null);
		searchTextField.clear();
	}

	// alert method
	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);

		alert.setTitle(title);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void showAlert1(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

		alert.setTitle(title);
		alert.setContentText(content);
		alert.showAndWait();
	}
/////////////////////////////////////////////////////////////////////////
//	private void displayDaysTreeHeight() {
//	try {
//		int selectedYear = Integer.parseInt(showInputYearDialog());
//		int selectedMonth = Integer.parseInt(showInputMonthDialog());
//
//		traverseAndDisplayOneMonth(selectedYear, selectedMonth);
//
//		int selectedDay = Integer.parseInt(showInputDayDialog());
//
//		Year yearNode = new Year(selectedYear);
//		Month monthNode = new Month(selectedMonth);
//
//		if (avlTree.contains(yearNode)) {
//			yearNode = avlTree.search(yearNode);
//			AVLTree<Month> monthTree = yearNode.getTree();
//
//			if (monthTree.contains(monthNode)) {
//				monthNode = monthTree.search(monthNode);
//				AVLTree<Day> dayTree = monthNode.getTree();
//
//				// Create a dummy Day node for comparison
//				Day searchDay = new Day(selectedDay);
//
//				if (dayTree.contains(searchDay)) {
//					// Retrieve the actual Day node from the tree
//					Day actualDayNode = dayTree.search(searchDay);
//
//					// Display height of Days AVL Tree for the selected Year, Month, and Day
//					resultTextArea.setText("Days AVL Tree Height for Year " + yearNode.getYear() + ", Month "
//							+ monthNode.getMonth() + ", Day " + selectedDay + ": " + dayTree.avlTreeHeight());
//				} else {
//					showAlert("Error", "No data found for the selected day in the specified month and year.");
//				}
//			} else {
//				showAlert("Error", "No data found for the selected month in the specified year.");
//			}
//		} else {
//			showAlert("Error", "No data found for the selected year.");
//		}
//	} catch (NumberFormatException e) {
//		showAlert("Error", "Invalid year, month, or day format. Please enter valid numbers.");
//	}
//}
}
