package application;

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("WELCOME TO RASHA PROJECT !");

		Label titleLabel = new Label("MY PROJECT ");
		titleLabel.setStyle(
				"-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

		HBox titleBox = new HBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY)));
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));
		vbox.setAlignment(Pos.CENTER);

		Button ManagementScreen = new Button("Management Screen");
		Button StatisticsScreen = new Button("Statistics Screen");

		ManagementScreen.setOnAction(e -> new ManagementScreen().start(new Stage()));
		StatisticsScreen.setOnAction(e -> new StatisticsScreen().start(new Stage()));
		// Set the button styles using CSS
		ManagementScreen.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: green;");
		StatisticsScreen.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: green;");

		vbox.getChildren().addAll(titleBox, ManagementScreen, StatisticsScreen);
		Image icon = new Image(new File("C:\\Users\\user\\Desktop\\java\\Rasha_proj3\\welcome.png").toURI().toString());

		// Set the icon for the stage
		primaryStage.getIcons().add(icon);
		Scene scene = new Scene(vbox, 400, 200);
		primaryStage.setScene(scene);

		primaryStage.show();
	}
}
