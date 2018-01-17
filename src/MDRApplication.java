
import javafx.application.Application;
import javafx.stage.Stage;

public class MDRApplication extends Application{
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VisualizationModel myModel = new VisualizationModel();
		VisualizationView myView = new VisualizationView(primaryStage,myModel);
		StartButtonController myController = new StartButtonController();
		NextDayButtonController nextDayController = new NextDayButtonController(myModel);
		
		myModel.addObserver(myView);
		myController.addModel(myModel);
		myController.addView(myView);
		
		myView.addStartController(myController);
		myView.addNextDayController(nextDayController);
		
		myView.drawDisplay();
	}
	
}
