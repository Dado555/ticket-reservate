package web.dto;

public class CommentDTO {
	private Integer manifestationID;
	private String username;
	private String text;
	private Integer rating;
	
	public CommentDTO() {
		super();
	}

	public CommentDTO(Integer manifestationID, String username, String text, Integer rating) {
		super();
		this.manifestationID = manifestationID;
		this.username = username;
		this.text = text;
		this.rating = rating;
	}

	public Integer getManifestationID() {
		return manifestationID;
	}

	public void setManifestationID(Integer manifestationID) {
		this.manifestationID = manifestationID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}
}
