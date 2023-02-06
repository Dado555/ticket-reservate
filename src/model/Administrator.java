package model;

public class Administrator extends User {
	
	public Administrator() {
		super();
	}

	public Administrator(Administrator admin) {
		super(admin.getUsername(), admin.getPassword(), admin.getFirstName(), admin.getLastName(), admin.getGender(), admin.getBirthDate());
	}

}
