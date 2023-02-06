package dao;

import java.util.List;

import model.Salesman;
import web.dto.SalesmanSSFDTO;


public interface SalesmanDao extends GenericDao<String, Salesman> {
			
	List<Salesman> findAllExcludingDeleted();
	
	void add(Salesman salesman);
		
	boolean edit(String username, Salesman newSalesman);
	
	boolean contains(String username);
	
	void load(String contextPath);
		
	Salesman delete(String username);
		
	Salesman block(String username);
	
	List<Salesman> ssfSalesmans(SalesmanSSFDTO requirements);
	
	void sortBy(List<Salesman> salesmansList, String sortType);

	List<Salesman> filterBy(List<Salesman> salesmansList, String filterType);
	
	List<Salesman> byUsername(String username, List<Salesman> salesmansList);
	
	List<Salesman> byFirstName(String username, List<Salesman> salesmansList);

	List<Salesman> byLastName(String username, List<Salesman> salesmansList);
}
