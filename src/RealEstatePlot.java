
public class RealEstatePlot {

	private Building building;
	private int row;
	private int col;
	
	public RealEstatePlot() {
	}
	
	public RealEstatePlot(int row, int col,Building building) {
		this.row=row;
		this.col=col;
		this.building=building;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public Building getBuilding() {
		return building;
	}
	
}
