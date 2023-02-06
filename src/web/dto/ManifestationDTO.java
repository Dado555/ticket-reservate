package web.dto;

import java.util.Collection;
import java.util.Date;

import model.Location;
import model.enums.ManifastationStatus;

public class ManifestationDTO {
	
	private String title;
	private String type;
	private Integer seats;
	private Date dateTime;
	double priceRegular;
	private ManifastationStatus status;
	private Location location;
	Collection<Integer> ticketsID;
	private String posterPath;

	public ManifestationDTO() {
		
	}

	public ManifestationDTO(String title, String type, Integer seats, Date dateTime, Double priceRegular,
			ManifastationStatus status, Location location, Collection<Integer> ticketsID, String posterPath) {
		super();
		this.title = title;
		this.type = type;
		this.seats = seats;
		this.dateTime = dateTime;
		this.priceRegular = priceRegular;
		this.status = status;
		this.location = location;
		this.ticketsID = ticketsID;
		this.posterPath = posterPath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getSeats() {
		return seats;
	}

	public void setSeats(Integer seats) {
		this.seats = seats;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Double getPriceRegular() {
		return priceRegular;
	}

	public void setPriceRegular(Double priceRegular) {
		this.priceRegular = priceRegular;
	}

	public ManifastationStatus getStatus() {
		return status;
	}

	public void setStatus(ManifastationStatus status) {
		this.status = status;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Collection<Integer> getTicketsID() {
		return ticketsID;
	}

	public void setTicketsID(Collection<Integer> ticketsID) {
		this.ticketsID = ticketsID;
	}

	public String getPosterPath() {
		return posterPath;
	}

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}

}
