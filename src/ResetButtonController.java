import javafx.event.Event;
import javafx.event.EventHandler;

public class ResetButtonController implements EventHandler {
	private VisualizationModel model;
	

	public ResetButtonController(VisualizationModel model) {
		this.model=model;
	}
	
	@Override
	public void handle(Event event) {
		model.reset();
	}

}
