package dao;

import java.util.List;

import model.Comment;


public interface CommentDao extends GenericDao<Integer, Comment>{
		
	public List<Comment> findAccepted(Integer manifestationId);
	
	boolean add(Comment comment);
		
	List<Comment> findAllForManifestation(Integer manifestationId);
}
