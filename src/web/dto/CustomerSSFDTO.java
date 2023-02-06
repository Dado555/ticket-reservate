package web.dto;

public class CustomerSSFDTO {
	String username;
	String firstName;
	String lastName;
	String sortType;
	String filterType;
	private Boolean ascendingSort;

	public CustomerSSFDTO() {
		super();
	}

	public CustomerSSFDTO(String username, String firstName, String lastName, String sortType, String filterType) {
		super();
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.sortType = sortType;
		this.filterType = filterType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	
	public Boolean getAscendingSort() {
		return ascendingSort;
	}

	public void setAscendingSort(Boolean ascendingSort) {
		this.ascendingSort = ascendingSort;
	}
}
