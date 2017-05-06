package gui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class MPServer extends SP implements EventHandler<ActionEvent> {
	private ObjectOutputStream out;// writes world updates
	private ObjectInputStream in;// listens spawn requests

	private String hostIP;
	private Text connectMsg;
	private Stage connectUI;
	private Stage gameUI;

	private MPWorld world;
	BooleanProperty shouldUpdateBlue = new SimpleBooleanProperty(false);
	BooleanProperty shouldUpdateRed = new SimpleBooleanProperty(false);
	BooleanProperty disconnected = new SimpleBooleanProperty(false);

	MPServer(int[][] param, String hostIP) {
		super(param, false);
		world = new MPWorld(param);
		this.hostIP = hostIP;

		connectUI = new Stage();
		connectUI.setTitle("Multiplayer Server");
		connectUI.setScene(new Scene(initConnectUI(), 500, 220));
		connectUI.setResizable(false);
		connectUI.setOnCloseRequest(e -> System.exit(0));// explicit exit to
															// interrupt socket
															// accept

		gameUI = new Stage();
		gameUI.setTitle("Multiplayer");
		gameUI.setOnCloseRequest(e -> {
			try {
				out.writeInt(Def.serverShutdown);
			} catch (IOException e1) {
				Platform.runLater(new IOPrompt(e1));
			} finally {
				System.exit(0);
			}
		});
		gameUI.setScene(new Scene(initGameUI(), 1000, 600));
		gameUI.setResizable(false);

		world.hq[0].lifeElements.addListener((a, b, c) -> {
			LE.setText("Life Elements: " + world.getPlayerLE());
		});
		world.hq[1].lifeElements.addListener((a, b, c) -> {
			// send LE to client
			try {
				out.writeInt(Def.updateLE);
				out.writeInt(c.intValue());
			} catch (IOException e1) {
				Platform.runLater(new IOPrompt(e1));
			}

		});
		world.clock.minute.addListener((a, b, c) -> {
			time.setText(world.clock.toString());
			// send time to client
			try {
				out.writeInt(Def.updateTime);
				out.writeUTF(world.clock.toString());
			} catch (IOException e1) {
				Platform.runLater(new IOPrompt(e1));
			}
		});
		world.end.addListener((a, b, c) -> {
			if (world.redHQOccupierCount.get() == 2) {
				spawnMsg.setText("Blue\nVictory!");
				return;
			}
			if (world.blueHQOccupierCount.get() == 2) {
				spawnMsg.setText("Red\nVictory!");
				return;
			}
			spawnMsg.setText("It's a\nDraw!");
			// send end message to client
			try {
				out.writeInt(Def.updateEnd);
				out.writeUTF(spawnMsg.getText());
			} catch (IOException e1) {
				Platform.runLater(new IOPrompt(e1));
			}
		});
		shouldUpdateBlue.addListener((o, ov, c) -> {// don't need to send
			if (c == true) {
				displayWarrior(world.hq[1].warriorInHQ.getFirst(), 6);
				world.shouldUpdateBlue.set(false);
			}
		});
		shouldUpdateRed.addListener((o, ov, c) -> {
			if (c == true) {
				displayWarrior(world.hq[0].warriorInHQ.getFirst(), 0);
				world.shouldUpdateBlue.set(false);
				// send spawned warrior to client
				try {
					out.writeInt(Def.updateRedSpawn);
					out.writeObject(world.hq[0].warriorInHQ.getFirst());
				} catch (IOException e1) {
					Platform.runLater(new IOPrompt(e1));
				}
			}
		});
		world.shouldUpdateMap.addListener((a, b, c) -> {
			if (c == true) {
				updateMap();
				world.shouldUpdateMap.set(false);
				try {
					out.writeInt(Def.updateMap);
					out.writeObject(world.cities);
				} catch (IOException e1) {
					Platform.runLater(new IOPrompt(e1));
				}
			}
		});
		world.shouldUpdateFlag.addListener((a, b, c) -> {
			if (c == true) {
				updateFlag();
				world.shouldUpdateFlag.set(false);
				try {
					out.writeInt(Def.updateFlag);
					out.writeInt(world.cityToUpdate);
					out.writeObject(world.flagToUpdate);
				} catch (IOException e1) {
					Platform.runLater(new IOPrompt(e1));
				}
			}
		});
		world.redHQOccupierCount.addListener((a, b, c) -> {
			redOccu.setText("   " + c);
			try {
				out.writeInt(Def.updateRedOccu);
				out.writeInt(c.intValue());
			} catch (IOException e1) {
				Platform.runLater(new IOPrompt(e1));
			}
		});
		world.blueHQOccupierCount.addListener((a, b, c) -> {
			blueOccu.setText("   " + c);
			try {
				out.writeInt(Def.updateBlueOccu);
				out.writeInt(c.intValue());
			} catch (IOException e1) {
				Platform.runLater(new IOPrompt(e1));
			}
		});
	}

	@Override
	public synchronized void handle(ActionEvent event) {
		((Stage) (((Node) event.getSource()).getScene().getWindow())).close();
		new Thread(new acceptConnection()).start();
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

		connectMsg = new Text("Waiting...");
		connectMsg.setFont(Font.font(Def.font, 12));
		connectMsg.setFill(Color.CADETBLUE);
		ret.getChildren().add(connectMsg);
		return ret;
	}

	private HBox configTop() {
		HBox hb = new HBox(50);
		hb.setStyle("-fx-background-color: #336699;");
		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(15, 12, 15, 12));
		hb.setSpacing(10);
		time = new Text(world.getClock().toString());
		time.setFont(new Font(Def.font, 20));
		time.setFill(Color.WHITE);
		LE = new Text("Life Elements: " + world.getPlayerLE());
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

	protected BorderPane initGameUI() {
		BorderPane ret = new BorderPane();
		ret.setTop(configTop());
		ret.setRight(configRight());
		ret.setCenter(configCenter());
		ret.setBottom(configBottom());
		return ret;
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
		spawnDragon.setOnMouseClicked(new MPspawnHandler(type.DRAGON));
		flow.getChildren().add(spawnDragon);

		ImageView spawnIceman = new ImageView();
		spawnIceman.setImage(imgs.iceman);
		spawnIceman.setOnMouseClicked(new MPspawnHandler(type.ICEMAN));
		flow.getChildren().add(spawnIceman);

		ImageView spawnLion = new ImageView();
		spawnLion.setImage(imgs.lion);
		spawnLion.setOnMouseClicked(new MPspawnHandler(type.LION));
		flow.getChildren().add(spawnLion);

		ImageView spawnWolf = new ImageView();
		spawnWolf.setImage(imgs.wolf);
		spawnWolf.setOnMouseClicked(new MPspawnHandler(type.WOLF));
		flow.getChildren().add(spawnWolf);

		ImageView spawnNinja = new ImageView();
		spawnNinja.setImage(imgs.ninja);
		spawnNinja.setOnMouseClicked(new MPspawnHandler(type.NINJA));
		flow.getChildren().add(spawnNinja);

		spawnMsg = new Text();
		spawnMsg.setFont(new Font(Def.font, 18));
		spawnMsg.setFill(Color.CRIMSON);
		spawnMsg.setTextAlignment(TextAlignment.JUSTIFY);
		flow.getChildren().add(spawnMsg);
		return flow;
	}

	class acceptConnection implements Runnable {

		@Override
		public void run() {
			Platform.runLater(() -> connectUI.show());
			try {
				ServerSocket serverSocket = new ServerSocket(Def.portNo);
				Socket socket = serverSocket.accept();
				connectMsg.setText("Client connected");
				out = new ObjectOutputStream(socket.getOutputStream());
				connectMsg.setText("Output Stream Generated");
				out.flush();
				in = new ObjectInputStream(socket.getInputStream());
				connectMsg.setText("Input Stream Generated");
				connectMsg.setText("Launching...");
				// try {
				// Thread.sleep(1000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				Platform.runLater(() -> {// TA so smart, calling Platform to use
											// JavaFX thread
					connectUI.close();
					new Thread(world).start();
					gameUI.show();// starts game
				});
				// TODO: add message sender/receiver(decoder) here
			} catch (IOException e) {
				connectMsg.setText(e.getMessage() + ". Please retry.");
				connectMsg.setFill(Color.RED);
			}

		}

	}

	private class MPspawnHandler implements EventHandler<MouseEvent> {
		WarriorType.type requestType;

		MPspawnHandler(WarriorType.type t) {
			requestType = t;
		}

		@Override
		public void handle(MouseEvent event) {
			if (world.end.get())
				return;
			switch (world.requestSpawn(requestType, Team.red)) { // handle spawn
																	// results
			case Def.mSpawnSuccess:
				shouldUpdateRed.set(true);
				Thread t = new Thread(() -> {
					String str = requestType + "\nspawned!";
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

	private class MPdetailHandler implements EventHandler<MouseEvent> {
		int city;
		int t;

		MPdetailHandler(int cityIndex, int team) {
			city = cityIndex;
			t = team;
		}

		@Override
		public void handle(MouseEvent event) {
			try {
				if (city == 0) {
					updateDetails(world.hq[0].warriorInHQ.getFirst());
					displayWarrior(world.hq[0].warriorInHQ.getFirst(), thumbnail);
					return;
				}
				if (city == 6) {
					updateDetails(world.hq[1].warriorInHQ.getFirst());
					displayWarrior(world.hq[1].warriorInHQ.getFirst(), thumbnail);
					return;
				}
				updateDetails(t == 0 ? world.cities[city - 1].warriorInCity.getFirst()
						: world.cities[city - 1].warriorInCity.getLast());
				displayWarrior(t == 0 ? world.cities[city - 1].warriorInCity.getFirst()
						: world.cities[city - 1].warriorInCity.getLast(), thumbnail);
			} catch (NoSuchElementException e) {
				System.out.println("the list is empty, but I don't care");
			}
		}

		private void updateDetails(Warrior w) {
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
		}

	}

	void updateMap() {
		((ImageView) slots[0].getChildren().get(0)).setImage(null);// spawned
																	// moved out
		for (int i = 0; i < 5; i++) {
			if (world.cities[i].warriorInCity.isEmpty()) {
				displayWarrior(null, i + 1);
				continue;
			}
			Warrior W1 = world.cities[i].warriorInCity.getFirst();
			Warrior W2 = world.cities[i].warriorInCity.getLast();
			if (W1 == W2) {
				cleanMap(i + 1);
				displayWarrior(W1, i + 1);
				continue;
			}
			displayWarrior(W1, i + 1);
			displayWarrior(W2, i + 1);
		}
		((ImageView) slots[6].getChildren().get(1)).setImage(null);// spawned
																	// moved out
	}

	void updateFlag() {
		ImageView flagSlotToSet = flags[world.cityToUpdate];
		Team flagToSet = world.flagToUpdate;
		flagSlotToSet.setImage(flagToSet == Team.red ? imgs.redFlag : imgs.blueFlag);
	}
}
