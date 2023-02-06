package model.enums;

public enum CustomerTypePoints {
	BRONZE(1500.0),
	SILVER(3000.0),
	GOLD(4500.0);
	
	private double value;
	private static CustomerTypePoints vals[] = CustomerTypePoints.values();
	
	CustomerTypePoints(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public static double getValue(CustomerTypeName ct) {
		return vals[ct.ordinal()].value;
	}
	
	public static double getNextValue(CustomerTypeName ct) {
		return vals[ct.ordinal()+1].value;
	}	
	
	public static CustomerTypeName getCustomerTypeNameFromPoints(double points) {
		if (points < CustomerTypePoints.SILVER.value)
			return CustomerTypeName.BRONZE;
		else if (points < CustomerTypePoints.GOLD.value)
			return CustomerTypeName.SILVER;
		else
			return CustomerTypeName.GOLD;
	}
}
