package dao;

import java.util.Date;
import java.util.List;

import model.Ticket;
import web.dto.TicketsSSFDTO;


public interface TicketDao extends GenericDao<Integer, Ticket>{
	
	List<Ticket> findAllExcludingDeleted();
	
	List<Ticket> getReservedTicketsForManifestationIDs(List<Integer> manifestationIDs);
	
	boolean add(Ticket ticket);
		
	List<Ticket> getReservedTickets();
	
	List<Ticket> getUserTickets(List<Integer> ticketIDs);
	
	List<Ticket> getUserReservedTickets(List<Integer> ticketIDs);
	
	List<Ticket> ssfTickets(TicketsSSFDTO requirements, Integer userType, String username);
	
	void sortBy(List<Ticket> ticketsList, String sortType);

	List<Ticket> filterByType(List<Ticket> ticketsList, String filterType);
	
	List<Ticket> filterByStatus(List<Ticket> ticketsList, String filterStatus);
	
	List<Ticket> byManifestation(String manifestationTitle, List<Ticket> ticketsList);
	
	List<Ticket> byFromDate(Date fromDate, List<Ticket> ticketsList);

	List<Ticket> byToDate(Date toDate, List<Ticket> ticketsList);
	
	List<Ticket> byFromPrice(Double fromPrice, List<Ticket> ticketsList);
	
	List<Ticket> byToPrice(Double toPrice, List<Ticket> ticketsList);
}
