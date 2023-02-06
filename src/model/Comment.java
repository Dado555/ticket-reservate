package model;
import model.enums.CommentStatus;
import web.dto.CommentDTO;


public class Comment {
	
	private Integer id;
	private Integer manifestationID;
	private String username;
	private String text;
	private Integer rating;	// TODO: 1 to 5
	private CommentStatus status;
	private Boolean deleted;
	
	public Comment() {
		
	}

	public Comment(Integer manifestation, String customer, String text, Integer rating) {
		this.manifestationID = manifestation;
		this.username = customer;
		this.text = text;
		this.rating = rating;
		this.setStatus(CommentStatus.INITIAL);
		this.deleted = false;
	}

	public Comment(Comment comment) {
		this.id = comment.id;
		this.manifestationID = comment.manifestationID;
		this.username = comment.username;
		this.text = comment.text;
		this.rating = comment.rating;
		this.status = comment.status;
		this.deleted = comment.deleted;
	}
	
	public Comment(CommentDTO cdto) {
		this.id = -1;
		this.manifestationID = cdto.getManifestationID();
		this.username = cdto.getUsername();
		this.text = cdto.getText();
		this.rating = cdto.getRating();
		this.status = CommentStatus.INITIAL;
		this.deleted = false;
	}

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getManifestation() {
		return manifestationID;
	}

	public void setManifestation(Integer manifestation) {
		this.manifestationID = manifestation;
	}

	public String getCustomer() {
		return username;
	}

	public void setCustomer(String customer) {
		this.username = customer;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public CommentStatus getStatus() {
		return status;
	}

	public void setStatus(CommentStatus status) {
		this.status = status;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
}
