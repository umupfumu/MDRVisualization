import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

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
	
	private static int WORK_LIKELIHOOD_PERCENT = 70;
	private static int NUM_STARTING_INFECTED = 2;
	private static int LIKELIHOOD_TO_INFECT_PERCENT = 100; //percent per day. 1 is 1% not 100%
	private static int DEVELOP_ACTIVE_PERCENT=50;
	
	private int buildingRows;
	private int buildingCols;
	
	private RealEstatePlot[][] realEstatePlots;
	
	public VisualizationModel() {
		buildingRows = N_ROWS / N_ROWS_PER_BUILDING;
		buildingCols = N_COLUMNS / N_COLUMNS_PER_BUILDING;
		
		realEstatePlots = new RealEstatePlot[buildingRows][buildingCols];
		for(int row=0;row<buildingRows;row++) {
			for(int col=0;col<buildingCols;col++) {
				realEstatePlots[row][col]=new RealEstatePlot(row,col,null);
			}
		}

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
			if(i<NUM_STARTING_INFECTED) {
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

	public void advanceDay() {		

		clearWorkplaces();
		
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			if(ThreadLocalRandom.current().nextInt(0,100)<WORK_LIKELIHOOD_PERCENT) {
				assignWorkplace(person);
			}	
		}
		
		for(int i=0;i<people.size();i++) {
			Person person = people.get(i);
			if(person.getInfected()){
				if(person.getDiseaseState()==DiseaseState.LATENT)
					if(ThreadLocalRandom.current().nextInt(0,100)<DEVELOP_ACTIVE_PERCENT)
						person.setDiseaseState(DiseaseState.ACTIVE);
				if(person.getDiseaseState()==DiseaseState.ACTIVE)
					spreadInfection(person);
			}
		}
		
		day++;

		setChanged();
		notifyObservers(WhatHappened.DAY_ADVANCED);
	}

	private void spreadInfection(Person person) {
		Home home = person.getHome();
		
		ArrayList<Person> houseMates = home.getPeopleLivingHere();
		
		for(int i=0;i<houseMates.size();i++) {
			Person housemate = houseMates.get(i);
			if(ThreadLocalRandom.current().nextDouble(100)<LIKELIHOOD_TO_INFECT_PERCENT) {
				if(housemate!=person)
					housemate.infect(person);
			}
		}
		
		Workplace workplace = person.getWorkplace();
		if(workplace!=null) {
			ArrayList<Person> workmates = workplace.getCurrentOccupants();
			for(int i=0;i<workmates.size();i++) {
				Person workmate = workmates.get(i);
				if(ThreadLocalRandom.current().nextInt(0,100)<LIKELIHOOD_TO_INFECT_PERCENT) {
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

	

}