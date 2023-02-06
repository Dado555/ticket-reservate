package web.dto;

import java.util.Date;

public class TicketsSSFDTO {
	private String manifestationTitle;
	private Double priceFrom, priceTo;
	private Date dateFrom, dateTo;
	private String sortType;
	private String filterType;
	private String filterStatus;

	public TicketsSSFDTO() {
		super();
	}
	
	public TicketsSSFDTO(String manifestationTitle, Double priceFrom, Double priceTo, Date dateFrom, Date dateTo,
			String sortType, String filterType, String filterStatus) {
		super();
		this.manifestationTitle = manifestationTitle;
		this.priceFrom = priceFrom;
		this.priceTo = priceTo;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.sortType = sortType;
		this.filterType = filterType;
		this.filterStatus = filterStatus;
	}
	
	public String getManifestationTitle() {
		return manifestationTitle;
	}
	
	public void setManifestationTitle(String manifestationTitle) {
		this.manifestationTitle = manifestationTitle;
	}
	
	public Double getPriceFrom() {
		return priceFrom;
	}
	
	public void setPriceFrom(Double priceFrom) {
		this.priceFrom = priceFrom;
	}
	
	public Double getPriceTo() {
		return priceTo;
	}
	
	public void setPriceTo(Double priceTo) {
		this.priceTo = priceTo;
	}
	
	public Date getDateFrom() {
		return dateFrom;
	}
	
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	
	public Date getDateTo() {
		return dateTo;
	}
	
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
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

	public String getFilterStatus() {
		return filterStatus;
	}

	public void setFilterStatus(String filterStatus) {
		this.filterStatus = filterStatus;
	}
	
}
