package dao.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import dao.CustomerDao;
import model.Customer;
import model.CustomerType;
import web.controller.Application;
import web.dto.CustomerSSFDTO;
import model.enums.CustomerTypeName;


public class CustomerDaoImpl implements CustomerDao {
	
	private final Map<String, Customer> customers = new HashMap<>();
	
	public CustomerDaoImpl() {
		load("./data/customer.json");
	}
	
	@Override
	public List<Customer> findAll() {
		return customers.values().stream().collect(Collectors.toList());
	}

	@Override
	public Customer findOne(String username) {
		return customers.getOrDefault(username, null);
	}
	
	@Override
	public void load(String contextPath) {
		Type gsonType = new TypeToken<HashMap<String, Customer>>(){}.getType();
		try {
			setCustomers(Application.g.fromJson(new FileReader(contextPath), gsonType));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean contains(String username) {
		return customers.containsKey(username);
	}
	
	@Override
	public boolean add(Customer customer) {
		if(customer.getCustomerType() == null)
			customer.setCustomerType(new CustomerType());
        customers.put(customer.getUsername(), customer);
		saveChanges();
        return true;
	}

	@Override
	public boolean edit(String username, Customer newCustomer) {		
		Customer oldCustomer = findOne(username);
		oldCustomer.setPassword(newCustomer.getPassword());
		oldCustomer.setFirstName(newCustomer.getFirstName());
		oldCustomer.setLastName(newCustomer.getLastName());
		oldCustomer.setGender(newCustomer.getGender());
		oldCustomer.setBirthDate(newCustomer.getBirthDate());
		customers.replace(username, oldCustomer);
		saveChanges();
		return true;
	}
	
	@Override
	public void saveChanges() {
		Type gsonType = new TypeToken<HashMap<String, Customer>>(){}.getType();
		try {
			String jsonOut = Application.g.toJson(customers, gsonType);
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/customer.json"));
			writer.write(jsonOut);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		};
	}
	
	private void setCustomers(Map<String, Customer> customers1) {
		for(Map.Entry<String, Customer> entry: customers1.entrySet()) {
			this.customers.put(entry.getKey(), new Customer(entry.getValue()));
		}
	}

	@Override
	public List<Customer> findAllExcludingDeleted() {
		return customers.values().stream().filter(t -> !t.isDeleted()).collect(Collectors.toList());
	}

	@Override
	public Customer delete(String username) {
		Customer s = findOne(username);
		s.setDeleted(true);
		customers.replace(username, s);
		saveChanges();
		return s;
	}

	@Override
	public Customer block(String username, Boolean isBlocked) {
		Customer s = findOne(username);
		s.setBlocked(isBlocked);
		customers.replace(username, s);
		saveChanges();
		return s;
	}

	@Override
	public List<Customer> ssfCustomers(CustomerSSFDTO requirements) {
		List<Customer> customersList = findAllExcludingDeleted();
		if(requirements.getUsername() != null && !requirements.getUsername().isEmpty())
			customersList = byUsername(requirements.getUsername(), customersList);
		if(requirements.getFirstName() != null && !requirements.getFirstName().isEmpty())
			customersList = byFirstName(requirements.getFirstName(), customersList);
		if(requirements.getLastName() != null && !requirements.getLastName().isEmpty())
			customersList = byLastName(requirements.getLastName(), customersList);
		if(requirements.getSortType() != null && !requirements.getSortType().isEmpty())
			sortBy(customersList, requirements.getSortType());
		if(requirements.getFilterType() != null && !requirements.getFilterType().isEmpty() && !requirements.getFilterType().toLowerCase().equals("empty"))
			customersList = filterBy(customersList, requirements.getFilterType());

		return customersList;
	}

	@Override
	public void sortBy(List<Customer> customersList, String sortType) {
		if(sortType.equals("usernameAscending"))
				Collections.sort(customersList, compareByUsername);
		else if(sortType.equals("usernameDesceding"))
			Collections.sort(customersList, Collections.reverseOrder(compareByUsername));
		else if(sortType.equals("firstNameAsceding"))
			Collections.sort(customersList, compareByFirstName);
		else if(sortType.equals("firstNameDesceding"))
			Collections.sort(customersList, Collections.reverseOrder(compareByFirstName));
		else if(sortType.equals("lastNameAsceding"))
			Collections.sort(customersList, compareByLastName);
		else if(sortType.equals("lastNameDesceding"))
			Collections.sort(customersList, Collections.reverseOrder(compareByLastName));
		else if(sortType.equals("pointsAsceding")) 
			Collections.sort(customersList, compareByPoints);
		else if(sortType.equals("pointsDesceding"))
			Collections.sort(customersList, Collections.reverseOrder(compareByPoints));
	}

	@Override
	public List<Customer> filterBy(List<Customer> customersList, String filterType) {
		CustomerTypeName ctn = CustomerTypeName.REGULAR;
		if(filterType.toLowerCase().equals("bronze"))
			ctn = CustomerTypeName.BRONZE;
		else if(filterType.toLowerCase().equals("silver"))
			ctn = CustomerTypeName.SILVER;
		else if(filterType.toLowerCase().equals("gold"))
			ctn = CustomerTypeName.GOLD;
		final CustomerTypeName ctn2 = ctn;
		return customersList.stream()
				 .filter(m -> m.getCustomerType().getName() == ctn2)
			     .collect(Collectors.toList());
	}

	@Override
	public List<Customer> byUsername(String username, List<Customer> customers) {
		return customers.stream()
				 .filter(m -> m.getUsername().toLowerCase().contains(username.toLowerCase()))
				 .collect(Collectors.toList());
	}

	@Override
	public List<Customer> byFirstName(String username, List<Customer> customers) {
		return customers.stream()
				 .filter(m -> m.getFirstName().toLowerCase().contains(username.toLowerCase()))
				 .collect(Collectors.toList());
	}

	@Override
	public List<Customer> byLastName(String username, List<Customer> customers) {
		return customers.stream()
				 .filter(m -> m.getLastName().toLowerCase().contains(username.toLowerCase()))
				 .collect(Collectors.toList());
	}
	
	public Comparator<Customer> compareByUsername = (Customer m1, Customer m2) -> m1.getUsername().toLowerCase().compareTo(m2.getUsername().toLowerCase());
	public Comparator<Customer> compareByFirstName = (Customer m1, Customer m2) -> m1.getFirstName().toLowerCase().compareTo(m2.getFirstName().toLowerCase());
	public Comparator<Customer> compareByLastName = (Customer m1, Customer m2) -> m1.getLastName().toLowerCase().compareTo(m2.getLastName().toLowerCase());
	public Comparator<Customer> compareByPoints = (Customer m1, Customer m2) -> Double.compare(m1.getPoints(), m2.getPoints());
}
