
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class StartButtonController implements EventHandler {

	private VisualizationModel model;
	private VisualizationView view;

	public StartButtonController(VisualizationModel model) {
		this.model=model;
	}

	@Override
	public void handle(Event event) {
		((Node)event.getSource()).getScene().getWindow().hide();
		
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
