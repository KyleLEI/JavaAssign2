package gui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MP implements EventHandler<ActionEvent> {
	private int[][] m_param;

	MP(int param[][]) {
		m_param = param;// in case this is the server
	}

	@Override
	public void handle(ActionEvent event) {
		Stage stage = new Stage();
		stage.setTitle("Multiplayer");
		stage.setScene(new Scene(initConnectUI(), 500, 220));
		stage.setResizable(false);
//		((Stage)(((Node) event.getSource()).getScene().getWindow())).close();//TODO: uncomment after debug
		stage.show();
	}

	private GridPane initConnectUI() {
		GridPane ret = new GridPane();
//		ret.setGridLinesVisible(true);// removed after debug
		ret.setPadding(new Insets(20, 10, 20, 20));
		ret.setVgap(10);
		ret.setHgap(15);
		setConstraints(ret);
		Text title = new Text("         Multiplayer Game");
		title.setFont(Font.font(Def.font, FontWeight.BOLD, 20));
		ret.add(title, 1, 0);
		Text yourIP = new Text("Your IP Address");
		yourIP.setFont(Font.font(Def.font, 12));
		ret.add(yourIP, 0, 2);

		String myIP=null;
		try {//TODO: add back after debug
			myIP=InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("IP Address Error");
			alert.setContentText("Unable to obtain local IP address. Please check your network configuration.");
			alert.showAndWait();
		}
//		myIP="192.168.254.254";
		Text IP = new Text(myIP);
		IP.setFont(Font.font(Def.font,16));
		ret.add(IP, 1,2);
		
		Text hostIP = new Text("Host IP Address");
		hostIP.setFont(Font.font(Def.font, 12));
		ret.add(hostIP, 0, 3);
		
		TextField tf=new TextField();
		tf.setMaxWidth(150);
		ret.add(tf, 1, 3);
		
		Button create=new Button("Create Game");
		create.setPrefSize(100, 20);
		create.setOnAction(new MPServer(m_param,myIP));
		ret.add(create, 2, 2);
		
		Button join=new Button("Join Game");
		join.setPrefSize(100, 20);
		join.setOnAction(new MPClient());
		ret.add(join, 2, 3);
		return ret;
	}

	private void setConstraints(GridPane grid) {
		RowConstraints rowConstraints = new RowConstraints();
		rowConstraints.setPrefHeight(35);
		grid.getColumnConstraints().add(new ColumnConstraints(100));
		grid.getRowConstraints().add(rowConstraints);

		grid.getColumnConstraints().add(new ColumnConstraints(230));
		grid.getRowConstraints().add(new RowConstraints(20));

		grid.getColumnConstraints().add(new ColumnConstraints(100));
		grid.getRowConstraints().add(rowConstraints);

		grid.getRowConstraints().add(rowConstraints);

	}

}
