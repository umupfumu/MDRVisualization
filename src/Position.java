
public class Position {
	int rowWithinBuilding;
	int colWithinBuilding;
	int globalRow;
	int globalCol;
	Person person;
	
	public Position(int rowWithinBuilding, int colWithinBuilding, int buildingRow, int buildingCol, Person person) {
		this.rowWithinBuilding=rowWithinBuilding;
		this.colWithinBuilding=colWithinBuilding;
		
		this.globalRow = buildingRow+rowWithinBuilding;
		this.globalCol = buildingCol+colWithinBuilding;
		this.person=person;
	}

	public int getRowWithinBuilding() {
		return rowWithinBuilding;
	}
	
	public int getColWithinBuilding() {
		return colWithinBuilding;
	}

	public int getGlobalRow() {
		return globalRow;
	}
	
	public int getGlobalCol() {
		return globalCol;
	}
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person=person;
	}
}
