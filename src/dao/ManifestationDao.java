package dao;

import java.util.Date;
import java.util.List;

import model.Manifestation;
import model.Salesman;
import web.dto.ManifestationSSFDTO;


public interface ManifestationDao extends GenericDao<Integer, Manifestation>{
	
	List<Manifestation> findAllExcludingDeleted();
	
	List<Manifestation> getManifestationsForSalesman(Salesman salesman);

	List<Integer> getTicketIDsForManifestation(Integer manifestationID);
	
	boolean add(Manifestation manifestation);
	
	boolean edit(Integer id, Manifestation newManifestation);
	
	boolean checkTimeLocation(Manifestation manifestation);
		
	List<Manifestation> ssfManifestations(ManifestationSSFDTO requirements, Salesman salesman);
	
	void sortBy(List<Manifestation> manifestationsList, String sortType, double longitude, double latitude);

	List<Manifestation> filterBy(List<Manifestation> manifestationsList, String filterType);
	
	List<Manifestation> byTitle(String title, List<Manifestation> manifestations);
	
	List<Manifestation> byFromDate(Date fromDate, List<Manifestation> manifestations);

	List<Manifestation> byToDate(Date toDate, List<Manifestation> manifestations);
	
	List<Manifestation> byFromPrice(Double fromPrice, List<Manifestation> manifestations);
	
	List<Manifestation> byToPrice(Double toPrice, List<Manifestation> manifestations);
	
	List<Manifestation> byAddress(String cityCountry, List<Manifestation> manifestations);
	
	List<Manifestation> byAvailability(List<Manifestation> manifestations);
	
	Manifestation approve(Integer id);
	
	List<Manifestation> inactive();
}
