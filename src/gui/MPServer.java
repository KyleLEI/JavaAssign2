package gui;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import world.World;

public class MPServer extends World implements EventHandler<ActionEvent> {
	private String hostIP;
	Stage connectUI;
	Stage gameUI;
	BooleanProperty connected = new SimpleBooleanProperty(false);

	MPServer(int[][] param, String hostIP) {
		super(param[0][0], param[0][1], param[0][2]);
		setHP(param[1][0], param[1][1], param[1][2], param[1][3], param[1][4]);
		setAttack(param[2][0], param[2][1], param[2][2], param[2][3], param[2][4]);
		this.hostIP = hostIP;

		// TODO: config gameUI

		connectUI = new Stage();
		connectUI.setTitle("Multiplayer Server");
		connectUI.setScene(new Scene(initConnectUI(), 500, 220));
		connectUI.setResizable(false);

		connected.addListener((a, b, c) -> {
			connectUI.close();
			gameUI.show();
		});
	}

	@Override
	public void handle(ActionEvent event) {
		Platform.runLater(new acceptConnection());
		((Stage) (((Node) event.getSource()).getScene().getWindow())).close();
		connectUI.show();
	}

	private VBox initConnectUI() {
		VBox ret = new VBox(20);
		ret.setPrefWidth(500);
		ret.setAlignment(Pos.CENTER);

		ProgressBar pb = new ProgressBar(-1);
		ret.getChildren().add(pb);

		Text ip = new Text("Host IP: " + hostIP);
		ip.setFont(Font.font(16));
		ret.getChildren().add(ip);

		Text waiting = new Text("Waiting for player to join");
		waiting.setFont(Font.font(Def.font, 12));
		ret.getChildren().add(waiting);
		return ret;
	}

	class acceptConnection implements Runnable{

		@Override
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(8000);
				Socket socket = serverSocket.accept();
				//TODO: map to streams
				connected.set(true);
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Connection Error");
				alert.setHeaderText("Unable to Connect");
				alert.setContentText(e.getMessage());
				alert.showAndWait();
				Platform.exit();
			}
			
		}
		
	}
}
