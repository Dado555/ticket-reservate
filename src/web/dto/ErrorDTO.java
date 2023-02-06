package web.dto;

public class ErrorDTO {
	
	private String message;
	private String type;
	
	// AuthException: 100 
	// ExistentException: 200
	// 
	private Integer error;
	
	
	public ErrorDTO() {
		
	}

	public ErrorDTO(String message, String type, Integer error) {
		super();
		this.message = message;
		this.type = type;
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getError() {
		return error;
	}

	public void setError(Integer error) {
		this.error = error;
	}

}
