package dao;

import java.util.List;

import model.Customer;
import web.dto.CustomerSSFDTO;

public interface CustomerDao extends GenericDao<String, Customer>{
	
	boolean add(Customer customer);
	
	boolean edit(String username, Customer newCustomer);
	
	boolean contains(String username);
		
	Customer delete(String username);
	
	Customer block(String username, Boolean isBlocked);
	
	List<Customer> ssfCustomers(CustomerSSFDTO requirements);
	
	void sortBy(List<Customer> customersList, String sortType);

	List<Customer> filterBy(List<Customer> customersList, String filterType);
	
	List<Customer> byUsername(String username, List<Customer> customers);
	
	List<Customer> byFirstName(String username, List<Customer> customers);

	List<Customer> byLastName(String username, List<Customer> customers);
	
	List<Customer> findAllExcludingDeleted();
}
