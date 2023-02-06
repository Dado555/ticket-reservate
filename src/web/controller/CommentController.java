package web.controller;

import java.util.List;

import com.google.gson.Gson;

import dao.CommentDao;
import dao.impl.AdministratorDaoImpl;
import dao.impl.SalesmanDaoImpl;
import model.Comment;
import model.enums.CommentStatus;
import spark.Route;
import web.dto.CommentDTO;

public class CommentController {
	public static final AdministratorDaoImpl administratorDao = Application.administratorDao;
	public static final SalesmanDaoImpl salesmanDao = Application.salesmanDao;	
	public static final CommentDao commentDao = Application.commentDao;
	
	public static Gson g = Application.g;
	public static Gson gsonDate = Application.gsonDate;
	
	public static Route getCommentsAdmin = (request, response) -> {
		Integer manifestationId = Integer.parseInt(request.params("manifestationId"));
		List<Comment> comments = commentDao.findAllForManifestation(manifestationId);
		response.type("application/json");
		return g.toJson(comments);
	};
	
	public static Route getCommentsCustomer = (request, response) -> {
		Integer manifestationId = Integer.parseInt(request.params("manifestationId"));
		List<Comment> comments = commentDao.findAccepted(manifestationId);
		response.type("application/json");
		return g.toJson(comments);
	};
	
	public static Route getCommentsSalesman = (request, response) -> {
		Integer manifestationId = Integer.parseInt(request.params("manifestationId"));
		List<Comment> comments = commentDao.findAllForManifestation(manifestationId);
		response.type("application/json");
		return g.toJson(comments);
	};
	
	public static Route addCommentCustomer = (request, response) -> {
		String payload = request.body();
		CommentDTO cdto = g.fromJson(payload, CommentDTO.class);
		
		Boolean ret = commentDao.add(new Comment(cdto));
		response.type("application/json");
		return g.toJson(ret);
	};
	
	public static Route changeCommentStatus = (request, response) -> {	
		Integer commentId = Integer.parseInt(request.queryParams("commentid"));
		String status = request.queryParams("status");
		Comment c = commentDao.findOne(commentId);
		if(status.equals("approve")) {
			c.setStatus(CommentStatus.ACCEPTED);
		} else {
			c.setStatus(CommentStatus.REJECTED);
		}
		commentDao.add(c);
		response.type("application/json");
		return g.toJson(true);
	};
}
