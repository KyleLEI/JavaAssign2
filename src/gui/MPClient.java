package gui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import gui.MPServer.IOPrompt;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import warriors.Warrior;
import world.Team;
import world.WarriorType;
import world.WarriorType.type;

public class MPClient extends SP {
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private Text connectMsg;
	private Stage connectUI;
	private Stage gameUI;

	private Warrior[] warriors = new Warrior[10];
	private Warrior redSpawn;
	private Warrior blueSpawn;
	private boolean end;

	MPClient(int[][] params) {
		super(params, false);

		connectUI = new Stage();
		connectUI.setTitle("Multiplayer Client");
		connectUI.setScene(new Scene(initConnectUI(), 500, 220));
		connectUI.setResizable(false);
		connectUI.setOnCloseRequest(e -> System.exit(0));

		gameUI = new Stage();
		gameUI.setTitle("Multiplayer");
		gameUI.setOnCloseRequest(e -> {
			try {
				in.close();
				out.close();
			} catch (IOException e1) {
				Platform.runLater(new IOPrompt(e1));
			} finally {
				System.exit(0);
			}
		});
		gameUI.setScene(new Scene(initGameUI(), 1000, 600));
		gameUI.setResizable(false);
	}

	public void handle(String remoteIP) {
		try {
			Platform.runLater(() -> connectUI.show());
			Socket socket = new Socket(remoteIP, Def.portNo);
			connectMsg.setText("Server Connected");
			in = new ObjectInputStream(socket.getInputStream());
			connectMsg.setText("Input Stream Generated");
			out = new ObjectOutputStream(socket.getOutputStream());
			connectMsg.setText("Output Stream Generated");
			connectMsg.setText("Launching...");
			decode(in.readInt());// set initial before showing UI
			decode(in.readInt());
			Platform.runLater(() -> {// TA so smart, calling Platform to use
				// JavaFX thread
				connectUI.close();
				gameUI.show();// starts game
			});
			new Thread(() -> {
				for (;;) {
					try {
						decode(in.readInt());
					} catch (ClassNotFoundException e) {
						System.err.println("Check casting: " + e.getMessage());
					} catch (IOException e) {
						Platform.runLater(new IOPrompt(e));
					}
				}
			}).start();
		} catch (IOException e) {
			connectMsg.setText(e.getMessage() + ". Please retry.");
			connectMsg.setFill(Color.RED);
		} catch (ClassNotFoundException e) {
			System.err.println("Check casting: " + e.getMessage());
		}
	}

	private void decode(int header) throws IOException, ClassNotFoundException {
		switch (header) {
		case Def.updateLE:// int
			LE.setText("Life Elements: " + in.readInt());
			break;
		case Def.updateTime:// UTF
			time.setText(in.readUTF());
			break;
		case Def.updateEnd:// UTF, update end
			System.out.println("Should end now");
			this.end = true;
			spawnMsg.setText(in.readUTF());
			break;
		case Def.updateRedSpawn:// Warrior
			redSpawn = (Warrior) in.readObject();
			displayWarrior(redSpawn, 0);
			break;
		case Def.updateBlueSpawn:// Warrior
			blueSpawn = (Warrior) in.readObject();
			displayWarrior(blueSpawn, 6);
			break;
		case Def.updateMap:// 10 warrior
			for (int i = 0; i < 5; i++) {
				warriors[2 * i] = (Warrior) in.readObject();
				warriors[2 * i + 1] = (Warrior) in.readObject();
			}
			updateMap();
			break;
		case Def.updateFlag:// int city, Team flag
			int city = in.readInt();
			Team flag = (Team) in.readObject();
			updateFlag(city, flag);
			break;
		case Def.updateRedOccu:// int
			redOccu.setText(String.valueOf(in.readInt()));
			break;
		case Def.updateBlueOccu:// int
			blueOccu.setText(String.valueOf(in.readInt()));
			break;
		case Def.spawnResponse:
			switch (in.readInt()) { // handle spawn // results
			case Def.mSpawnSuccess:
				Thread t = new Thread(() -> {
					String str = blueSpawn.getClass().getSimpleName() + "\nspawned!";
					spawnMsg.setText(str);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					if (spawnMsg.getText().equals(str))
						spawnMsg.setText("");
				});
				t.start();
				break;
			case Def.mNotEnoughLE:
				Thread t1 = new Thread(() -> {
					String str = "not enough\nlife elements";
					spawnMsg.setText(str);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					if (spawnMsg.getText().equals(str))
						spawnMsg.setText("");
				});
				t1.start();
				break;
			case Def.mNotRightTime:
				Thread t2 = new Thread(() -> {
					String str = "cannot\nspawn now";
					spawnMsg.setText(str);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					if (spawnMsg.getText().equals(str))
						spawnMsg.setText("");
				});
				t2.start();
			}
		}
	}

