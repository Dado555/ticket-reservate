package model;

import java.util.Date;

public class Cancelation {
	
	private Date dateTime;
	private String username;

	public Cancelation() {
		
	}

	public Cancelation(Date dateTime, String username) {
		super();
		this.dateTime = dateTime;
		this.username = username;
	}

	public Cancelation(Cancelation cancelation) {
		this.dateTime = cancelation.getDateTime();
		this.username = cancelation.getUsername();
	}
	
	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
