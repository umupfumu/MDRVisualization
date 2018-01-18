import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class VisualizationModel extends Observable {
	private ArrayList<Home> homes = new ArrayList<Home>();
	private ArrayList<Workplace> workplaces = new ArrayList<Workplace>();
	private ArrayList<Person> people = new ArrayList<Person>();
	private static int day = 0;
	
	private static int MIN_HOMES = 20;
	private static int MAX_HOMES = 25;
	private static int MIN_WORKPLACES = 11;
	private static int MAX_WORKPLACES = 15;

	
	private static int MAX_PEOPLE_PER_HOME = 8;
	
	private static int MIN_PEOPLE = 100;
	private static int MAX_PEOPLE = 100;
	
	private static int N_ROWS = 54; //should be divisible by N_ROWS_PER_BUILDING
	private static int N_COLUMNS = 81; //should be divisible by N_COLUMNS_PER_BUILDING
	public static int N_COLUMNS_PER_BUILDING = 3;
	public static int N_ROWS_PER_BUILDING = 3;
	private static int N_DAYS_TO_CURE=180;



	private SimpleDoubleProperty workLikelihoodPercent = new SimpleDoubleProperty(70);
	private SimpleIntegerProperty numStartingInfected = new SimpleIntegerProperty(2);
	private SimpleDoubleProperty likelihoodInfectPercent = new SimpleDoubleProperty(1); //percent per day. 1 is 1% not 100%
	private SimpleDoubleProperty developActivePercent= new SimpleDoubleProperty(1);
	private SimpleDoubleProperty likelihoodJoinTreatment=new SimpleDoubleProperty(20);
	private SimpleDoubleProperty developMDRPercent= new SimpleDoubleProperty(1);
	
	private int buildingRows;
	private int buildingCols;
	
	private RealEstatePlot[][] realEstatePlots;
	
	public VisualizationModel() {
		clearPlots();
	}

	public void runModel() throws Exception {
		int nHomes = ThreadLocalRandom.current().nextInt(MIN_HOMES,MAX_HOMES+1);
		int nWorkplaces = ThreadLocalRandom.current().nextInt(MIN_WORKPLACES,MAX_WORKPLACES+1);
		
		int nPeople = ThreadLocalRandom.current().nextInt(MIN_PEOPLE,MAX_PEOPLE+1);
		
		if(MAX_PEOPLE_PER_HOME * nHomes < nPeople) throw new Exception("There aren't enough homes for that many people.");
		if(nHomes > buildingRows*buildingCols) throw new Exception("There isn't enough real estate for that many buildings.");
		if(N_ROWS_PER_BUILDING*N_COLUMNS_PER_BUILDING < (MAX_PEOPLE/MIN_WORKPLACES))  throw new Exception("There aren't enough workplaces for that many people");
		
		createHomes(nHomes);
		createWorkplaces(nWorkplaces);
		createPeople(nPeople);
		
		setChanged();
		notifyObservers(WhatHappened.MODEL_INITIATED);
	}
	
	private void createPeople(int nPeople) {
		for(int i=0;i<nPeople;i++) {

			Person person = new Person();
			if(i<numStartingInfected.get()) {
				person.setDiseaseState(DiseaseState.ACTIVE);
				person.setResistanceProfile(ResistanceProfile.NORMAL);
				person.setInfected(true);
			}
			assignHome(person);
			people.add(person);
		}
	}

	private void createWorkplaces(int nWorkplaces) {
		for(int i=0;i<nWorkplaces;i++) {
			addNewBuildingToRealEstate(Workplace.class);
		}
	}

	private void createHomes(int nHomes) {
		for(int i=0;i<nHomes;i++) {
			addNewBuildingToRealEstate(Home.class);
		}
	}

	private void addNewBuildingToRealEstate(Class buildingType) {
		int buildingRow = ThreadLocalRandom.current().nextInt(0,buildingRows);
		int buildingCol = ThreadLocalRandom.current().nextInt(0,buildingCols);
		RealEstatePlot plot = realEstatePlots[buildingRow][buildingCol];
		if(plot.getBuilding()==null) {
			Building building=null;
			if(buildingType.equals(Workplace.class)) {
				Workplace workplace = new Workplace(buildingRow,buildingCol);
				building=workplace;
				workplaces.add(workplace);
			}else if(buildingType.equals(Home.class)) {
				Home home = new Home(buildingRow,buildingCol);
				building=home;
				homes.add(home);
			}
			realEstatePlots[buildingRow][buildingCol] = new RealEstatePlot(buildingRow,buildingCol,building);
		}else { //plot occupied, try again
			addNewBuildingToRealEstate(buildingType);
		}
	}

	private void assignHome(Person person) {
		int nHomes = homes.size();
		int homeIndex = ThreadLocalRandom.current().nextInt(0,nHomes);
		Home home = homes.get(homeIndex);
		if(home.getNumPeopleLivingHere()>=MAX_PEOPLE_PER_HOME) {
			assignHome(person); // home full, try again
		}else {
			home.addPersonWhoLivesHere(person);
			Position pos = home.addCurrentOccupant(person);
			person.setHomePosition(pos);
			person.setHome(home);
		}
	}
	
	private void assignWorkplace(Person person) {
		int nWorkplaces = workplaces.size();
		int workplaceIndex = ThreadLocalRandom.current().nextInt(0,nWorkplaces);
		Workplace workplace = workplaces.get(workplaceIndex);
		Position pos = workplace.addCurrentOccupant(person);
		if(pos==null) { assignWorkplace(person);
		}else{
			person.setWorkPosition(pos);
			person.setWorkplace(workplace);
		} 
	}

	public ArrayList<Home> getHomes() {
		return homes;
	}

	public ArrayList<Workplace> getWorkplaces() {
		return workplaces;
	}

	public ArrayList<Person> getPeople() {
		return people;
	}

	public int getDay() {
		return day;
	}

	public void advanceDay(boolean shouldNotify) {		
		clearWorkplaces();
		assignWorkplaces();
		enrollInTreatment();
		incrementTreatmentDay();
		convertToMDR();
		spreadInfections();
		day++;
		setChanged();
		if(shouldNotify)
			notifyObservers(WhatHappened.DAY_ADVANCED);
	}

	private void convertToMDR() {
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			if(person.getInfected()
					&& person.isInTreatment()
					&& person.getResistanceProfile()!=ResistanceProfile.MDR) {
				if(ThreadLocalRandom.current().nextDouble(0,100)<developMDRPercent.get())
					person.setResistanceProfile(ResistanceProfile.MDR);
			}
		}
	}

	private void incrementTreatmentDay() {
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			if(person.getInfected()&&person.isInTreatment()) {
				person.setnDaysInTreatment(person.getnDaysInTreatment()+1);
				if(person.getnDaysInTreatment()==N_DAYS_TO_CURE)
					person.setInfected(false);
			}
		}
	}

	private void enrollInTreatment() {
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			if(person.getInfected() && person.getDiseaseState() == DiseaseState.ACTIVE 
					&& person.getResistanceProfile()!=ResistanceProfile.MDR
					&&!person.isInTreatment()) {
				if(ThreadLocalRandom.current().nextDouble(0,100)<likelihoodJoinTreatment.get())
					person.setInTreatment(true);
			}
		}
	}

	private void spreadInfections() {
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			if(person.getInfected()){
				if(person.getDiseaseState()==DiseaseState.LATENT)
					if(ThreadLocalRandom.current().nextDouble(0,100)<developActivePercent.get())
						person.setDiseaseState(DiseaseState.ACTIVE);
				if(person.getDiseaseState()==DiseaseState.ACTIVE)
					spreadInfection(person);
			}
		}
	}

	private void assignWorkplaces() {
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			if(ThreadLocalRandom.current().nextDouble(0,100)<workLikelihoodPercent.get()) {
				assignWorkplace(person);
			}	
		}
	}

	private void spreadInfection(Person person) {
		Home home = person.getHome();
		
		ArrayList<Person> houseMates = home.getPeopleLivingHere();
		
		for(int i=0;i<houseMates.size();i++) {
			Person housemate = houseMates.get(i);
			if(ThreadLocalRandom.current().nextDouble(100)<likelihoodInfectPercent.get()) {
				if(housemate!=person)
					housemate.infect(person);
			}
		}
		
		Workplace workplace = person.getWorkplace();
		if(workplace!=null) {
			ArrayList<Person> workmates = workplace.getCurrentOccupants();
			for(int i=0;i<workmates.size();i++) {
				Person workmate = workmates.get(i);
				if(ThreadLocalRandom.current().nextDouble(0,100)<likelihoodInfectPercent.get()) {
					if(workmate!=person)
						workmate.infect(person);
				}
			}
		}
	}

	private void clearWorkplaces() {
		for(int i =0; i<workplaces.size();i++) {
			workplaces.get(i).clearOccupancy();
		}
	}

	public void reset() {
		clearPlots();
		workplaces=new ArrayList<Workplace>();
		people=new ArrayList<Person>();
		homes=new ArrayList<Home>();
		day=0;
		setChanged();
		notifyObservers(WhatHappened.MODEL_RESET);
	}

	private void clearPlots() {
		buildingRows = N_ROWS / N_ROWS_PER_BUILDING;
		buildingCols = N_COLUMNS / N_COLUMNS_PER_BUILDING;
		
		realEstatePlots = new RealEstatePlot[buildingRows][buildingCols];
		for(int row=0;row<buildingRows;row++) {
			for(int col=0;col<buildingCols;col++) {
				realEstatePlots[row][col]=new RealEstatePlot(row,col,null);
			}
		}
	}

	
	public SimpleDoubleProperty getWorkLikelihoodPercent() {
		return workLikelihoodPercent;
	}

	public SimpleIntegerProperty getNumStartingInfected() {
		return numStartingInfected;
	}

	public SimpleDoubleProperty getLikelihoodInfectPercent() {
		return likelihoodInfectPercent;
	}

	public SimpleDoubleProperty getDevelopActivePercent() {
		return developActivePercent;
	}

	public SimpleDoubleProperty getLikelihoodJoinTreatment() {
		return likelihoodJoinTreatment;
	}

	public SimpleDoubleProperty getDevelopMDRPercent() {
		return developMDRPercent;
	}
}
