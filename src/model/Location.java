package model;

public class Location {

	private Double longitude; // duzina
	private Double latitude; // sirina;
	private String address;
	
	public Location() {
		
	}
	
	public Location(Double longitude, Double latitude, String address) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
	}
	
	public Location(Location loc) {
		this.longitude = loc.getLongitude();
		this.latitude = loc.getLatitude();
		this.address = loc.getAddress();
	}
	
	public boolean equals(Location loc) {
		return Double.compare(this.longitude, loc.longitude) == 0 &&
				Double.compare(this.latitude, loc.latitude) == 0;
	}
	
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
		// regex = ^(\+|-)?(?:180(?:(?:\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\.[0-9]{1,6})?))$
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
		// regex = ^(\+|-)?(?:90(?:(?:\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\.[0-9]{1,6})?))$
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
		// regex = ^(\w+\s?){1,5}\s*\d+\s*([\s'-]+[A-Za-z]+){1,2}(?=\s+\d{5})\s+\d{5}\s{0,1}$
	}

	public Double getDistance(Location loc) {
		return getDistance(this.latitude, this.longitude, loc.latitude, loc.longitude);
	}
	
	private Double getDistance(Double fromLat, Double fromLon, Double toLat, Double toLon){
       Double radius = 6371.0;   // Earth radius in km
       Double deltaLat = Math.toRadians(toLat - fromLat);
       Double deltaLon = Math.toRadians(toLon - fromLon);
       Double lat1 = Math.toRadians(fromLat);
       Double lat2 = Math.toRadians(toLat);
       Double aVal = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
       Math.sin(deltaLon/2) * Math.sin(deltaLon/2) * Math.cos(lat1) * Math.cos(lat2);
       Double cVal = 2*Math.atan2(Math.sqrt(aVal), Math.sqrt(1-aVal));  
	   Double distance = radius*cVal;
       return distance;
	 }
	
	public Double distance(Double lat2, Double lon2) {
	    final int R = 6371; // Radius of the earth

	    Double latDistance = Math.toRadians(lat2 - this.latitude);
	    Double lonDistance = Math.toRadians(lon2 - this.longitude);
	    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    Double distance = R * c * 1000; // convert to meters

	    return distance;
	}
}
