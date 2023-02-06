package web.dto;

import java.util.Date;


public class ManifestationSSFDTO {
	private String title;
	private Date fromDate;
	private Date toDate;
	private Double fromPrice, toPrice;
	private String cityCountry;
	private String sortType;
	private String filterType;
	private Boolean availableTickets;
	private Double currentLongitude, currentLatitude;
	
	public ManifestationSSFDTO() {
		super();
	}

	public ManifestationSSFDTO(String title, Date fromDate, Date toDate, Double fromPrice, Double toPrice,
			String cityCountry, String sortType, String filterType) {
		super();
		this.title = title;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.fromPrice = fromPrice;
		this.toPrice = toPrice;
		this.cityCountry = cityCountry;
		this.sortType = sortType;
		this.filterType = filterType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Double getFromPrice() {
		return fromPrice;
	}

	public void setFromPrice(Double fromPrice) {
		this.fromPrice = fromPrice;
	}

	public Double getToPrice() {
		return toPrice;
	}

	public void setToPrice(Double toPrice) {
		this.toPrice = toPrice;
	}

	public String getCityCountry() {
		return cityCountry;
	}

	public void setCityCountry(String cityCountry) {
		this.cityCountry = cityCountry;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public Boolean isAvailableTickets() {
		return availableTickets;
	}

	public void setAvailableTickets(Boolean availableTickets) {
		this.availableTickets = availableTickets;
	}
	
	public Double getLongitude() {
		return currentLongitude;
	}

	public void setLongitude(Double longitude) {
		this.currentLongitude = longitude;
	}

	public Double getLatitude() {
		return currentLatitude;
	}

	public void setLatitude(Double latitude) {
		this.currentLatitude = latitude;
	}

	@Override
	public String toString() {
		return "ManifestationSSFDTO [title=" + title + ", fromDate=" + fromDate + ", toDate=" + toDate + ", fromPrice="
				+ fromPrice + ", toPrice=" + toPrice + ", cityCountry=" + cityCountry + ", sortType=" + sortType
				+ ", filterType=" + filterType + ", availableTickets="
				+ availableTickets + ", currentLongitude=" + currentLongitude + ", currentLatitude=" + currentLatitude
				+ "]";
	}
}
