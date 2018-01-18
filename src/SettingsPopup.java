import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

public class SettingsPopup implements EventHandler {

	private VisualizationModel model;
	
	public SettingsPopup(VisualizationModel model) {
		this.model=model;
	}
	
	@Override
	public void handle(Event event) {
		Stage stage = new Stage();
		
		GridPane grid = new GridPane();
		
		TextField nStarting = new TextField();
		TextField devMDRPercent = new TextField();
		TextField workPercent = new TextField();
		TextField spreadInfect = new TextField();
		TextField latentToActive = new TextField();
		TextField joinTreatPercent = new TextField();
		
		Button okButton = new Button("OK");
		
		nStarting.textProperty().bindBidirectional(model.getNumStartingInfected(), new NumberStringConverter());
		devMDRPercent.textProperty().bindBidirectional(model.getDevelopMDRPercent(),new NumberStringConverter());
		workPercent.textProperty().bindBidirectional(model.getWorkLikelihoodPercent(), new NumberStringConverter());
		spreadInfect.textProperty().bindBidirectional(model.getLikelihoodInfectPercent(),new NumberStringConverter());
		latentToActive.textProperty().bindBidirectional(model.getDevelopActivePercent(),new NumberStringConverter());
		joinTreatPercent.textProperty().bindBidirectional(model.getLikelihoodJoinTreatment(),new NumberStringConverter());

		grid.add(nStarting, 0, 0);
		grid.add(new Text("Number of infected people."), 1, 0);
		grid.add(spreadInfect, 0, 1);
		grid.add(new Text("Percent chance per day per contact of spreading infection."), 1, 1);
		grid.add(latentToActive, 0, 2);
		grid.add(new Text("Percent chance per day latent infection turns into active infection."), 1, 2);
		grid.add(joinTreatPercent, 0, 3);
		grid.add(new Text("Percent chance per day an actively infected individual joins the treatment program."), 1, 3);
		grid.add(devMDRPercent, 0, 4);
		grid.add(new Text("Percent chance per day a person undergoing treatment develops MDR."), 1, 4);
		grid.add(workPercent, 0, 5);
		grid.add(new Text("Percent chance per day an individual goes to work or school."), 1, 5);
		grid.add(okButton, 1,6 );
		
		grid.setVgap(3);
		grid.setHgap(3);
		
		okButton.setOnAction(new StartButtonController(model));
		
		
		stage.setAlwaysOnTop(true);		
		Scene scene = new Scene(grid);				
		stage.setScene(scene);
		stage.show();
	}

}
