
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VisualizationView implements Observer {

	private StartButtonController startButtonController;
	private NextDayButtonController nextDayButtonController;
	
	private Stage primaryStage;

	private Pane mainPane;
	private Label dayLabel;
	private BorderPane borderPane;
	private OuterScene outerScene;
	private Image wellImage;
	private Image sickImage;
	private Image latentImage;
	private HashMap<Person,ImageView> imageViewMap = new HashMap<Person,ImageView>();
	
	private static int CANVAS_WIDTH =1000;
	private static int CANVAS_HEIGHT=700;
	private static int ROW_HEIGHT = 12;
	private static int COLUMN_WIDTH = 12;
	private static int TIME_TO_WORKPLACE = 3;
	
	private static Color WORKPLACE_COLOR = Color.BLUE;
	private static Color HOME_COLOR = Color.BLACK;
	
	private static enum SHAPE_TYPE {RECTANGLE,ROUNDED_RECTANGLE};
	
	private static String WELL_IMAGE_FILE = "wellPerson.png";
	private static String SICK_IMAGE_FILE = "sickPerson.png";
	private static String LATENT_IMAGE_FILE = "latentPerson.png";
	
	public VisualizationView(Stage stage,VisualizationModel model) {
		this.primaryStage=stage;
		File wellImageFile = new File(WELL_IMAGE_FILE);
		File sickImageFile = new File(SICK_IMAGE_FILE);
		File latentImageFile = new File(LATENT_IMAGE_FILE);
		wellImage = new Image(wellImageFile.toURI().toString());
		sickImage = new Image(sickImageFile.toURI().toString());
		latentImage = new Image(latentImageFile.toURI().toString());
		dayLabel = new Label();
	}
	
	public void drawDisplay() {
		primaryStage.setTitle("MDR Visualization");

		borderPane = new BorderPane();
		ToolBar toolbar = new ToolBar();
		VBox bottomBox = new VBox();
		HBox buttonPane = new HBox();
		Button startButton = new Button("Start");
		Button nextDayButton = new Button("Next day");
		
		startButton.setOnAction(startButtonController);
		nextDayButton.setOnAction(nextDayButtonController);
		
		buttonPane.getChildren().add(startButton);
		buttonPane.getChildren().add(nextDayButton);
		
		buttonPane.setAlignment(Pos.BOTTOM_CENTER);
		
		bottomBox.getChildren().add(dayLabel);
		bottomBox.getChildren().add(buttonPane);
		bottomBox.setAlignment(Pos.BOTTOM_CENTER);
		
		mainPane = new Pane();
		mainPane.setStyle("-fx-background-color:white");
		
		borderPane.setCenter(mainPane);
		
		borderPane.setTop(toolbar);
		borderPane.setBottom(bottomBox);
		
		borderPane.setStyle("-fx-border-width:2;" +
						"-fx-border-style: solid inside;");

		
		outerScene = new OuterScene(borderPane);
		primaryStage.setScene(outerScene);
		
		primaryStage.show();

	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		VisualizationModel model = ((VisualizationModel)arg0);
		WhatHappened whatHappened = (WhatHappened)arg1;
		
		switch(whatHappened) {
			case MODEL_INITIATED:{
				initView(model);
				break;
			}
			case DAY_ADVANCED:{
				updateLabels(model);
				updateDiseaseState(model);
				animatePeopleToWorkplace(model);
				break;
			}
		}
		
	}

	private void updateDiseaseState(VisualizationModel model) {
		ArrayList<Person> people = model.getPeople();
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			ImageView imageView = imageViewMap.get(person);
			updateImageForDiseaseState(imageView,person);
		}
	}

	private void updateImageForDiseaseState(ImageView imageView, Person person) {
		DiseaseState diseaseState = person.getDiseaseState();
		boolean infected = person.getInfected();
		if(infected) {
			if(diseaseState==diseaseState.LATENT)
				imageView.setImage(latentImage);
			else
				imageView.setImage(sickImage);
		} else {
			imageView.setImage(wellImage);
		}
	}

	private void initView(VisualizationModel model) {
		drawHomes(model);
		drawWorkplaces(model);
		drawPeople(model);
		updateLabels(model);
	}

	private void drawPeople(VisualizationModel model) {
		ArrayList<Person> people = model.getPeople();
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			Position position = person.getHomePosition();
			
			
			int globalRow = position.getGlobalRow();
			int globalCol = position.getGlobalCol();
			
			int xLoc = globalCol * COLUMN_WIDTH;
			int yLoc = globalRow * ROW_HEIGHT;
			
			ImageView imageView = new ImageView();
			
			updateImageForDiseaseState(imageView, person);
			
			imageView.setX(xLoc);
			imageView.setY(yLoc);
			imageView.setFitHeight(ROW_HEIGHT);
			imageView.setFitWidth(COLUMN_WIDTH);
			
			imageViewMap.put(person, imageView);
			mainPane.getChildren().add(imageView);
		}
	}

	private void animatePeopleToWorkplace(VisualizationModel model) {
		ArrayList<Person> people = model.getPeople();
		
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			if(person.getWorkPosition()!=null)
				animatePersonToWorkplace(person,model);
		}
	}
	
	private void animatePersonToWorkplace(Person person,VisualizationModel model) {
		
		IntegerProperty xLoc = new SimpleIntegerProperty();
		IntegerProperty yLoc = new SimpleIntegerProperty();
		
		Position homePos = person.getHomePosition();
		Position workPos = person.getWorkPosition();
		
		int xLocHome = homePos.getGlobalCol() * COLUMN_WIDTH;
		int yLocHome = homePos.getGlobalRow() * ROW_HEIGHT;
		
		int xLocWork = workPos.getGlobalCol() * COLUMN_WIDTH;
		int yLocWork = workPos.getGlobalRow() * ROW_HEIGHT;
		
		int toX = xLocWork-xLocHome;
		int toY = yLocWork-yLocHome;
		
		SequentialTransition transition = new SequentialTransition();
		
		TranslateTransition toWork = new TranslateTransition();
		
		toWork.setToX(toX);
		toWork.setToY(toY);

		toWork.setDuration(Duration.seconds(TIME_TO_WORKPLACE));
		toWork.setNode(imageViewMap.get(person));
		
		TranslateTransition toHome = new TranslateTransition();
		
		toHome.setByX(-toX);
		toHome.setByY(-toY);

		toHome.setDuration(Duration.seconds(TIME_TO_WORKPLACE));
		toHome.setNode(imageViewMap.get(person));
/*		
		transition.setCycleCount(2);
		transition.setAutoReverse(true);*/
		
		transition.getChildren().addAll(toWork,toHome);
		transition.play();
	}

	private void updateLabels(VisualizationModel model) {
		dayLabel.setText("Day " + model.getDay());
	}

	private void drawWorkplaces(VisualizationModel model) {
		ArrayList<Workplace> workplaces = model.getWorkplaces();
		int nWorkplaces = workplaces.size();
		
		for(int i=0;i<nWorkplaces;i++) {
			drawBuilding(workplaces.get(i),SHAPE_TYPE.RECTANGLE,WORKPLACE_COLOR);
		}
	}

	private void drawBuilding(Building building, SHAPE_TYPE shapeType,Color color) {
		int row = building.getRow();
		int col = building.getCol();
		
		int buildingWidth = VisualizationModel.N_COLUMNS_PER_BUILDING*COLUMN_WIDTH;
		int buildingHeight = VisualizationModel.N_ROWS_PER_BUILDING*ROW_HEIGHT;
		
		int xLoc = col*buildingWidth;
		int yLoc = row*buildingHeight;

		Rectangle rect = new Rectangle(xLoc,yLoc,buildingWidth,buildingHeight);
		rect.setStroke(color);
		rect.setFill(Color.WHITE);
		
		if(shapeType==SHAPE_TYPE.ROUNDED_RECTANGLE) {
			rect.setArcWidth(10);
			rect.setArcHeight(10);
		}
		
		mainPane.getChildren().add(rect);
	}

	private void drawHomes(VisualizationModel model) {
		ArrayList<Home> homes = model.getHomes();
		int nHomes = homes.size();
		
		for(int i=0;i<nHomes;i++) {
			drawBuilding(homes.get(i),SHAPE_TYPE.ROUNDED_RECTANGLE,HOME_COLOR);
		}
	}

	public void addStartController(StartButtonController c) {
		this.startButtonController = c;
	}

	public void addNextDayController(NextDayButtonController nextDayController) {
		this.nextDayButtonController = nextDayController;
	}


}