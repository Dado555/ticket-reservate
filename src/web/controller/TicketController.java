package web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import dao.AdministratorDao;
import dao.CancelationDao;
import dao.CustomerDao;
import dao.ManifestationDao;
import dao.SalesmanDao;
import dao.TicketDao;
import dao.UserDao;
import model.Cancelation;
import model.Customer;
import model.Manifestation;
import model.Salesman;
import model.Ticket;
import model.User;
import model.enums.TicketStatus;
import spark.Route;
import web.dto.ErrorDTO;
import web.dto.TicketDTO;
import web.dto.TicketsSSFDTO;

public class TicketController {
	
	public static final UserDao userDao = Application.userDao;
	public static final AdministratorDao administratorDao = Application.administratorDao;
	public static final SalesmanDao salesmanDao = Application.salesmanDao;	
	public static final CustomerDao customerDao = Application.customerDao;	
	public static final TicketDao ticketDao = Application.ticketDao;
	public static final ManifestationDao manifestationDao = Application.manifestationDao;
	public static final CancelationDao cancelationDao = Application.cancelationDao;

	public static Gson g = Application.g;
	public static Gson gsonDate = Application.gsonDate;
	
	
	public static Route getAllTickets = (request, response) -> {
		List<Ticket> tickets = ticketDao.findAll();
		response.type("application/json");
		return g.toJson(tickets);
	};
		
	public static Route getOneTicket = (request, response) -> {
		response.type("application/json");

		Integer id = Integer.parseInt(request.params("id"));
		Ticket ticket = ticketDao.findOne(id);
		if (ticket == null)
			return g.toJson(new ErrorDTO("Ticket does not exist!", "ExistentException", 200));
		
		return g.toJson(ticket);
	};

