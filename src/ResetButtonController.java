import javafx.event.Event;
import javafx.event.EventHandler;

public class ResetButtonController implements EventHandler {
	private VisualizationModel model;
	
	public void addModel(VisualizationModel m) {
		this.model=m;
	}

	@Override
	public void handle(Event event) {
		model.reset();
	}

}
