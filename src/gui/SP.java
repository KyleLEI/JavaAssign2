package gui;

import Images.ImagesLoader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import warriors.Dragon;
import warriors.Iceman;
import warriors.Lion;
import warriors.Ninja;
import warriors.Warrior;
import warriors.Wolf;
import world.Team;
import world.WarriorType;
import world.WarriorType.type;

public class SP implements EventHandler<ActionEvent> {
	private int[][] m_param;
	private SPWorld world;
	private Text time;
	private Text LE;
	private Text spawnMsg;
	private HBox[] slots;
	private ImageView[] flags;
	private ImagesLoader imgs = new ImagesLoader();

	SP(int param[][]) {
		m_param = param;
	}

	@Override
	public void handle(ActionEvent event) {
		world = new SPWorld(m_param);
		world.hq[0].lifeElements.addListener((a, b, c) -> {
			LE.setText("Life Elements: " + world.getPlayerLE());
		});
		world.clock.minute.addListener((a, b, c) -> {
			time.setText(world.clock.toString());
		});
		world.end.addListener((a, b, c) -> {// FIXME: not FX thread
			// if (world.redHQOccupierCount == 2) {
			// Alert alert = new Alert(AlertType.INFORMATION);
			// alert.setTitle("Game Over");
			// alert.setHeaderText("Blue Victory");
			// alert.setContentText("The blue team have secured victory");
			// alert.showAndWait();
			// return;
			// }
			// if (world.blueHQOccupierCount == 2) {
			// Alert alert = new Alert(AlertType.INFORMATION);
			// alert.setTitle("Game Over");
			// alert.setHeaderText("Red Victory");
			// alert.setContentText("The red team have secured victory");
			// alert.showAndWait();
			// return;
			// }
			//
			// Alert alert = new Alert(AlertType.INFORMATION);
			// alert.setTitle("Game Over");
			// alert.setHeaderText("Draw");
			// alert.setContentText("It's a draw");
			// alert.showAndWait();

		});
		world.shouldUpdateBlue.addListener((o, ov, c) -> {
			if (c == true) {
				displayWarrior(world.hq[1].warriorInHQ.getFirst(), 6);
				world.shouldUpdateBlue.set(false);
			}
		});
		world.shouldUpdateMap.addListener((a, b, c) -> {
			if (c == true) {
				updateMap();
				world.shouldUpdateMap.set(false);
			}
		});
		Thread logic = new Thread(world);
		Stage stage = new Stage();
		stage.setTitle("Singleplayer");
		stage.setOnCloseRequest(e -> world.task_should_exit = true);
		stage.setScene(new Scene(initUI(), 1000, 600));
		stage.show();
		logic.start(); // since UI takes time to load, start logic after UI

	}

	private BorderPane initUI() {
		BorderPane ret = new BorderPane();
		ret.setTop(configTop());
		ret.setRight(configRight());
		ret.setCenter(configCenter());
		return ret;
	}

	private HBox configTop() {
		HBox hb = new HBox(50);
		hb.setStyle("-fx-background-color: #336699;");
		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(15, 12, 15, 12));
		hb.setSpacing(10);
		time = new Text(world.getClock().toString());
		time.setFont(new Font("Helvetica", 20));
		time.setFill(Color.WHITE);
		LE = new Text("Life Elements: " + world.getPlayerLE());
		LE.setFont(new Font("Helvetica", 20));
		LE.setFill(Color.WHITE);
		Region blank = new Region();
		blank.setPrefWidth(500);
		Region blank2 = new Region();
		blank2.setPrefWidth(300);
		hb.getChildren().addAll(blank, time, blank2, LE);
		return hb;
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
		spawnDragon.setOnMouseClicked(new spawnHandler(type.DRAGON));
		flow.getChildren().add(spawnDragon);

		ImageView spawnIceman = new ImageView();
		spawnIceman.setImage(imgs.iceman);
		spawnIceman.setOnMouseClicked(new spawnHandler(type.ICEMAN));
		flow.getChildren().add(spawnIceman);

		ImageView spawnLion = new ImageView();
		spawnLion.setImage(imgs.lion);
		spawnLion.setOnMouseClicked(new spawnHandler(type.LION));
		flow.getChildren().add(spawnLion);

		ImageView spawnWolf = new ImageView();
		spawnWolf.setImage(imgs.wolf);
		spawnWolf.setOnMouseClicked(new spawnHandler(type.WOLF));
		flow.getChildren().add(spawnWolf);