	public static Route getTicketsForUser = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("User is not logged in!", "AuthException", 100));
		
		List<Ticket> tickets = new ArrayList<>();
		if (administratorDao.findOne(current.getUsername()) != null) 
			tickets = ticketDao.findAll();
		if (salesmanDao.findOne(current.getUsername()) != null) 
			tickets = ticketDao.getReservedTicketsForManifestationIDs(((Salesman) current).getManifestationIDs());
		if (customerDao.findOne(current.getUsername()) != null) {
			tickets = ticketDao.findAll().stream()
							   			 .filter(t -> (((Customer) current).getTicketIDs().contains(t.getId()) && t.getCustomerUsername().equals(((Customer) current).getUsername())))
							   			 .collect(Collectors.toList());
		}	
		return g.toJson(tickets);
	};
		
	public static Route createTicket = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));		
		if (current == null)
			return g.toJson(new ErrorDTO("You have no permission to create ticket!", "AuthException", 100));
		if (administratorDao.findOne(current.getUsername()) == null && salesmanDao.findOne(current.getUsername()) == null)
			return g.toJson(new ErrorDTO("You have no permission to create ticket!", "AuthException", 100));

		String payload = request.body();
		TicketDTO ticketDTO = g.fromJson(payload, TicketDTO.class);
		
		Manifestation manifestation = manifestationDao.findOne(ticketDTO.getManifestationID());
		if (manifestation == null)
			return g.toJson(new ErrorDTO("Manifestation does not exist!", "ExistentException", 200));
		
		int ticketId = ticketDao.findAll().size();
		for(int i = 0; i < ticketDTO.getCount(); i++) {
			Ticket ticket = new Ticket(ticketId++, manifestation, ticketDTO.getType());
			ticketDao.add(ticket);
			//manifestation.addTicket(ticket);
		}
		manifestationDao.add(manifestation);
		return g.toJson(true);
	};
	
	public static Route checkCanUserComment = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(false);
		
		Customer customer = customerDao.findOne(current.getUsername());
		if (customer == null)
			return g.toJson(false);
		
		List<Ticket> userTickets = ticketDao.findAll().stream()
	   			 									  .filter(t -> customer.getTicketIDs().contains(t.getId()))
	   			 									  .collect(Collectors.toList());
		System.out.println(userTickets.size());
		if(userTickets != null) {
			Integer manifestationId = Integer.parseInt(request.params("manifestationId"));
			for(Ticket t: userTickets) {
				if(t.getManifestationID().equals(manifestationId) && t.getStatus().equals(TicketStatus.RESERVED) 
						&& manifestationDao.findOne(manifestationId).getDateTime().before(new Date()))
					return g.toJson(true);
			}
		}
		return g.toJson(false);
	};

	public static Route getTicketsForSalesman = (request, response) -> {
		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		
		if (current == null)
			return null;
		
		Salesman salesman = salesmanDao.findOne(current.getUsername());
		if(salesman == null)
			return null;
		
		List<Ticket> tickets = ticketDao.getUserTickets(salesman.getManifestationIDs());
		response.type("application/json");
		return g.toJson(tickets);
	};
	
	public static Route deleteTicket = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));		
		if (current == null)
			return g.toJson(new ErrorDTO("You have no permission to delete ticket!", "AuthException", 100));
		if (administratorDao.findOne(current.getUsername()) == null && salesmanDao.findOne(current.getUsername()) == null)
			return g.toJson(new ErrorDTO("You have no permission to delete ticket!", "AuthException", 100));

		Integer id = Integer.parseInt(request.params("id"));
		Ticket ticket = ticketDao.findOne(id);
		if (ticket == null)
			return g.toJson(new ErrorDTO("Ticket does not exist!", "ExistentException", 200));
		
		ticket.setDeleted(true);
		return g.toJson(ticket);
	};

	public static Route handleSearchSortFilter = (request, response) -> {
		String payload = request.body();
		System.out.println(payload);
		TicketsSSFDTO ticket = gsonDate.fromJson(payload, TicketsSSFDTO.class);
		System.out.println(ticket);
		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null) {
			return g.toJson(null);
		}
		Integer userType = -1;
		String username = current.getUsername();
		if (current instanceof Salesman) {
			userType = 0;
		} else if (current instanceof Customer) {
			userType = 1;
		}
		List<Ticket> results = ticketDao.ssfTickets(ticket, userType, username);
		response.type("application/json");
		return g.toJson(results);
	};
	
	public static Route cancelTicket = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));		
		if (current == null)
			return g.toJson(new ErrorDTO("You have no permission to cancel ticket!", "AuthException", 100));
		
		Customer customer = customerDao.findOne(current.getUsername());
		if (customer == null)
			return g.toJson(new ErrorDTO("You have no permission to cancel ticket!", "AuthException", 100));
				
		int id = Integer.parseInt(request.params("id"));
		Ticket ticket = ticketDao.findOne(id);
		if (ticket == null)
			return g.toJson(new ErrorDTO("Ticket does not exist!", "ExistentException", 200));
		
		Manifestation manifestation = manifestationDao.findOne(ticket.getManifestationID());
		
		boolean isCancelable = true;
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
	    cal.setTime(manifestation.getDateTime());
	    cal.add(Calendar.DATE, -7);
		if (currentDate.before(cal.getTime()))
			isCancelable = false;			
		
		if (!isCancelable)
			return g.toJson(new ErrorDTO("Ticket is not cancelable!", "ExistentException", 200));
		
		Cancelation cancelation = new Cancelation(new Date(), customer.getUsername());
		cancelationDao.add(cancelation);
		cancelationDao.saveChanges();

		List<Cancelation> cancelations = cancelationDao.findAll()
													   .stream()
													   .filter(c -> c.getUsername().equals(customer.getUsername()) && customer.checkSuspicious(c))
													   .collect(Collectors.toList());
		if (cancelations.size() > 5) {
			customer.setSuspicious(true);
			customerDao.add(customer);
			customerDao.saveChanges();
		}		
		
		Double lostPoints = ticket.getPrice() / 1000 * 133 * 4;
		Double points = (customer.getPoints() - lostPoints);
		if (points < 0)
			points = 0.0;
		
		customer.updateBasedOnNewPoints(points);
		ticket.setStatus(TicketStatus.CANCELED);
		ticketDao.add(ticket);
		customerDao.add(customer);
		ticketDao.saveChanges();
		customerDao.saveChanges();
		return g.toJson(ticket);
	};
	
	
}
