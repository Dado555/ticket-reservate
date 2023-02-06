package web.controller;

import java.util.List;

import com.google.gson.Gson;

import dao.impl.AdministratorDaoImpl;
import dao.impl.CustomerDaoImpl;
import dao.impl.SalesmanDaoImpl;
import dao.impl.TicketDaoImpl;
import model.Administrator;
import model.Customer;
import model.Salesman;
import model.Ticket;
import model.User;
import spark.Route;
import support.UserToUserDTO;
import web.dto.ErrorDTO;


public class UserController {

	public static final AdministratorDaoImpl administratorDao =	Application.administratorDao;
	public static final SalesmanDaoImpl salesmanDao = Application.salesmanDao;
	public static final CustomerDaoImpl customerDao = Application.customerDao;
	public static final TicketDaoImpl ticketDao = Application.ticketDao;

	public static final UserToUserDTO userToUserDTO = new UserToUserDTO();
	
	public static final Gson g = Application.g;
	
	
	public static Route getUser = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("User is not logged in!", "AuthException", 100));

		String username = request.params("username");
		System.out.println(current.getUsername() + " --- " + username);
		if (!current.getUsername().equals(username))
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		User user = null;
		if (administratorDao.contains(username))
			user = (Administrator) administratorDao.findOne(username);
		else if (salesmanDao.contains(username))
			user = (Salesman) salesmanDao.findOne(username);
		else
			user = (Customer) customerDao.findOne(username);

		return g.toJson(userToUserDTO.convert(user));
	};
	
	public static Route getTicketsForUser = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));		
		if (current == null)
			return g.toJson(new ErrorDTO("User is not logged in!", "AuthException", 100));
		
		Customer customer = customerDao.findOne(current.getUsername());
		if (customer == null)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		String username = request.params("username");
		if (customer.getUsername() != username)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		List<Ticket> userTickets = ticketDao.getUserTickets(customer.getTicketIDs());
		return g.toJson(userTickets);
	};

	public static Route getReservedTicketsForUser = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));		
		if (current == null)
			return g.toJson(new ErrorDTO("User is not logged in!", "AuthException", 100));
		
		Customer customer = customerDao.findOne(current.getUsername());
		if (customer == null)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		String username = request.params("username");
		if (customer.getUsername() != username)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		List<Ticket> userTickets = ticketDao.getUserReservedTickets(customer.getTicketIDs());
		return g.toJson(userTickets);
	};
	
}