	private HBox configTop() {
		HBox hb = new HBox(50);
		hb.setStyle("-fx-background-color: #336699;");
		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(15, 12, 15, 12));
		hb.setSpacing(10);
		time = new Text("N/A");
		time.setFont(new Font(Def.font, 20));
		time.setFill(Color.WHITE);
		LE = new Text("Life Elements: " + "N/A");
		LE.setFont(new Font(Def.font, 20));
		LE.setFill(Color.WHITE);
		Region blank = new Region();
		blank.setPrefWidth(500);
		Region blank2 = new Region();
		blank2.setPrefWidth(300);
		hb.getChildren().addAll(blank, time, blank2, LE);
		return hb;
	}

	private GridPane configCenter() {
		slots = new HBox[7];
		flags = new ImageView[5];

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(Def.cityW);
		grid.setVgap(10);
		grid.setPadding(new Insets(0, 10, 0, 10));

		for (int i = 0; i < 5; i++) { // slots for displaying flags
			flags[i] = new ImageView();
			flags[i].setFitWidth(Def.flagW);
			flags[i].setFitHeight(Def.flagW);
			grid.add(flags[i], 1 + i, 0);
		}

		grid.add(new ImageView(imgs.hqImage), 0, 1);
		for (int i = 0; i < 5; i++) {
			grid.add(new ImageView(imgs.cityImage), i + 1, 1);
		}
		grid.add(new ImageView(imgs.hqImage), 6, 1);

		for (int i = 0; i < 7; i++) {
			slots[i] = new HBox();
			slots[i].setPrefSize(Def.cityW, Def.cityW / 2);
			slots[i].setAlignment(Pos.CENTER);
			ImageView red = new ImageView();
			red.setFitHeight(Def.cityW / 2);
			red.setFitWidth(Def.cityW / 2);
			red.setOnMouseClicked(new MPdetailHandler(i, 0));
			ImageView blue = new ImageView();
			blue.setFitHeight(Def.cityW / 2);
			blue.setFitWidth(Def.cityW / 2);
			blue.setOnMouseClicked(new MPdetailHandler(i, 1));
			slots[i].getChildren().addAll(red, blue);
			grid.add(slots[i], i, 2);
		}

		redOccu = new Text("   " + 0);
		redOccu.setFill(Color.BLUE);
		redOccu.setFont(new Font(Def.font, 16));
		blueOccu = new Text("   " + 0);
		blueOccu.setFill(Color.RED);
		blueOccu.setFont(new Font(Def.font, 16));

		grid.add(redOccu, 0, 3);
		grid.add(blueOccu, 6, 3);
		// grid.setGridLinesVisible(true);// removed after debug
		return grid;
	}

	private FlowPane configRight() {
		FlowPane flow = new FlowPane();
		// flow.setPadding(new Insets(5, 0, 5, 0));
		flow.setVgap(4);
		flow.setHgap(4);
		flow.setPrefWrapLength(210); // preferred width allows for two columns
		flow.setStyle("-fx-background-color: DAE6F3;");

		ImageView spawnDragon = new ImageView();
		spawnDragon.setImage(imgs.dragon);
		spawnDragon.setOnMouseClicked((e) -> requestSpawn(WarriorType.type.DRAGON));
		flow.getChildren().add(spawnDragon);

		ImageView spawnIceman = new ImageView();
		spawnIceman.setImage(imgs.iceman);
		spawnIceman.setOnMouseClicked((e) -> requestSpawn(WarriorType.type.ICEMAN));
		flow.getChildren().add(spawnIceman);

		ImageView spawnLion = new ImageView();
		spawnLion.setImage(imgs.lion);
		spawnLion.setOnMouseClicked((e) -> requestSpawn(WarriorType.type.LION));
		flow.getChildren().add(spawnLion);

		ImageView spawnWolf = new ImageView();
		spawnWolf.setImage(imgs.wolf);
		spawnWolf.setOnMouseClicked((e) -> requestSpawn(WarriorType.type.WOLF));
		flow.getChildren().add(spawnWolf);

		ImageView spawnNinja = new ImageView();
		spawnNinja.setImage(imgs.ninja);
		spawnNinja.setOnMouseClicked((e) -> requestSpawn(WarriorType.type.NINJA));
		flow.getChildren().add(spawnNinja);

		spawnMsg = new Text();
		spawnMsg.setFont(new Font(Def.font, 18));
		spawnMsg.setFill(Color.CRIMSON);
		spawnMsg.setTextAlignment(TextAlignment.JUSTIFY);
		flow.getChildren().add(spawnMsg);
		return flow;
	}

	private VBox initConnectUI() {
		VBox ret = new VBox(20);
		ret.setPrefWidth(500);
		ret.setAlignment(Pos.CENTER);

		ProgressBar pb = new ProgressBar(-1);
		ret.getChildren().add(pb);

		Text waiting = new Text("Connecting to server...");
		waiting.setFont(Font.font(Def.font, 12));
		ret.getChildren().add(waiting);

		connectMsg = new Text("Waiting...");
		connectMsg.setFont(Font.font(Def.font, 12));
		connectMsg.setFill(Color.CADETBLUE);
		ret.getChildren().add(connectMsg);
		return ret;
	}

	private BorderPane initGameUI() {
		BorderPane ret = new BorderPane();
		ret.setTop(configTop());
		ret.setRight(configRight());
		ret.setCenter(configCenter());
		ret.setBottom(configBottom());
		return ret;
	}

	class IOPrompt implements Runnable {
		IOException e1;

		IOPrompt(IOException e) {
			e1 = e;
		}

		@Override
		public void run() {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Connection Error");
			alert.setContentText(e1.getMessage());
			alert.showAndWait();
			System.exit(1);
		}

	}

	void requestSpawn(WarriorType.type requestType) {
		if (end)
			return;
		try {
			out.reset();
			out.writeObject(requestType);
			out.flush();

		} catch (IOException e) {
			Platform.runLater(new IOPrompt(e));
		}
	}

	private class MPdetailHandler implements EventHandler<MouseEvent> {
		int city;
		int t;

		MPdetailHandler(int cityIndex, int team) {
			city = cityIndex;
			t = team;
		}

		@Override
		public void handle(MouseEvent event) {
			if (city == 0) {
				updateDetails(redSpawn);
				displayWarrior(redSpawn, thumbnail);
				return;
			}
			if (city == 6) {
				updateDetails(blueSpawn);
				displayWarrior(blueSpawn, thumbnail);
				return;
			}
			updateDetails(t == 0 ? warriors[2 * (city - 1)] : warriors[2 * (city - 1) + 1]);
			displayWarrior(t == 0 ? warriors[2 * (city - 1)] : warriors[2 * (city - 1) + 1], thumbnail);

		}

		private void updateDetails(Warrior w) {
			if (w == null)
				return;
			propertyV[0].setText(" " + w.getClass().getSimpleName());
			propertyV[1].setText(" " + w.getHP());
			propertyV[2].setText(" " + (w.getTeam() == Team.red ? "Red" : "Blue"));
			propertyV[3].setText(" " + w.getAttackV());
			propertyV[4].setText(" " + w.getID());
			propertyV[5].setText(" " + w.getEnemiesKilled());

			String location;
			if (city == 0)
				location = "Red HQ";
			else if (city == 6)
				location = "Blue HQ";
			else
				location = "City " + city;

			propertyV[6].setText(" " + location);
			propertyV[7].setText(" " + w.getSteps());

		}
	}

	void updateMap() {
		((ImageView) slots[0].getChildren().get(0)).setImage(null);// spawned
																	// moved out
		for (int i = 0; i < 5; i++) {
			cleanMap(i + 1);
			Warrior w1 = warriors[2 * i];
			Warrior w2 = warriors[2 * i + 1];
			if (w1 == null && w2 == null)
				continue;
			if (w1 != null)
				displayWarrior(w1, i + 1);
			if (w2 != null)
				displayWarrior(w2, i + 1);
		}
		((ImageView) slots[6].getChildren().get(1)).setImage(null);// spawned
																	// moved out
	}

	void updateFlag(int city, Team flag) {
		ImageView flagSlotToSet = flags[city];
		flagSlotToSet.setImage(flag == Team.red ? imgs.redFlag : imgs.blueFlag);
	}
}
