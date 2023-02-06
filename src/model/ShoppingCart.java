package model;

import java.util.ArrayList;
import java.util.List;


public class ShoppingCart {
	
	private Integer id;
	private String username;
	private List<ShoppingCartItem> items;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ShoppingCart(String username) {
		this.id = 0; // TODO: generisanje id
		this.username = username;
		this.items = new ArrayList<>();
	}

	public ShoppingCart(ShoppingCart cart) {
		this.id = cart.id;
		this.username = cart.username;
		this.items = cart.items;
	}

	public Double getTotal() {
		double retVal = 0;
		for(ShoppingCartItem item: items)
			retVal += item.getTotalPrice();
		return retVal;
	}
	
	public void addItem(ShoppingCartItem item) {
		this.items.add(item);
	}
	
	public Integer getCartID() {
		return id;
	}

	public void setCartID(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<ShoppingCartItem> getItems() {
		return items;
	}

	public void setItems(List<ShoppingCartItem> items) {
		this.items = items;
	}
	
}
