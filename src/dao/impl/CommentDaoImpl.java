package dao.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import dao.CommentDao;
import model.Comment;
import model.enums.CommentStatus;
import web.controller.Application;


public class CommentDaoImpl implements CommentDao{
	
	private final Map<Integer, Comment> comments = new HashMap<>();
	
	public CommentDaoImpl() {
		load("./data/comment.json");
	}
	
	@Override
	public List<Comment> findAll() {
		return comments.values().stream().collect(Collectors.toList());
	}
	
	@Override
	public Comment findOne(Integer id) {
		return comments.getOrDefault(id, null);
	}
	
	@Override
	public void load(String contextPath) {
		Type gsonType = new TypeToken<HashMap<Integer, Comment>>(){}.getType();
		try {
			setComments(Application.g.fromJson(new FileReader(contextPath), gsonType));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean add(Comment comment) {
		if(comment.getId() == -1)
			comment.setId(findAll().size());
		comments.put(comment.getId(), comment);
		saveChanges();
		return true;
	}
	
	@Override
	public void saveChanges() {
		Type gsonType = new TypeToken<HashMap<Integer, Comment>>(){}.getType();
		try {
			String jsonOut = Application.g.toJson(comments, gsonType);
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/comment.json"));
			writer.write(jsonOut);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		};
	}
	
	private void setComments(Map<Integer, Comment> comment1) {
		for(Map.Entry<Integer, Comment> entry: comment1.entrySet()) {
			this.comments.put(entry.getKey(), new Comment(entry.getValue()));
		}
	}

	@Override
	public List<Comment> findAccepted(Integer manifestationId) {
		return comments.values().stream().filter(t -> (t.getStatus().equals(CommentStatus.ACCEPTED) && t.getManifestation() == manifestationId)).collect(Collectors.toList());
	}

	@Override
	public List<Comment> findAllForManifestation(Integer manifestationId) {
		return comments.values().stream().filter(t -> t.getManifestation() == manifestationId).collect(Collectors.toList());
	}
}
