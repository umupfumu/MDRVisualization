import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Building {

	private int row;
	private int column;
	private Position internalPositions[][];
	private int maximumOccupancy;
	
	private ArrayList<Person> currentOccupants = new ArrayList<Person>();
	
	public Building(int buildingRow, int buildingCol) {
		this.row=buildingRow;
		this.column=buildingCol;
		int nPositionRows = VisualizationModel.N_ROWS_PER_BUILDING;
		int nPositionCols = VisualizationModel.N_COLUMNS_PER_BUILDING;
		maximumOccupancy = nPositionRows * nPositionCols;
		clearOccupancy();
	}
	

	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.column;
	}
	
	//returns Position person was placed, or null if unable to place
	public Position addCurrentOccupant(Person person) {
		if(currentOccupants.size()==maximumOccupancy) {
			return null;
		} else {
			int row = ThreadLocalRandom.current().nextInt(0,VisualizationModel.N_ROWS_PER_BUILDING);
			int col = ThreadLocalRandom.current().nextInt(0,VisualizationModel.N_COLUMNS_PER_BUILDING);
			if(internalPositions[row][col].getPerson()==null) {
				internalPositions[row][col].setPerson(person);
				currentOccupants.add(person);
				return internalPositions[row][col];
			}else{
				return addCurrentOccupant(person);
			}
		}
	}


	public ArrayList<Person> getCurrentOccupants() {
		return currentOccupants;
	}


	public Position[][] getPositions() {
		return internalPositions;
	}
	
	public int getCurrentOccupancy() {
		return currentOccupants.size();
	}
	
	public int getMaximumOccupancy() {
		return maximumOccupancy;
	}
	
	public void clearOccupancy() {
		int nPositionRows = VisualizationModel.N_ROWS_PER_BUILDING;
		int nPositionCols = VisualizationModel.N_COLUMNS_PER_BUILDING;

		for(int i=0;i<currentOccupants.size();i++) {
			currentOccupants.get(i).setWorkplace(null);
		}
		
		currentOccupants = new ArrayList<Person>();
		internalPositions = new Position[nPositionRows][nPositionCols];
		
		for(int row =0;row<nPositionRows;row++) {
			for(int col=0;col<nPositionCols;col++) {
				int globalRow = this.row * VisualizationModel.N_ROWS_PER_BUILDING;
				int globalCol = this.column * VisualizationModel.N_COLUMNS_PER_BUILDING;
				internalPositions[row][col] = new Position(row,col,globalRow,globalCol,null);
			}
		}
	}
}
