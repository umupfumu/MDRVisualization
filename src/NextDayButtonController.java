import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class NextDayButtonController implements EventHandler {

	private VisualizationModel model;
	
	public NextDayButtonController(VisualizationModel myModel) {
		model=myModel;
	}

	@Override
	public void handle(Event arg0) {
		
		int nDays = ((Integer)((Button) arg0.getSource()).getUserData());
		
		for(int i=0;i<nDays;i++)
			model.advanceDay(i==nDays-1);
	}

}
