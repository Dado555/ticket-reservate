package model.enums;

public enum CustomerPointsDiscount {
	REGULAR(0.00),
	BRONZE(0.03),
	SILVER(0.06),
	GOLD(0.09);
	
	private double value;
	private static CustomerPointsDiscount vals[] = CustomerPointsDiscount.values();
	
	CustomerPointsDiscount(double i) {
		this.value = i;
	}
	
	public static double getValue(CustomerTypeName ct) {
		return vals[ct.ordinal()].value;
	}
}
