
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class StartButtonController implements EventHandler {

	private VisualizationModel model;
	private VisualizationView view;
	
	public void addModel(VisualizationModel m) {
		this.model=m;
	}

	public void addView(VisualizationView v) {
		this.view = v;
	}

	@Override
	public void handle(Event event) {
		try {
			model.runModel();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			e.printStackTrace(System.out);
		}
	}
}
