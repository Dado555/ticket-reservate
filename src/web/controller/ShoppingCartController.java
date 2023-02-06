package web.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import dao.impl.CustomerDaoImpl;
import dao.impl.ManifestationDaoImpl;
import dao.impl.ShoppingCartDaoImpl;
import dao.impl.TicketDaoImpl;
import model.Customer;
import model.Manifestation;
import model.ShoppingCart;
import model.ShoppingCartItem;
import model.Ticket;
import model.User;
import model.enums.ManifastationStatus;
import model.enums.TicketType;
import spark.Route;
import web.dto.ErrorDTO;
import web.dto.ShoppingCartDTO;
import web.dto.ShoppingCartItemDTO;

public class ShoppingCartController {
	
	
	public static final CustomerDaoImpl customerDao = Application.customerDao;
	public static final ManifestationDaoImpl manifestationDao = Application.manifestationDao;
	public static final TicketDaoImpl ticketDao = Application.ticketDao;
	public static final ShoppingCartDaoImpl cartDao = Application.cartDao;

	public static Gson g = Application.g;
	
	
	public static Route getCart = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));		
		if (current == null)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		Customer customer = customerDao.findOne(current.getUsername());
		if (customer == null)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		ShoppingCart cart = customer.getCartOrCreateIfNull();				
		return g.toJson(new ShoppingCartDTO(cart.getItems(), customer.getCustomerType().getDiscount() * cart.getTotal()));
	};
	
	public static Route addItemToCart = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		Customer customer = customerDao.findOne(current.getUsername());
		if (customer == null)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		String payload = request.body();
		ShoppingCartItemDTO item = g.fromJson(payload, ShoppingCartItemDTO.class);	
		
		Manifestation manifestation = manifestationDao.findOne(item.getManifestationID());
		if (manifestation == null)
			return g.toJson(new ErrorDTO("Manifestation does not exist!", "ExistentException", 200));
		if (manifestation.getStatus().equals(ManifastationStatus.INACTIVE))
			return g.toJson(new ErrorDTO("Manifestation is not active anymore!", "ExistentException", 200));
		if (manifestation.getSeats() < item.getCount())
			return g.toJson(new ErrorDTO("No more tickets!", "ExistentException", 200));

		List<Integer> ticketIDs = manifestationDao.getTicketIDsForManifestation(manifestation.getId());
		List<Ticket> tickets = ticketDao.findAll().stream()
								 		   		  .filter(t -> ticketIDs.contains(t.getId()) && t.getType().equals(TicketType.valueOf(item.getTicketType())))
								 		   		  .collect(Collectors.toList());

		if (tickets.size() < item.getCount())
			return g.toJson(new ErrorDTO("Not enough tickets to make desired reservation!", "ExistentException", 200));
		
		tickets = tickets.stream().limit(item.getCount()).collect(Collectors.toList());
		customer.addToCustomerCart(new ShoppingCartItem(tickets));	
		ShoppingCartDTO cartDTO = new ShoppingCartDTO(customer.getCart().getItems(), customer.getDiscount() * customer.getCart().getTotal());
		return g.toJson(cartDTO);
	};		
	
	public static Route submitCart = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		Customer customer = customerDao.findOne(current.getUsername());
		if (customer == null)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
				
		ShoppingCart cart = customer.getCartOrCreateIfNull();
		for (ShoppingCartItem item : cart.getItems()) {
			for (Ticket ticket : item.getTickets()) {
				Manifestation manifestation = manifestationDao.findOne(ticket.getManifestationID());
				ticket.reserve(customer, manifestation);
				manifestation.getTicketsCount().put(ticket.getType(), manifestation.getTicketsCount().get(ticket.getType()) - 1);
				manifestation.setSeats(manifestation.getSeats()-1);
				ticketDao.add(ticket);
				manifestationDao.add(manifestationDao.findOne(ticket.getManifestationID()));
			}
		}
		
		ticketDao.saveChanges();
		customerDao.saveChanges();
		manifestationDao.saveChanges();
		
		Double points = cart.getItems()
						 	.stream()
						 	.mapToDouble(item -> (item.getTotalPrice() / 1000 * 133)).sum();

		customer.updateBasedOnNewPoints(customer.getPoints() + points);
		
		System.out.println("---------------------------------------------------------------------");
		System.out.println(customer.getPoints());
		System.out.println(customer.getCustomerType().getName());
		System.out.println(customer.getCustomerType().getRequiredPoints());
		System.out.println(customer.getCustomerType().getDiscount());
		System.out.println("---------------------------------------------------------------------");
		
		customer.resetCart();
		customerDao.add(customer);
		customerDao.saveChanges();
		return g.toJson(cart);
	};	
}
