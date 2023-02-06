package dao.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import dao.TicketDao;
import model.Ticket;
import model.enums.TicketStatus;
import web.controller.Application;
import web.dto.TicketsSSFDTO;


public class TicketDaoImpl implements TicketDao {
	
	private final Map<Integer, Ticket> tickets = new HashMap<>();
	private ManifestationDaoImpl manifestationDao = Application.manifestationDao;
	private SalesmanDaoImpl salesmanDao = Application.salesmanDao;
	
	public TicketDaoImpl() {
		load("./data/ticket.json");
	}
	
	@Override
	public List<Ticket> findAll() {
		return tickets.values().stream().collect(Collectors.toList());
	}

	@Override
	public Ticket findOne(Integer id) {
		return tickets.getOrDefault(id, null);
	}
	
	@Override
	public List<Ticket> getReservedTickets() {
		return tickets.values()
					  .stream()
					  .filter(t -> t.getStatus().equals(TicketStatus.RESERVED))
					  .collect(Collectors.toList());
	}

	@Override
	public List<Ticket> getUserTickets(List<Integer> ticketIDs) {
		return tickets.values()
				  .stream()
				  .filter(t -> ticketIDs.contains(t.getId()))
				  .collect(Collectors.toList());
	}

	@Override
	public List<Ticket> getUserReservedTickets(List<Integer> ticketIDs) {
		return tickets.values()
		  	   		  .stream()
		  	   		  .filter(t -> ticketIDs.contains(t.getId()) && t.getStatus().equals(TicketStatus.RESERVED))
		  	   		  .collect(Collectors.toList());
	}

	@Override
	public boolean add(Ticket ticket) {
		tickets.put(ticket.getId(), ticket);
		saveChanges();
		return true;
	}

