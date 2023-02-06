package web.dto;

public class ShoppingCartItemDTO {
	
	private Integer manifestationID;
	private Integer count;
	private String ticketType;
	
	public ShoppingCartItemDTO() {
		
	}

	public ShoppingCartItemDTO(Integer manifestationID, Integer count, String ticketType) {
		super();
		this.manifestationID = manifestationID;
		this.count = count;
		this.ticketType = ticketType;
	}

	public Integer getManifestationID() {
		return manifestationID;
	}

	public void setManifestationID(Integer manifestationID) {
		this.manifestationID = manifestationID;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	
	
}
