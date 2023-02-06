package web.dto;

import java.util.Date;
import java.util.List;

import model.enums.Gender;
import model.enums.UserType;

public class SalesmanDTO extends UserDTO {

	private List<Integer> manifestationIDs;
	private Boolean deleted;
	
	public SalesmanDTO(String username, String firstName, String lastName, Gender gender, Date birthDate, UserType userType, List<Integer> manifestationIDs, Boolean deleted) {
		super(username, firstName, lastName, gender, birthDate, userType);
		this.manifestationIDs = manifestationIDs;
		this.deleted = deleted;
	}

	public List<Integer> getManifestationIDs() {
		return manifestationIDs;
	}
	public void setManifestationIDs(List<Integer> manifestationIDs) {
		this.manifestationIDs = manifestationIDs;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	
}
