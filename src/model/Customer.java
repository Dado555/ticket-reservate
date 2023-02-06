package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.enums.CustomerPointsDiscount;
import model.enums.CustomerTypeName;
import model.enums.CustomerTypePoints;
import model.enums.Gender;

public class Customer extends User {
	
	private List<Integer> ticketIDs;
	private CustomerType customerType;
	private Double points;
	private ShoppingCart cart;
	private Boolean suspicious;
	private Boolean deleted;
	private Boolean blocked;

	public Customer() {
		super();
	}
	
	public Customer(String username, String password, String firstName, String lastName, Gender gender, Date birthDate, CustomerType customerType) {
		super(username, password, firstName, lastName, gender, birthDate);
		this.setCustomerType(customerType);
		this.points = 0.0;
		this.deleted = false;
		this.blocked = false;
		this.suspicious = false;
		this.ticketIDs = new ArrayList<>();
	}
	
	public Customer(String username, String password, String firstName, String lastName, Gender gender, Date birthDate) {
		super(username, password, firstName, lastName, gender, birthDate);
		this.customerType = new CustomerType();
		this.points = 0.0;
		this.suspicious = false;
		this.deleted = false;
		this.blocked = false;
		this.ticketIDs = new ArrayList<>();
	}
	
	public void editCustomer(User user) {
		this.setUsername(user.getUsername());
		this.setFirstName(user.getFirstName());
		this.setPassword(user.getPassword());
		this.setLastName(user.getLastName());
		this.setGender(user.getGender());
		this.setBirthDate(user.getBirthDate());
	}

	public Customer(Customer customer) {
		super(customer.getUsername(), customer.getPassword(),customer.getFirstName(), customer.getLastName(), customer.getGender(), customer.getBirthDate());
		this.customerType = new CustomerType(customer.getCustomerType());
		this.points = customer.getPoints();
		this.deleted = customer.isDeleted();
		this.blocked = customer.isBlocked();
		this.suspicious = customer.isSuspicious();
		this.ticketIDs = new ArrayList<Integer>(customer.getTicketIDs());
	}

	public void addTicket(Ticket ticket) {
		this.ticketIDs.add(ticket.getId());
		if (!this.getUsername().equals(ticket.getCustomerUsername()))
			ticket.setCustomerUserName(this);
	}
	
	public void updateBasedOnNewPoints(Double points) {
		this.points = points;
		int ordinal = this.getCustomerType().getName().ordinal();
		if (points > customerType.getRequiredPoints() && ordinal < CustomerTypeName.values().length-1) {
			CustomerTypeName name = CustomerTypePoints.getCustomerTypeNameFromPoints(points);
			this.customerType.setName(name);
			this.customerType.setDiscount(CustomerPointsDiscount.getValue(name));
		} 
		CustomerTypeName name = this.customerType.getName();
		Double requiredPoints =  (name == CustomerTypeName.GOLD) ? 0 : CustomerTypePoints.getValue(name) - this.points;
		this.customerType.setRequiredPoints(requiredPoints);
	}
	
	public void addToCustomerCart(ShoppingCartItem item) {
		if (cart == null)
			cart = new ShoppingCart(this.getUsername());	
		
		cart.addItem(item);
	}
	
	public Double getDiscount() {
		return customerType.getDiscount();
	}
	
	public List<Integer> getTicketIDs() {
		return ticketIDs;
	}

	public void setTicketIDs(List<Integer> ticketIDs) {
		this.ticketIDs = ticketIDs;
	}

	public CustomerType getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = (customerType != null) ? customerType : new CustomerType();
	}

	public Double getPoints() {
		return points;
	}

	public void setPoints(Double points) {
		this.points = points;
	}

	public Boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "Customer [ticketsID=" + ticketIDs + ", customerType=" + customerType + ", points=" + points
				+ ", deleted=" + deleted + ", getUsername()=" + getUsername() + ", getPassword()=" + getPassword()
				+ ", getFirstName()=" + getFirstName() + ", getLastName()=" + getLastName() + ", getGender()="
				+ getGender() + ", getBirthDate()=" + getBirthDate() + "]";
	}

	public ShoppingCart getCartOrCreateIfNull() {
		if (cart == null)
			cart = new ShoppingCart(this.getUsername());
		return cart;
	}
	
	public ShoppingCart getCart() {
		return cart;
	}
	
	public ShoppingCart resetCart()
	{
		cart.getItems().clear();
		return cart;
	}

	public void setCart(ShoppingCart cart) {
		this.cart = cart;
	}
	
	public Boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	public Boolean isSuspicious() {
		return suspicious;
	}

	public void setSuspicious(Boolean suspicious) {
		this.suspicious = suspicious;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public Boolean getBlocked() {
		return blocked;
	}
	
	public Boolean checkSuspicious(Cancelation cancelation) {
		Boolean isSuspicious = true;
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
	    cal.setTime(cancelation.getDateTime());
	    cal.add(Calendar.DATE, -30);
		if (currentDate.before(cal.getTime()))
			isSuspicious = false;	
		
		return isSuspicious;
	}
	
}
