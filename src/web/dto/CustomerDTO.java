package web.dto;

import java.util.Date;
import java.util.List;

import model.CustomerType;
import model.ShoppingCart;
import model.enums.Gender;
import model.enums.UserType;

public class CustomerDTO extends UserDTO {
	
	private List<Integer> ticketIDs;
	private CustomerType customerType;
	private Double points;
	private ShoppingCart cart;
	private Boolean deleted;
	private Boolean blocked;
	private Boolean suspicious;
	
	public CustomerDTO() {
		
	}

	public CustomerDTO(String username, String firstName, String lastName, Gender gender, Date birthDate, UserType userType, List<Integer> ticketIDs, CustomerType customerType, Double points, ShoppingCart cart,
			Boolean deleted, Boolean blocked, Boolean suspicious) {
		super(username, firstName, lastName, gender, birthDate, userType);
		this.ticketIDs = ticketIDs;
		this.customerType = customerType;
		this.points = points;
		this.cart = cart;
		this.deleted = deleted;
		this.blocked = blocked;
		this.suspicious = suspicious;
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
		this.customerType = customerType;
	}

	public Double getPoints() {
		return points;
	}

	public void setPoints(Double points) {
		this.points = points;
	}

	public ShoppingCart getCart() {
		return cart;
	}

	public void setCart(ShoppingCart cart) {
		this.cart = cart;
	}

	public Boolean getDeleted() {
		return deleted;
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

	public Boolean getSuspicious() {
		return suspicious;
	}

	public void setSuspicious(Boolean suspicious) {
		this.suspicious = suspicious;
	}
	
	
	
	
}