		ImageView spawnNinja = new ImageView();
		spawnNinja.setImage(imgs.ninja);
		spawnNinja.setOnMouseClicked(new spawnHandler(type.NINJA));
		flow.getChildren().add(spawnNinja);

		spawnMsg = new Text();
		spawnMsg.setFont(new Font("Helvetica", 18));
		spawnMsg.setFill(Color.CRIMSON);
		spawnMsg.setTextAlignment(TextAlignment.CENTER);
		flow.getChildren().add(spawnMsg);
		return flow;
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
			ImageView blue = new ImageView();
			blue.setFitHeight(Def.cityW / 2);
			blue.setFitWidth(Def.cityW / 2);
			slots[i].getChildren().addAll(red, blue);
			grid.add(slots[i], i, 2);
		}

		grid.setGridLinesVisible(true);
		return grid;
	}

	private class spawnHandler implements EventHandler<MouseEvent> {
		WarriorType.type requestType;

		spawnHandler(WarriorType.type t) {
			requestType = t;
		}

		@Override
		public void handle(MouseEvent event) {
			switch (world.requestSpawn(requestType)) { // handle spawn results
			case Def.mSpawnSuccess:
				displaySpawn(Team.red, requestType);
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

	void displaySpawn(Team f, WarriorType.type t) {
		int slotIndex = (f == Team.red ? 0 : 6);
		int slotslotIndex = (f == Team.red ? 0 : 1);
		switch (t) {
		case LION:
			((ImageView) slots[slotIndex].getChildren().get(slotslotIndex))
					.setImage(f == Team.red ? imgs.lionRed : imgs.lionBlue);
			break;
		case DRAGON:
			((ImageView) slots[slotIndex].getChildren().get(slotslotIndex))
					.setImage(f == Team.red ? imgs.dragonRed : imgs.dragonBlue);
			break;
		case ICEMAN:
			((ImageView) slots[slotIndex].getChildren().get(slotslotIndex))
					.setImage(f == Team.red ? imgs.icemanRed : imgs.icemanBlue);
			break;

		case NINJA:
			((ImageView) slots[slotIndex].getChildren().get(slotslotIndex))
					.setImage(f == Team.red ? imgs.ninjaRed : imgs.ninjaBlue);
			break;
		case WOLF:
			((ImageView) slots[slotIndex].getChildren().get(slotslotIndex))
					.setImage(f == Team.red ? imgs.wolfRed : imgs.wolfBlue);
		}
	}

	void displayWarrior(Warrior w, int cityIndex) {
		if (w == null) {// this means the city is empty
			((ImageView) slots[cityIndex].getChildren().get(0)).setImage(null);
			((ImageView) slots[cityIndex].getChildren().get(1)).setImage(null);
			return;
		}
		int slotslotIndex = (w.getTeam() == Team.red ? 0 : 1);
		if (w instanceof Lion) {
			((ImageView) slots[cityIndex].getChildren().get(slotslotIndex))
					.setImage(w.getTeam() == Team.red ? imgs.lionRed : imgs.lionBlue);
			return;
		}
		if (w instanceof Dragon) {
			((ImageView) slots[cityIndex].getChildren().get(slotslotIndex))
					.setImage(w.getTeam() == Team.red ? imgs.dragonRed : imgs.dragonBlue);
			return;
		}
		if (w instanceof Iceman) {
			((ImageView) slots[cityIndex].getChildren().get(slotslotIndex))
					.setImage(w.getTeam() == Team.red ? imgs.icemanRed : imgs.icemanBlue);
			return;
		}
		if (w instanceof Ninja) {
			((ImageView) slots[cityIndex].getChildren().get(slotslotIndex))
					.setImage(w.getTeam() == Team.red ? imgs.ninjaRed : imgs.ninjaBlue);
			return;
		}
		if (w instanceof Wolf) {
			((ImageView) slots[cityIndex].getChildren().get(slotslotIndex))
					.setImage(w.getTeam() == Team.red ? imgs.wolfRed : imgs.wolfBlue);
			return;
		}
	}

	void cleanMap(int cityIndex) {
		((ImageView) slots[cityIndex].getChildren().get(0)).setImage(null);
		((ImageView) slots[cityIndex].getChildren().get(1)).setImage(null);
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
			if(W1==W2) {
				cleanMap(i+1);
				displayWarrior(W1,i+1);
				continue;
			}
			displayWarrior(W1, i + 1);
			displayWarrior(W2, i + 1);
		}
		((ImageView) slots[6].getChildren().get(1)).setImage(null);// spawned
																	// moved out
	}

}
