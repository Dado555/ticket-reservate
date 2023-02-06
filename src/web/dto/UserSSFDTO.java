package web.dto;

public class UserSSFDTO {
	private String firstName;
	private String lastName;
	private String username;
	private String sortType;
	private Boolean ascendingSort;
	private String filterType;
	
	public UserSSFDTO() {
		super();
	}

	public UserSSFDTO(String firstName, String lastName, String username, String sortType, String filterType) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.sortType = sortType;
		this.filterType = filterType;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public Boolean isAscendingSort() {
		return ascendingSort;
	}

	public void setAscendingSort(Boolean ascendingSort) {
		this.ascendingSort = ascendingSort;
	}
}
