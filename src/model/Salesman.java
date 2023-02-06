package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.enums.Gender;

public class Salesman extends User {
	
	private List<Integer> manifestationIDs;
	private Boolean deleted;
	private Boolean blocked;

	public Salesman() {}
	
	public Salesman(String username, String password, String firstName, String lastName, Gender gender, Date birthDate) {
		super(username, password, firstName, lastName, gender, birthDate);
		this.manifestationIDs = new ArrayList<Integer>();
		this.deleted = false;
		this.blocked = false;
	}
	
	public Salesman(Salesman salesman) {
		super(salesman.getUsername(), salesman.getPassword(), salesman.getFirstName(), salesman.getLastName(), salesman.getGender(), salesman.getBirthDate());
		this.manifestationIDs = (salesman.getManifestationIDs() != null) ? new ArrayList<Integer>(salesman.getManifestationIDs()) : new ArrayList<Integer>();
		this.deleted = salesman.isDeleted();
		this.blocked = salesman.getBlocked();
	}

	public void addManifestation(Integer manifestation) {
		this.manifestationIDs.add(manifestation);
	}

	public Boolean isDeleted() {
		return deleted;
	}

	public List<Integer> getManifestationIDs() {
		return manifestationIDs;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getBlocked() {
		return blocked;
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}
}
