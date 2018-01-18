
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VisualizationView implements Observer {
	
	private VisualizationModel model;
	
	private ArrayList<Rectangle> buildings = new ArrayList<Rectangle>();
	
	private Stage primaryStage;

	private Button startButton = new Button("Start");

	private Button nextDayButton = new Button("Next day");
	private Button advanceThirtyButton = new Button("Advance 30 days");
	private Button advanceThreeSixtyFiveButton = new Button("Advance 365 days");
	
	private Pane mainPane;
	private Label dayLabel;
	private BorderPane borderPane;
	private OuterScene outerScene;
	private Image wellImage;
	private Image sickImage;
	private Image latentImage;
	private Image latentMDRImage;
	private Image activeMDRImage;
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
	private static String MDR_ACTIVE_FILE = "MDRactive.png";
	private static String MDR_LATENT_FILE = "MDRlatent.png";
	
	public VisualizationView(Stage stage,VisualizationModel model) {
		this.primaryStage=stage;
		File wellImageFile = new File(WELL_IMAGE_FILE);
		File sickImageFile = new File(SICK_IMAGE_FILE);
		File latentImageFile = new File(LATENT_IMAGE_FILE);
		File activeMDRFile = new File(MDR_ACTIVE_FILE);
		File latentMDRFile = new File(MDR_LATENT_FILE);
		wellImage = new Image(wellImageFile.toURI().toString());
		sickImage = new Image(sickImageFile.toURI().toString());
		latentImage = new Image(latentImageFile.toURI().toString());
		latentMDRImage = new Image(latentMDRFile.toURI().toString());
		activeMDRImage = new Image(activeMDRFile.toURI().toString());
		dayLabel = new Label();
		
		this.model=model;
	}
	
	public void drawDisplay() {
		primaryStage.setTitle("MDR Visualization");

		borderPane = new BorderPane();
		ToolBar toolbar = new ToolBar();
		VBox bottomBox = new VBox();
		HBox buttonPane = new HBox();

		Button seeLegend = new Button();
		seeLegend.setText("See Legend");
		seeLegend.setOnAction(new EventHandler() {
			@Override
			public void handle(Event arg0) {
				
				ImageView well = new ImageView();
				ImageView activeMDR = new ImageView();
				ImageView sick = new ImageView();
				ImageView latent = new ImageView();
				ImageView latentMDR = new ImageView();
				
				well.setImage(wellImage);
				well.setFitHeight(ROW_HEIGHT);
				well.setFitWidth(COLUMN_WIDTH);
				sick.setImage(sickImage);
				sick.setFitHeight(ROW_HEIGHT);
				sick.setFitWidth(COLUMN_WIDTH);
				activeMDR.setImage(activeMDRImage);
				activeMDR.setFitHeight(ROW_HEIGHT);
				activeMDR.setFitWidth(COLUMN_WIDTH);
				latent.setImage(latentImage);
				latent.setFitHeight(ROW_HEIGHT);
				latent.setFitWidth(COLUMN_WIDTH);
				latentMDR.setImage(latentMDRImage);
				latentMDR.setFitHeight(ROW_HEIGHT);
				latentMDR.setFitWidth(COLUMN_WIDTH);
				
				int buildingWidth = VisualizationModel.N_COLUMNS_PER_BUILDING*COLUMN_WIDTH;
				int buildingHeight = VisualizationModel.N_ROWS_PER_BUILDING*ROW_HEIGHT;

				Rectangle house = new Rectangle(0,0,buildingWidth,buildingHeight);
				Rectangle workplace = new Rectangle(0,0,buildingWidth,buildingHeight);
				
				house.setStroke(Color.BLACK);
				house.setFill(Color.WHITE);
				
				workplace.setStroke(Color.BLUE);
				workplace.setArcHeight(10);
				workplace.setArcWidth(10);
				workplace.setFill(Color.WHITE);
				
				Stage dialogStage = new Stage();
				GridPane grid = new GridPane();
				Scene legendScene = new Scene(grid);
				
				grid.add(house, 0, 0,1,1);
				grid.add(workplace, 0,1,1,1);
				grid.add(well, 0, 2,1,1);
				grid.add(sick, 0, 3,1,1);
				grid.add(latent, 0, 4,1,1);
				grid.add(activeMDR, 0, 5,1,1);
				grid.add(latentMDR, 0, 6,1,1);
				
				grid.add(new Text("House"), 1,0,1,1);
				grid.add(new Text("Workplace or school"), 1, 1);
				grid.add(new Text("Well"), 1, 2);
				grid.add(new Text("Infected"), 1, 3);
				grid.add(new Text("Latent"), 1, 4);
				grid.add(new Text("Infected MDR"), 1, 5);
				grid.add(new Text("Latent MDR"), 1, 6);
				
				grid.setVgap(3);
				grid.setHgap(2);
				
				dialogStage.setAlwaysOnTop(true);
				
				dialogStage.setScene(legendScene);
				dialogStage.show();
			}			
		});
		

		
		nextDayButton.setVisible(false);
		advanceThirtyButton.setVisible(false);
		advanceThreeSixtyFiveButton.setVisible(false);
		
		nextDayButton.setUserData(1);
		advanceThirtyButton.setUserData(30);
		advanceThreeSixtyFiveButton.setUserData(365);
		
		advanceThirtyButton.setOnAction(new NextDayButtonController(model));
		advanceThreeSixtyFiveButton.setOnAction(new NextDayButtonController(model));
		
//		startButton.setOnAction(startButtonController);
		startButton.setOnAction(new SettingsPopup(model));
		
		nextDayButton.setOnAction(new NextDayButtonController(model));
		
		buttonPane.getChildren().add(startButton);
		buttonPane.getChildren().add(nextDayButton);
		buttonPane.getChildren().add(advanceThirtyButton);
		buttonPane.getChildren().add(advanceThreeSixtyFiveButton);
		buttonPane.getChildren().add(seeLegend);
		
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
				startButton.setText("Reset");
				startButton.setOnAction(null);
				startButton.setOnAction(new ResetButtonController(model));
				nextDayButton.setVisible(true);
				advanceThirtyButton.setVisible(true);
				advanceThreeSixtyFiveButton.setVisible(true);				
				break;
			}
			case DAY_ADVANCED:{
				updateLabels(model);
				updateDiseaseState(model);
				animatePeopleToWorkplace(model);
				break;
			}
			case MODEL_RESET:{
				startButton.setText("Start");
				startButton.setOnAction(null);
				startButton.setOnAction(new SettingsPopup(model));
				nextDayButton.setVisible(false);
				advanceThirtyButton.setVisible(false);
				advanceThreeSixtyFiveButton.setVisible(false);
				updateLabels(model);
				updateDiseaseState(model);
				removeBuildings();
				break;
			}
		}
		
	}

	private void removeBuildings() {
		for(int i =0;i<buildings.size();i++)
			buildings.get(i).setVisible(false);
	}

	private void updateDiseaseState(VisualizationModel model) {
		ArrayList<Person> people = model.getPeople();
		Iterator it = imageViewMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Person person = (Person) pair.getKey();
			ImageView imageView = (ImageView) pair.getValue();
			if(!people.contains(person)) {
				imageView.setImage(null);
				it.remove();
			}
		}
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			ImageView imageView = imageViewMap.get(person);
			updateImageForDiseaseState(imageView,person);
		}
	}

	private void updateImageForDiseaseState(ImageView imageView, Person person) {
		DiseaseState diseaseState = person.getDiseaseState();
		ResistanceProfile resistanceProfile = person.getResistanceProfile();
		boolean infected = person.getInfected();
		if(infected) {
			if(diseaseState==DiseaseState.LATENT) {
				if(resistanceProfile==ResistanceProfile.MDR) {
					imageView.setImage(latentMDRImage);
				} else {
					imageView.setImage(latentImage);
				}
			} else {
				if(resistanceProfile==ResistanceProfile.MDR){
					imageView.setImage(activeMDRImage);
				} else {
					imageView.setImage(sickImage);					
				}
			}
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
		buildings.add(rect);
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


}
