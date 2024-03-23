package application;
//1210773-Rasha Mansour

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StatisticsScreen extends Application {
	// attributes
	private Services services;
	ComboBox<String> columnChoiceBox;
	Button fileChooserButton;
	ComboBox<DataElement> dataChoiceBox = new ComboBox<>();
	TextField totalTextField;
	TextField averageTextField;
	TextField maxTextField;
	TextField minTextField;

	// constructor
	public StatisticsScreen() {
		services = new Services();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle(" Rasha Electricity Statistics");

		// Title
		Label titleLabel = new Label("Statistics");
		titleLabel.setStyle(
				"-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

		HBox titleBox = new HBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY)));

		ToggleGroup toggleGroup = new ToggleGroup();
		RadioButton yearRadioButton = new RadioButton("Year");
		RadioButton monthRadioButton = new RadioButton("Month");
		RadioButton dayRadioButton = new RadioButton("Day");
		RadioButton allDataRadioButton = new RadioButton("All Data");

		yearRadioButton.setToggleGroup(toggleGroup);
		monthRadioButton.setToggleGroup(toggleGroup);
		dayRadioButton.setToggleGroup(toggleGroup);
		allDataRadioButton.setToggleGroup(toggleGroup);
		toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue instanceof RadioButton) {
				RadioButton selectedRadioButton = (RadioButton) newValue;
				String selectedOption = selectedRadioButton.getText();

				populateDataChoiceBox(selectedOption);
			}
		});

		fileChooserButton = new Button("Choose File");
		fileChooserButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
		fileChooserButton.setStyle(fileChooserButton.getStyle() + "-fx-font-weight: bold;");
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		fileChooserButton.setOnAction(e -> {
			File file = fileChooser.showOpenDialog(primaryStage);
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

							ElectricityRecords electricityInfo = new ElectricityRecords(date, israeliLinesMWs,
									gazaPowerPlantMWs, egyptianLinesMWs, totalDailySupplyMWs, overallDemandMWs,
									powerCutsHoursPerDay, temperature);

							services.addElectricityInfo(electricityInfo);
						} else {
							System.out.println("Invalid line format: " + line);
						}
					}

					showAlert1("Success^-^", "Data loaded successfully!");
				} catch (IOException e1) {
					e1.printStackTrace();
					showAlert("Error!", "An error occurred while reading the file.");
				} catch (DateTimeParseException | NumberFormatException e2) {
					e2.printStackTrace();
					showAlert("Error!", "Error parsing the file. Please check the file format.");
				}
			}
		});

		dataChoiceBox = new ComboBox<>();
		dataChoiceBox.setPromptText("Select Data");
		dataChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				ColumnChoiceBox(newValue);
			}
		});

		columnChoiceBox = new ComboBox<>();
		columnChoiceBox.setPromptText("Select Column");

		Button computeButton = new Button("Compute");
		computeButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
		computeButton.setStyle(computeButton.getStyle() + "-fx-font-weight: bold;");

		Label totalLabel = new Label("Total:");
		Label averageLabel = new Label("Average:");
		Label maxLabel = new Label("Maximum:");
		Label minLabel = new Label("Minimum:");

		totalTextField = new TextField();
		totalTextField.setEditable(false);
		averageTextField = new TextField();
		averageTextField.setEditable(false);
		maxTextField = new TextField();
		maxTextField.setEditable(false);
		minTextField = new TextField();
		minTextField.setEditable(false);

		VBox root = new VBox(10);
		root.setPadding(new Insets(20, 20, 20, 20));
		root.getChildren().addAll(titleBox, fileChooserButton, yearRadioButton, monthRadioButton, dayRadioButton,
				allDataRadioButton, dataChoiceBox, columnChoiceBox, computeButton, totalLabel, totalTextField,
				averageLabel, averageTextField, maxLabel, maxTextField, minLabel, minTextField);

		computeButton.setOnAction(e -> {
			String selectedOption = getSelectedOption(toggleGroup);
			String selectedColumn = columnChoiceBox.getValue();

			if (selectedOption != null && selectedColumn != null) {
				DataElement selectedData = dataChoiceBox.getValue();
				ColumnChoiceBox(selectedData);

				if ("All Data".equals(selectedOption)) {
					// For "All Data" option, set selectedData to null
					selectedData = null;
				}

				ElectricityStats electricityStats = services.getStats(selectedOption, selectedColumn, selectedData);
				updateResult(electricityStats);
			}
		});

		Image icon = new Image(
				new File("C:\\Users\\user\\Desktop\\java\\Rasha_proj3\\statstics.png").toURI().toString());

		// Set the icon for the stage
		primaryStage.getIcons().add(icon);
		Scene scene = new Scene(root, 400, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// methods

	private void updateResult(ElectricityStats electricityStats) {
		totalTextField.setText(String.valueOf(electricityStats.getSum()));
		averageTextField.setText(String.valueOf(electricityStats.getAvg()));
		maxTextField.setText(String.valueOf(electricityStats.getMax()));
		minTextField.setText(String.valueOf(electricityStats.getMin()));
	}

	private String getSelectedOption(ToggleGroup toggleGroup) {
		RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
		return selectedRadioButton == null ? null : selectedRadioButton.getText();
	}

	private void populateDataChoiceBox(String selectedOption) {
		dataChoiceBox.getItems().clear(); // Clear existing items

		if ("Year".equals(selectedOption)) {
			AVLTree<Year> years = services.getYears();
			for (Year year : years) {
				dataChoiceBox.getItems().add(new DataElement(year));
			}
		} else if ("Month".equals(selectedOption)) {
			AVLTree<Year> years = services.getYears();
			for (Year year : years) {
				AVLTree<Month> months = year.getTree();
				for (Month month : months) {
					dataChoiceBox.getItems().add(new DataElement(month));
				}
			}
		} else if ("Day".equals(selectedOption)) {
			AVLTree<Year> years = services.getYears();
			for (Year year : years) {
				AVLTree<Month> months = year.getTree();
				for (Month month : months) {
					AVLTree<Day> days = month.getTree();
					for (Day day : days) {
						dataChoiceBox.getItems().add(new DataElement(day));
					}
				}
			}
		} else if ("All Data".equals(selectedOption)) {
			AVLTree<Year> years = services.getYears();
			for (Year year : years) {
				dataChoiceBox.getItems().add(new DataElement(year));
				AVLTree<Month> months = year.getTree();
				for (Month month : months) {
					dataChoiceBox.getItems().add(new DataElement(month));
					AVLTree<Day> days = month.getTree();
					for (Day day : days) {
						dataChoiceBox.getItems().add(new DataElement(day));
					}
				}
			}
		}
	}

	private void ColumnChoiceBox(DataElement selectedData) {
		columnChoiceBox.getItems().clear(); // Clear existing items

		if (selectedData.getData() instanceof Year) {
			columnChoiceBox.getItems().addAll("Israeli_Lines_MWs", "Gaza_Power_Plant_MWs", "Egyptian Lines",
					"Total daily Supply available", "Overall demand", "Power Cuts hours day", "Temp");
		} else if (selectedData.getData() instanceof Month) {
			columnChoiceBox.getItems().addAll("Israeli_Lines_MWs", "Gaza_Power_Plant_MWs", "Egyptian Lines",
					"Total daily Supply available", "Overall demand", "Power Cuts hours day", "Temp");
		} else if (selectedData.getData() instanceof Day) {
			columnChoiceBox.getItems().addAll("Israeli_Lines_MWs", "Gaza_Power_Plant_MWs", "Egyptian Lines",
					"Total daily Supply available", "Overall demand", "Power Cuts hours day", "Temp");
		}
	}

	private void updateResultFields(double total, double average, double max, double min) {
		totalTextField.setText(String.valueOf(total));
		averageTextField.setText(String.valueOf(average));
		maxTextField.setText(String.valueOf(max));
		minTextField.setText(String.valueOf(min));
	}

	// alerts method
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
}
