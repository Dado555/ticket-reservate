package dao.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
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

import dao.ManifestationDao;
import model.Manifestation;
import model.Salesman;
import model.enums.ManifastationStatus;
import web.controller.Application;
import web.dto.ManifestationSSFDTO;


public class ManifestationDaoImpl implements ManifestationDao {

	private final Map<Integer, Manifestation> manifestations = new HashMap<>();

	private int autoGeneratedID = 0;

	public ManifestationDaoImpl() {
		load("./data/manifestation.json");
	}

	@Override
	public List<Manifestation> findAll() {
		return manifestations.values().stream().collect(Collectors.toList());
	}
	
	@Override
	public Manifestation findOne(Integer id) {
		return manifestations.getOrDefault(id, null);
	}
	
	@Override
	public List<Manifestation> getManifestationsForSalesman(Salesman salesman) {
		return manifestations.values().stream()
									  .filter(m -> salesman.getManifestationIDs().contains(m.getId()) && !m.isDeleted())
									  .collect(Collectors.toList());
	}
	
	@Override
	public List<Integer> getTicketIDsForManifestation(Integer manifestationID) {
		System.out.println(manifestationID);
		return new ArrayList<>(manifestations.get(manifestationID).getTicketIDs());
	}
	
	@Override
	public void load(String contextPath) {
		Type gsonType = new TypeToken<HashMap<Integer, Manifestation>>() {}.getType();
		try {
			setManifestations(Application.g.fromJson(new FileReader(contextPath), gsonType));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean add(Manifestation manifestation) {
		if (manifestation.getId() == null) {
			manifestation.setId(autoGeneratedID++);
		}
		manifestations.put(manifestation.getId(), manifestation);
		saveChanges();
		return true;
	}

	@Override
	public boolean edit(Integer id, Manifestation newManifestation) {
		Manifestation oldManifestation = findOne(id);
		oldManifestation.setTitle(newManifestation.getTitle());
		oldManifestation.setType(newManifestation.getType());
		oldManifestation.setSeats(newManifestation.getSeats());
		oldManifestation.setDateTime(newManifestation.getDateTime());
		oldManifestation.setPriceRegular(newManifestation.getPriceRegular());
		oldManifestation.setStatus(newManifestation.getStatus());
		oldManifestation.setLocation(newManifestation.getLocation());
		manifestations.replace(id, oldManifestation);
		saveChanges();
		return true;
	}

	@Override
	public void saveChanges() {
		Type gsonType = new TypeToken<HashMap<Integer, Manifestation>>() {}.getType();
		try {
			String jsonOut = Application.g.toJson(manifestations, gsonType);
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/manifestation.json"));
			writer.write(jsonOut);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		};
	}

	private void setManifestations(Map<Integer, Manifestation> manifestations1) {
		for (Map.Entry<Integer, Manifestation> entry : manifestations1.entrySet()) {
			this.manifestations.put(entry.getKey(), new Manifestation(entry.getValue()));
		}
	}
	
	@Override
	public List<Manifestation> byTitle(String title, List<Manifestation> manifestations) {
		return manifestations.stream()
							 .filter(m -> m.getTitle().toLowerCase().contains(title.toLowerCase()))
							 .collect(Collectors.toList());
	}
	
	@Override
	public List<Manifestation> byFromDate(Date fromDate, List<Manifestation> manifestations) {
		return manifestations.stream()
							 .filter(m -> m.getDateTime().after(fromDate))
							 .collect(Collectors.toList());
	}
	
	@Override
	public List<Manifestation> byToDate(Date toDate, List<Manifestation> manifestations) {
		return manifestations.stream()
							 .filter(m-> m.getDateTime().before(toDate))
							 .collect(Collectors.toList());
	}
	
	@Override
	public List<Manifestation> byFromPrice(Double fromPrice, List<Manifestation> manifestations) {
		return manifestations.stream()
							 .filter(m -> m.getPriceRegular() >= fromPrice)
							 .collect(Collectors.toList());
	}
	
	@Override
	public List<Manifestation> byToPrice(Double toPrice, List<Manifestation> manifestations) {
		return manifestations.stream()
				 .filter(m -> m.getPriceRegular() <= toPrice)
				 .collect(Collectors.toList());
	}

	@Override
	public List<Manifestation> byAddress(String cityCountry, List<Manifestation> manifestations) {
		return manifestations.stream()
							 .filter(m -> m.getLocation().getAddress().toLowerCase().contains(cityCountry.toLowerCase()))
							 .collect(Collectors.toList());
	}
	
	@Override
	public List<Manifestation> byAvailability(List<Manifestation> manifestations) {
		return manifestations.stream()
							 .filter(m -> m.getSeats() > 0)
							 .collect(Collectors.toList());
	}
	
	@Override
	public List<Manifestation> ssfManifestations(ManifestationSSFDTO requirements, Salesman salesman) {
		List<Manifestation> manifestationsList;
		if(salesman != null)
			manifestationsList = getManifestationsForSalesman(salesman);
		else
			manifestationsList = findAllExcludingDeleted();
		if(requirements.getTitle() != null && !requirements.getTitle().isEmpty())
			manifestationsList = byTitle(requirements.getTitle(), manifestationsList);
		if(requirements.getCityCountry() != null && !requirements.getCityCountry().isEmpty())
			manifestationsList = byAddress(requirements.getCityCountry(), manifestationsList);
		if(requirements.getFromDate() != null)
			manifestationsList = byFromDate(requirements.getFromDate(), manifestationsList);
		if(requirements.getToDate() != null)
			manifestationsList = byToDate(requirements.getToDate(), manifestationsList);
		if(requirements.getFromPrice() != null && requirements.getFromPrice() > 0)
			manifestationsList = byFromPrice(requirements.getFromPrice(), manifestationsList);
		if(requirements.getToPrice() != null &&  requirements.getToPrice() > 0) 
			manifestationsList = byToPrice(requirements.getToPrice(), manifestationsList);
		if(requirements.getSortType() != null && !requirements.getSortType().isEmpty()) {
			double lat = 0, lon = 0;
			try {
				if(requirements.getLongitude() > 0) {
					lat = requirements.getLatitude();
					lon = requirements.getLongitude();
				}
			} catch(Exception e) {}
			sortBy(manifestationsList, requirements.getSortType(), lon, lat);
		}
		if(requirements.getFilterType() != null && !requirements.getFilterType().isEmpty())
			manifestationsList = filterBy(manifestationsList, requirements.getFilterType());
		if(requirements.isAvailableTickets() != null && requirements.isAvailableTickets())
			manifestationsList = byAvailability(manifestationsList);

		return manifestationsList;
	}
	
	@Override
	public void sortBy(List<Manifestation> manifestationsList, String sortType, double longitude, double latitude) {
		
		if(sortType.equals("titleAscending"))
			Collections.sort(manifestationsList, compareByTitle);
		else if(sortType.equals("titleDescending")) 
				Collections.sort(manifestationsList, Collections.reverseOrder(compareByTitle));
		else if(sortType.equals("dateTimeAscending")) 
			Collections.sort(manifestationsList, compareByDate);
		else if(sortType.equals("dateTimeDescending")) 
			Collections.sort(manifestationsList, Collections.reverseOrder(compareByDate));
		else if(sortType.equals("priceRegularAscending"))
			Collections.sort(manifestationsList, compareByPrice);
		else if(sortType.equals("priceRegularDescending"))
				Collections.sort(manifestationsList, Collections.reverseOrder(compareByPrice));
		else if(sortType.equals("locationAscending")) {
			Collections.sort(manifestationsList, new Comparator<Manifestation>() {
				@Override
				public int compare(Manifestation m1, Manifestation m2) {
					return Double.compare(m1.getLocation().distance(latitude, longitude), m2.getLocation().distance(latitude, longitude));
				}
			});
		}
		else if(sortType.equals("locationDescending")) {
			Collections.sort(manifestationsList, Collections.reverseOrder(new Comparator<Manifestation>() {
				@Override
				public int compare(Manifestation m1, Manifestation m2) {
					return Double.compare(m1.getLocation().distance(latitude, longitude), m2.getLocation().distance(latitude, longitude));
				}
			}));
		}
	}
	
	@Override
	public List<Manifestation> filterBy(List<Manifestation> manifestationsList, String filterType) {
		if(filterType.equals("empty") || filterType.equals(""))
			return manifestationsList;
		else if(filterType.equals("available"))
			return manifestationsList.stream()
					 .filter(m -> m.getSeats() > 0)
				     .collect(Collectors.toList());
		else 
			return manifestationsList.stream()
									 .filter(m -> m.getType().toLowerCase().equals(filterType.toLowerCase()))
								     .collect(Collectors.toList());
	}
	
	public Comparator<Manifestation> compareByTitle = (Manifestation m1, Manifestation m2) -> m1.getTitle().toLowerCase().compareTo(m2.getTitle().toLowerCase());
	public Comparator<Manifestation> compareByPrice = (Manifestation m1, Manifestation m2) -> Double.compare(m1.getPriceRegular(), m2.getPriceRegular());
	public Comparator<Manifestation> compareByDate = (Manifestation m1, Manifestation m2) -> m1.getDateTime().compareTo(m2.getDateTime());

	@Override
	public List<Manifestation> findAllExcludingDeleted() {
		return manifestations.values().stream().filter(t -> !t.isDeleted()).collect(Collectors.toList());
	}

	@Override
	public Manifestation approve(Integer id) {
		Manifestation s = findOne(id);
		s.setStatus(ManifastationStatus.ACTIVE);
		manifestations.replace(id, s);
		saveChanges();
		return s;
	}

	@Override
	public List<Manifestation> inactive() {
		return manifestations.values().stream().filter(t -> t.getStatus() == ManifastationStatus.INACTIVE).collect(Collectors.toList());
	}

	@Override
	public boolean checkTimeLocation(Manifestation manifestation) {
		Collection<Manifestation> filtered = manifestations.values()
					  						  .stream()
					  						  .filter(m -> m.getDateTime().equals(manifestation.getDateTime()) && m.getLocation().distance(manifestation.getLocation().getLatitude(), manifestation.getLocation().getLongitude()) < 10)
					  						  .collect(Collectors.toList());
		System.out.println(filtered.size());
		if (filtered.size() > 0)
			return false;

		return true;
	}
}
