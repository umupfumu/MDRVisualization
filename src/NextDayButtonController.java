import javafx.event.Event;
import javafx.event.EventHandler;

public class NextDayButtonController implements EventHandler {

	private VisualizationModel model;
	
	public NextDayButtonController(VisualizationModel myModel) {
		model=myModel;
	}

	@Override
	public void handle(Event arg0) {
		model.advanceDay();
	}

}
