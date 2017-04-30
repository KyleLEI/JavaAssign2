package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class SP implements EventHandler<ActionEvent> {
	int[][] m_param;

	public SP(int param[][]) {
		m_param=param;
	}

	@Override
	public void handle(ActionEvent event) {
		SPWorld world=new SPWorld(m_param);
		Thread logic = new Thread(world);
		logic.start();

		Stage stage = new Stage();
		stage.setTitle("Singleplayer");

		stage.setOnCloseRequest(e->world.task_should_exit=true);
		stage.show();
		// Hide this current window (if this is what you want)

	}

}
