package model;

import model.enums.CustomerPointsDiscount;
import model.enums.CustomerTypeName;
import model.enums.CustomerTypePoints;


public class CustomerType {

	private CustomerTypeName name;
	private Double discount;
	private Double requiredPoints;
	
	public CustomerType() {
		name = CustomerTypeName.REGULAR;
		requiredPoints = CustomerTypePoints.getValue(CustomerTypeName.BRONZE); // do BRONZE
		discount = CustomerPointsDiscount.getValue(CustomerTypeName.REGULAR); // 0 - 1 vrijednosti, REGULAR korisnik
	}
	
	public CustomerType(CustomerTypeName name, Double discount, Double points) {
		this.name = name;
		this.discount = discount;
		this.requiredPoints = points;
	}
	
	public CustomerType(CustomerType ct) {
		this();
		if(ct != null) {
			this.name = ct.getName();
			this.discount = ct.getDiscount();
			this.requiredPoints = ct.getRequiredPoints();
		}
	}

	public CustomerTypeName getName() {
		return name;
	}

	public void setName(CustomerTypeName name) {
		this.name = name;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getRequiredPoints() {
		return requiredPoints;
	}

	public void setRequiredPoints(Double requiredPoints) {
		this.requiredPoints = requiredPoints;
	}
	
	@Override
	public String toString() {
		return "CustomerType [name=" + name + ", discount=" + discount + ", requiredPoints=" + requiredPoints + "]";
	}
}
