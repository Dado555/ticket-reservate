package web.dto;

import model.enums.TicketType;

public class TicketDTO {
	
	private Integer manifestationID;
	private TicketType type;
	private Integer count;
	
	public TicketDTO() {
		
	}

	public TicketDTO(Integer manifestationID, TicketType type, Integer count) {
		super();
		this.manifestationID = manifestationID;
		this.type = type;
		this.count = count;
	}

	public Integer getManifestationID() {
		return manifestationID;
	}

	public void setManifestationID(Integer manifestationID) {
		this.manifestationID = manifestationID;
	}

	public TicketType getType() {
		return type;
	}

	public void setType(TicketType type) {
		this.type = type;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	

}
