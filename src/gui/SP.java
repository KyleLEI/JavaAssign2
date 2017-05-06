package gui;

import java.util.NoSuchElementException;

import Images.ImagesLoader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
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
	private boolean useEmbeddedWorld;
	protected Text time;
	protected Text LE;
	protected Text spawnMsg;
	protected HBox[] slots;
	protected ImageView[] flags;
	protected ImagesLoader imgs = new ImagesLoader();
	protected ImageView thumbnail;
	protected Text[] propertyV = new Text[8];
	protected Text redOccu;
	protected Text blueOccu;

	SP(int param[][], boolean useEmbeddedWorld) {
		m_param = param;
		this.useEmbeddedWorld = useEmbeddedWorld;
	}

	@Override
	public void handle(ActionEvent event) {
		((Stage) (((Node) event.getSource()).getScene().getWindow())).close();// close
																				// main
																				// menu
		if (useEmbeddedWorld)
			world = new SPWorld(m_param);
		world.hq[0].lifeElements.addListener((a, b, c) -> {
			LE.setText("Life Elements: " + world.getPlayerLE());
		});
		world.clock.minute.addListener((a, b, c) -> {
			time.setText(world.clock.toString());
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
		world.shouldUpdateFlag.addListener((a, b, c) -> {
			if (c == true) {
				updateFlag();
				world.shouldUpdateFlag.set(false);
			}
		});
		world.redHQOccupierCount.addListener((a, b, c) -> {
			redOccu.setText("   " + c);
		});
		world.blueHQOccupierCount.addListener((a, b, c) -> {
			blueOccu.setText("   " + c);
		});

		Thread logic = new Thread(world);
		Stage stage = new Stage();
		stage.setTitle("Singleplayer");
		stage.setOnCloseRequest(e -> System.exit(0));
		stage.setScene(new Scene(initUI(), 1000, 600));
		stage.setResizable(false);
		stage.show();
		logic.start(); // since UI takes time to load, start logic after UI
	}

	protected BorderPane initUI() {
		BorderPane ret = new BorderPane();
		ret.setTop(configTop());
		ret.setRight(configRight());
		ret.setCenter(configCenter());
		ret.setBottom(configBottom());
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
		spawnMsg.setFont(new Font(Def.font, 18));
		spawnMsg.setFill(Color.CRIMSON);
		spawnMsg.setTextAlignment(TextAlignment.JUSTIFY);
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
			red.setOnMouseClicked(new detailHandler(i, 0));
			ImageView blue = new ImageView();
			blue.setFitHeight(Def.cityW / 2);
			blue.setFitWidth(Def.cityW / 2);
			blue.setOnMouseClicked(new detailHandler(i, 1));
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

	protected HBox configBottom() {
		HBox ret = new HBox(20);
		ret.setStyle("-fx-background-color: F0F8FF;");
		ret.setPadding(new Insets(15, 12, 15, 12));
		ret.setSpacing(10);

		thumbnail = new ImageView();
		thumbnail.setFitHeight(100);
		thumbnail.setFitWidth(100);
		ret.getChildren().add(thumbnail);

		GridPane details = new GridPane();
		details.setPadding(new Insets(5, 10, 5, 10));
		details.setPrefSize(800, 35 * 4);
		details.setGridLinesVisible(true);
		configPropertyTitles(details);
		configPropertyFields(details);
		setConstraints(details);
		ret.getChildren().add(details);

		return ret;
	}

	private void configPropertyTitles(GridPane grid) {
		grid.add(getPropertyName("Type"), 0, 0);
		grid.add(getPropertyName("Party"), 0, 1);
		grid.add(getPropertyName("ID"), 0, 2);
		grid.add(getPropertyName("Location"), 0, 3);
		grid.add(getPropertyName("HP"), 2, 0);
		grid.add(getPropertyName("Attack"), 2, 1);
		grid.add(getPropertyName("Kill"), 2, 2);
		grid.add(getPropertyName("Step"), 2, 3);
	}

	private void configPropertyFields(GridPane grid) {
		for (int i = 0; i < 8; i++) {
			propertyV[i] = new Text();
		}
		for (int row = 0; row < 4; row++) {
			grid.add(propertyV[2 * row], 1, row);
			grid.add(propertyV[2 * row + 1], 3, row);
		}
	}

	// this function took me a day to figure out
	private void setConstraints(GridPane grid) {
		ColumnConstraints colConstraints = new ColumnConstraints();
		RowConstraints rowConstraints = new RowConstraints();
		colConstraints.setPrefWidth(150);
		rowConstraints.setPrefHeight(35);
		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 1; col++) {
				grid.getColumnConstraints().add(colConstraints);
				grid.getRowConstraints().add(rowConstraints);
			}
	}

	private Text getPropertyName(String name) {
		Text ret = new Text(" " + name);
		ret.setFont(new Font(Def.font, 16));
		ret.setFill(Color.RED);
		ret.setTextAlignment(TextAlignment.LEFT);
		return ret;
	}

	private class spawnHandler implements EventHandler<MouseEvent> {
		WarriorType.type requestType;

		spawnHandler(WarriorType.type t) {
			requestType = t;
		}

		@Override
		public void handle(MouseEvent event) {
			if (world.end.get())
				return;
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

	private class detailHandler implements EventHandler<MouseEvent> {
		int city;
		int t;

		detailHandler(int cityIndex, int team) {
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

	void displayWarrior(Warrior w, ImageView i) {
		if (w == null) {// this means the city is empty
			i.setImage(null);
			return;
		}
		if (w instanceof Lion) {
			i.setImage(w.getTeam() == Team.red ? imgs.lionRedHD : imgs.lionBlueHD);
			return;
		}
		if (w instanceof Dragon) {
			i.setImage(w.getTeam() == Team.red ? imgs.dragonRedHD : imgs.dragonBlueHD);
			return;
		}
		if (w instanceof Iceman) {
			i.setImage(w.getTeam() == Team.red ? imgs.icemanRedHD : imgs.icemanBlueHD);
			return;
		}
		if (w instanceof Ninja) {
			i.setImage(w.getTeam() == Team.red ? imgs.ninjaRedHD : imgs.ninjaBlueHD);
			return;
		}
		if (w instanceof Wolf) {
			i.setImage(w.getTeam() == Team.red ? imgs.wolfRedHD : imgs.wolfBlueHD);
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
