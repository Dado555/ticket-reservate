package model;

import model.enums.TicketStatus;
import model.enums.TicketType;


public class Ticket {
	private Integer id; // TODO: 10 characters
	private Integer manifestationID;
	private Double price;
	private String customerUsername;
	private TicketStatus status;
	private TicketType type;
	private Boolean deleted;
	
	public Ticket() {
		
	}
	
	public Ticket(Integer id, Manifestation manifestation, TicketType type) {
		this.id = id;
		this.status = TicketStatus.UNRESERVED;
		this.type = type;
		this.setManifestationID(manifestation);
		this.setPrice(manifestation);		
		this.deleted = false;
		this.customerUsername = "";
		manifestation.addTicket(this);
	}

	public Ticket(Ticket ticket) {
		this.id = ticket.getId();
		this.manifestationID = ticket.getManifestationID();
		this.price = ticket.getPrice();
		this.status = ticket.getStatus();
		this.type = ticket.getType();
		this.customerUsername = ticket.getCustomerUsername();
		this.deleted = ticket.deleted;
	}

	public void reserve(Customer customer, Manifestation manifestation) {
		setCustomerUserName(customer);
		setManifestationID(manifestation);
		setPrice(manifestation);
		setStatus(TicketStatus.RESERVED);
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getManifestationID() {
		return manifestationID;
	}

	public void setManifestationID(Manifestation manifestation) {
		this.manifestationID = manifestation.getId();
		if (manifestation != null && !manifestation.getTicketIDs().contains(this.getId()))
			manifestation.getTicketIDs().add(this.getId());	
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Manifestation manifestation) {
		if (type == TicketType.REGULAR)
			price = manifestation.getPriceRegular();
		else if (type == TicketType.FAN_PIT)
			price = 2 * manifestation.getPriceRegular();
		else 
			price = 4 * manifestation.getPriceRegular();
	}

	public String getCustomerUsername() {
		return customerUsername;
	}

	public void setCustomerUserName(Customer customer) {
		this.customerUsername = customer.getUsername();
		if (customer != null && !customer.getTicketIDs().contains(this.id))
			customer.getTicketIDs().add(this.id);
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}

	public TicketType getType() {
		return type;
	}

	public void setType(TicketType type) {
		this.type = type;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public void setManifestationID(Integer manifestationID) {
		this.manifestationID = manifestationID;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setCustomerUsername(String customerUsername) {
		this.customerUsername = customerUsername;
	}
	
	
}
