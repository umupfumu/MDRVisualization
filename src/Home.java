import java.util.ArrayList;

public class Home extends Building {

	private ArrayList<Person> peopleLivingHere = new ArrayList<Person>();
	
	public Home(int buildingRow, int buildingCol) {
		super(buildingRow, buildingCol);
	}

	public int getNumPeopleLivingHere() {
		return peopleLivingHere.size();
	}

	public void addPersonWhoLivesHere(Person person) {
		peopleLivingHere.add(person);
	}

	public ArrayList<Person> getPeopleLivingHere() {
		return peopleLivingHere;
	}

}
