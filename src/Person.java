
public class Person {

	private Position homePos;
	private Position workPos;
	private DiseaseState diseaseState;
	private ResistanceProfile resistanceProfile;
	private boolean inTreatment;
	private int nDaysInTreatment=0;
	
	public int getnDaysInTreatment() {
		return nDaysInTreatment;
	}

	public void setnDaysInTreatment(int nDaysInTreatment) {
		this.nDaysInTreatment = nDaysInTreatment;
	}

	public boolean isInTreatment() {
		return inTreatment;
	}

	public void setInTreatment(boolean inTreatment) {
		this.inTreatment = inTreatment;
	}

	public ResistanceProfile getResistanceProfile() {
		return resistanceProfile;
	}

	public void setResistanceProfile(ResistanceProfile resistanceProfile) {
		this.resistanceProfile = resistanceProfile;
	}

	public void setDiseaseState(DiseaseState diseaseState) {
		this.diseaseState = diseaseState;
	}

	private boolean infected;
	private Home home;
	public Home getHome() {
		return home;
	}

	public void setHome(Home home) {
		this.home = home;
	}

	public Workplace getWorkplace() {
		return workplace;
	}

	public void setWorkplace(Workplace workplace) {
		this.workplace = workplace;
	}

	private Workplace workplace;
	
	public void setHomePosition(Position pos) {
		this.homePos=pos;
	}
	
	public Position getHomePosition() {
		return homePos;
	}

	public void setWorkPosition(Position pos) {
		this.workPos=pos;
	}
	
	public Position getWorkPosition() {
		return this.workPos;
	}

	public void infect(Person infector) {
		if(!infected) {
			this.diseaseState=DiseaseState.LATENT;
			this.resistanceProfile=infector.getResistanceProfile();
		}else {
			if(infector.resistanceProfile==ResistanceProfile.MDR)
				this.resistanceProfile=ResistanceProfile.MDR;
		}
		
		this.infected=true;
	}

	public DiseaseState getDiseaseState() {
		return diseaseState;
	}
	
	public boolean getInfected() {
		return infected;
	}

	public void setInfected(boolean b) {
		this.infected=b;
	}

	
}
