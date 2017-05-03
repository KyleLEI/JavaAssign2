package gui;

import java.util.Optional;
import java.util.StringTokenizer;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import world.World;

public class WoW extends Application {
	int[][] param=new int[3][];
	private boolean isConfigured = false;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
//		config();
//		if (isConfigured)
//			mainMenu(primaryStage);
		int[][] params={
				{100,5,2000},
				{10,20,50,50,30},
				{20,50,50,50,50}};
		SP sp=new SP(params);
		sp.handle(null);
	}

	private void config() {
		Dialog<String[]> dialog = new Dialog<String[]>();
		dialog.setTitle("Parameters Setup");
		dialog.setHeaderText("Please enter the parameters for the game");
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		Node OKButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
		OKButton.setDisable(true);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField HQLE = new TextField();
		grid.add(new Label("Headquarter Life Elements:"), 0, 0);
		grid.add(HQLE, 1, 0);

		TextField endTime = new TextField();
		grid.add(new Label("End time:"), 0, 1);
		grid.add(endTime, 1, 1);

		TextField LE = new TextField();
		LE.setPromptText("separated by spaces");
		grid.add(new Label("Warrior Life Elements:"), 0, 2);
		grid.add(LE, 1, 2);

		TextField attack = new TextField();
		attack.setPromptText("separated by spaces");
		grid.add(new Label("Warrior Attack Values:"), 0, 3);
		grid.add(attack, 1, 3);

		class Listener implements ChangeListener<String> {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				OKButton.setDisable(HQLE.getText().isEmpty() || endTime.getText().isEmpty() || LE.getText().isEmpty()
						|| attack.getText().isEmpty());
			}
		}
		Listener notEmpty = new Listener();
		HQLE.textProperty().addListener(notEmpty);
		endTime.textProperty().addListener(notEmpty);
		LE.textProperty().addListener(notEmpty);
		attack.textProperty().addListener(notEmpty);

		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK) {
				String[] result = new String[3];
				result[0] = HQLE.getText() + " 5 " + endTime.getText();
				result[1] = LE.getText();
				result[2] = attack.getText();
				return result;
			}
			return null;
		});
		dialog.getDialogPane().setContent(grid);
		Optional<String[]> inValue = dialog.showAndWait();
		inValue.ifPresent(values -> {
			initializeParam(values);
		});
	}

	private void initializeParam(String[] values) {
		try {
			StringTokenizer tknzr;
			tknzr = new StringTokenizer(values[0], " ");
			param[0]=new int[3];
			param[0][0] = Integer.parseInt(tknzr.nextToken());
			param[0][1]=Integer.parseInt(tknzr.nextToken());
			param[0][2]=Integer.parseInt(tknzr.nextToken());

			tknzr = new StringTokenizer(values[1], " ");
			param[1]=new int[5];
			param[1][0]=Integer.parseInt(tknzr.nextToken());
			param[1][1]=Integer.parseInt(tknzr.nextToken());
			param[1][2]=Integer.parseInt(tknzr.nextToken());
			param[1][3]=Integer.parseInt(tknzr.nextToken());
			param[1][4]=Integer.parseInt(tknzr.nextToken());

			tknzr = new StringTokenizer(values[2], " ");
			param[2]=new int[5];
			param[2][0]=Integer.parseInt(tknzr.nextToken());
			param[2][1]=Integer.parseInt(tknzr.nextToken());
			param[2][2]=Integer.parseInt(tknzr.nextToken());
			param[2][3]=Integer.parseInt(tknzr.nextToken());
			param[2][4]=Integer.parseInt(tknzr.nextToken());
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Input Format Error");
			alert.setContentText("Please check your input parameters format and try again.");
			alert.showAndWait();
			return;
		}
		isConfigured = true;
	}

	private void mainMenu(Stage primaryStage) {
		primaryStage.setTitle("Launcher");
		TilePane layout = new TilePane();
		layout.setAlignment(Pos.CENTER);
		layout.setHgap(10);
		layout.setVgap(10);
		layout.setPadding(new Insets(25, 25, 25, 25));

		Text layouttitle = new Text("World of Warcraft");
		layouttitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		layout.getChildren().add(layouttitle);

		Text emptyLine = new Text("");
		layout.getChildren().add(emptyLine);

		Button display = new Button();
		display.setText("Display Assignment1");
		display.setOnAction(e -> {
			World world=new World(param[0][0],param[0][1],param[0][2]);
			world.setHP(param[1][0], param[1][1], param[1][2], param[1][3], param[1][4]);
			world.setAttack(param[1][0], param[1][1], param[1][2], param[1][3], param[1][4]);
			world.run();
		});
		layout.getChildren().add(display);

		Button SP = new Button();
		SP.setText("Singleplayer");
		SP.setOnAction(new SP(param));
		layout.getChildren().add(SP);

		Button MP = new Button();
		MP.setText("Multiplayer");
		layout.getChildren().add(MP);

		Scene scene = new Scene(layout, 300, 250);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
