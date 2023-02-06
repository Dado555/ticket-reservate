package web.dto;

import java.util.List;

import model.ShoppingCartItem;

public class ShoppingCartDTO {
	
	private List<ShoppingCartItem> items;
	private Double discount;
	private Double total;
	
	public ShoppingCartDTO() {
		
	}
	
	public ShoppingCartDTO(List<ShoppingCartItem> items, Double discount) {
		this.items = items;
		this.discount = discount;
		this.calculateTotal();
	}
	
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public void calculateTotal() {
		total = 0.0;
		for(ShoppingCartItem item: items)
			total += item.getTotalPrice();
	}
	
	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getTotal() {
		return total;
	}
}