	@Override
	public void load(String contextPath) {
		Type gsonType = new TypeToken<HashMap<Integer, Ticket>>(){}.getType();
		try {
			setTickets(Application.g.fromJson(new FileReader(contextPath), gsonType));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveChanges() {
		Type gsonType = new TypeToken<HashMap<Integer, Ticket>>(){}.getType();
		try {
			String jsonOut = Application.g.toJson(tickets, gsonType);
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/ticket.json"));
			writer.write(jsonOut);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		};
	}
	
	private void setTickets(Map<Integer, Ticket> tickets1) {
		for(Map.Entry<Integer, Ticket> entry: tickets1.entrySet()) {
			this.tickets.put(entry.getKey(), new Ticket(entry.getValue()));
		}
	}

	@Override
	public List<Ticket> findAllExcludingDeleted() {
		return tickets.values().stream().filter(t -> !t.getDeleted()).collect(Collectors.toList());
	}

	@Override
	public List<Ticket> ssfTickets(TicketsSSFDTO requirements, Integer userType, String username) { // 0- prodavac  1- kupac
		List<Ticket> ticketsList = findAll();
		if(userType == 0) {
			List<Integer> manifestationIds = salesmanDao.findOne(username).getManifestationIDs();
			ticketsList = ticketsList.stream().filter(t -> manifestationIds.contains(t.getManifestationID())).collect(Collectors.toList());
		}
		else if(userType == 1) {
			ticketsList = ticketsList.stream().filter(t -> t.getCustomerUsername().equals(username)).collect(Collectors.toList());
		}
		if(requirements.getManifestationTitle() != null)
			ticketsList = byManifestation(requirements.getManifestationTitle(), ticketsList);
		if(requirements.getPriceFrom() != null)
			ticketsList = byFromPrice(requirements.getPriceFrom(), ticketsList);
		if(requirements.getPriceTo() != null)
			ticketsList = byToPrice(requirements.getPriceTo(), ticketsList);
		if(requirements.getDateFrom() != null)
			ticketsList = byFromDate(requirements.getDateFrom(), ticketsList);
		if(requirements.getDateTo() != null)
			ticketsList = byToDate(requirements.getDateTo(), ticketsList);
		if(requirements.getSortType() != null)
			sortBy(ticketsList, requirements.getSortType());
		if(requirements.getFilterType() != null)
			ticketsList = filterByType(ticketsList, requirements.getFilterType());
		if(requirements.getFilterStatus() != null)
			ticketsList = filterByStatus(ticketsList, requirements.getFilterStatus());
		return ticketsList;
	}

	@Override
	public void sortBy(List<Ticket> ticketsList, String sortType) {
		if(sortType.equals("manifestationTitleAscending"))
			Collections.sort(ticketsList, compareByManifestationTitle);
		else if(sortType.equals("manifestationTitleDescending")) 
				Collections.sort(ticketsList, Collections.reverseOrder(compareByManifestationTitle));
		else if(sortType.equals("priceAscending")) 
			Collections.sort(ticketsList, compareByPrice);
		else if(sortType.equals("priceDescending")) 
			Collections.sort(ticketsList, Collections.reverseOrder(compareByPrice));
		else if(sortType.equals("dateTimeAscending"))
			Collections.sort(ticketsList, compareByDate);
		else if(sortType.equals("dateTimeDescending"))
				Collections.sort(ticketsList, Collections.reverseOrder(compareByDate));
	}
	
	public Comparator<Ticket> compareByManifestationTitle = (Ticket m1, Ticket m2) -> manifestationDao.findOne(m1.getManifestationID()).getTitle().toLowerCase().compareTo(manifestationDao.findOne(m2.getManifestationID()).getTitle().toLowerCase());
	public Comparator<Ticket> compareByPrice = (Ticket m1, Ticket m2) -> Double.compare(m1.getPrice(), m2.getPrice());
	public Comparator<Ticket> compareByDate = (Ticket m1, Ticket m2) -> manifestationDao.findOne(m1.getManifestationID()).getDateTime().compareTo(manifestationDao.findOne(m2.getManifestationID()).getDateTime());

	@Override
	public List<Ticket> filterByType(List<Ticket> ticketsList, String filterType) {
		if(filterType.equals("empty") || filterType.equals(""))
			return ticketsList;
		return ticketsList.stream()
								 .filter(m -> m.getType().toString().toLowerCase().equals(filterType.toLowerCase()))
							     .collect(Collectors.toList());
	}
	
	@Override
	public List<Ticket> filterByStatus(List<Ticket> ticketsList, String filterStatus) {
		if(filterStatus.equals("empty") || filterStatus.equals(""))
			return ticketsList;
		return ticketsList.stream()
								 .filter(m -> m.getStatus().toString().toLowerCase().equals(filterStatus.toLowerCase()))
							     .collect(Collectors.toList());
	}
	
	@Override
	public List<Ticket> byManifestation(String manifestationTitle, List<Ticket> ticketsList) {
		return ticketsList.stream()
				 .filter(m -> manifestationDao.findOne(m.getManifestationID()).getTitle().toLowerCase().contains(manifestationTitle.toLowerCase()))
				 .collect(Collectors.toList());
	}

	@Override
	public List<Ticket> byFromDate(Date fromDate, List<Ticket> ticketsList) {
		return ticketsList.stream()
				 .filter(m -> manifestationDao.findOne(m.getManifestationID()).getDateTime().after(fromDate))
				 .collect(Collectors.toList());
	}

	@Override
	public List<Ticket> byToDate(Date toDate, List<Ticket> ticketsList) {
		return ticketsList.stream()
				 .filter(m -> manifestationDao.findOne(m.getManifestationID()).getDateTime().before(toDate))
				 .collect(Collectors.toList());
	}

	@Override
	public List<Ticket> byFromPrice(Double fromPrice, List<Ticket> ticketsList) {
		return ticketsList.stream()
				 .filter(m -> manifestationDao.findOne(m.getManifestationID()).getPriceRegular() >= fromPrice)
				 .collect(Collectors.toList());
	}

	@Override
	public List<Ticket> byToPrice(Double toPrice, List<Ticket> ticketsList) {
		return ticketsList.stream()
				 .filter(m -> manifestationDao.findOne(m.getManifestationID()).getPriceRegular() <= toPrice)
				 .collect(Collectors.toList());
	}

	@Override
	public List<Ticket> getReservedTicketsForManifestationIDs(List<Integer> manifestationIDs) {
		return tickets.values().stream()
							   .filter(t -> manifestationIDs.contains(t.getManifestationID()) && t.getStatus().equals(TicketStatus.RESERVED))
							   .collect(Collectors.toList());
	}	
}
