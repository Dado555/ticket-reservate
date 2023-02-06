package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.enums.ManifastationStatus;
import model.enums.TicketType;

public class Manifestation {
	
	private Integer id;
	private String title;
	private String type;
	private Integer seats;
	private Date dateTime;
	private Double priceRegular;
	private ManifastationStatus status;
	private Location location;
	private List<Integer> ticketIDs;
	private Map<TicketType, Integer> ticketsCount;
	private Boolean deleted;
	private String posterPath;

	public Manifestation() {
		
	}
	
	public Manifestation(Integer id, String title, String type, Integer seats, Date dateTime, Double price, Location location, String posterPath) {
		this.id = id;
		this.title = title;
		this.type = type;
		this.seats = seats;
		this.dateTime = dateTime;
		this.priceRegular = price;
		this.status = ManifastationStatus.ACTIVE;
		this.location = location;
		this.deleted = false;
		this.setPosterPath(posterPath);

		this.ticketIDs = new ArrayList<Integer>();
		this.setInitialTicketsCount();
	}
	
	public Manifestation(Manifestation manifestation) {
		this.id = manifestation.getId();
		this.title = manifestation.getTitle();
		this.type = manifestation.getType();
		this.seats = manifestation.getSeats();
		this.dateTime = manifestation.dateTime;
		this.priceRegular = manifestation.getPriceRegular();
		this.status = manifestation.getStatus();
		this.location = new Location(manifestation.getLocation());
		this.ticketIDs = new ArrayList<Integer>(manifestation.getTicketIDs());
		this.deleted = manifestation.isDeleted();
		this.posterPath = manifestation.getPosterPath();
		this.ticketsCount = manifestation.getTicketsCount();
	}
	
	public void addTicket(Ticket ticket) {
		//this.ticketIDs.add(ticket.getId());
		if (this.getId() == ticket.getManifestationID())
			ticket.setManifestationID(this);
		if(ticketsCount.get(ticket.getType()) != null)
			this.ticketsCount.put(ticket.getType(), ticketsCount.get(ticket.getType()) + 1);
		else
			this.ticketsCount.put(ticket.getType(), 1);
		this.seats += 1;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public Boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Collection<Integer> getTicketIDs() {
		return ticketIDs;
	}

	public void setTickets(List<Integer> tickets) {
		this.ticketIDs = tickets;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getPosterPath() {
		return posterPath;
	}

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Manifestation [id=" + id + ", title=" + title + ", type=" + type + ", seats=" + seats + ", dateTime="
				+ dateTime + ", priceRegular=" + priceRegular + ", status=" + status + ", location=" + location
				+ ", ticketIDs=" + ticketIDs + ", deleted=" + deleted + ", posterPath=" + posterPath + "]";
	}

	public Map<TicketType, Integer> getTicketsCount() {
		return ticketsCount;
	}

	public void setTicketsCount(Map<TicketType, Integer> ticketsCount) {
		this.ticketsCount = ticketsCount;
	}
	
	public void setInitialTicketsCount() {
		this.ticketsCount = new HashMap<>();
		this.ticketsCount.put(TicketType.REGULAR, 0);
		this.ticketsCount.put(TicketType.VIP, 0);
		this.ticketsCount.put(TicketType.FAN_PIT, 0);
	}
	
	/*
	 	LocalDateTime now = LocalDateTime.of(2020, 2, 1, 15, 0);  
	    System.out.println("Before Formatting: " + now);  
	    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");  
	    String formatDateTime = now.format(format);  
	 */
}
