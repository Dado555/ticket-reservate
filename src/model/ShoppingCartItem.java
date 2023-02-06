package model;

import java.util.List;

public class ShoppingCartItem {
	
	private List<Ticket> tickets;
	private Double price;
	
	public ShoppingCartItem() {
		
	}
	
	public ShoppingCartItem(List<Ticket> tickets) {
		super();
		this.tickets = tickets;
		this.price = getTotalPrice();
	}

	public Double getTotalPrice() {
		double ret = 0;
		for (Ticket ticket : tickets)
			ret += ticket.getPrice();
		return ret;
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}
	
	
}
