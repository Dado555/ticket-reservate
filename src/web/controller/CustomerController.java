package web.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import dao.CancelationDao;
import dao.impl.AdministratorDaoImpl;
import dao.impl.CustomerDaoImpl;
import model.Cancelation;
import model.Customer;
import model.User;
import spark.Route;
import support.CustomerToCustomerDTO;
import web.dto.CustomerSSFDTO;
import web.dto.ErrorDTO;

public class CustomerController {
	

	public static final AdministratorDaoImpl administratorDao = Application.administratorDao;
	public static final CustomerDaoImpl customerDao = Application.customerDao;
	public static final CancelationDao cancelationDao = Application.cancelationDao;

	public static CustomerToCustomerDTO customerToCustomerDTO = new CustomerToCustomerDTO();

	public static final Gson g = Application.g;
	public static final Gson gsonDate = Application.gsonDate;
	
	
	public static Route getAllCustomers = (request, response) -> {
		List<Customer> customers = customerDao.findAllExcludingDeleted();
		for (Customer customer : customers) {
			System.out.println(customer.isSuspicious());
		}
		response.type("application/json");
		return g.toJson(customerToCustomerDTO.convert(customers));
	};

	public static Route getOneCustomer = (request, response) -> {
		String username = request.params("username");
		Customer customer = customerDao.findOne(username);

		response.type("application/json");
		return g.toJson(customerToCustomerDTO.convert(customer));
	};
	
	public static Route blockCustomer = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("You have no permission to block customer!", "AuthException", 100));
		if (administratorDao.findOne(current.getUsername()) == null)
			return g.toJson(new ErrorDTO("You have no permission to block customer!", "AuthException", 100));
		
		String username = request.params("username");
		if (customerDao.findOne(username) == null)
			return g.toJson(new ErrorDTO("Customer does not exists!", "AuthException", 100));
		
		Boolean isBlocked = g.fromJson(request.body(), Boolean.class);
		Customer newCustomer = customerDao.block(username, isBlocked);
		return g.toJson(customerToCustomerDTO.convert(newCustomer));
	};
	
	public static Route deleteCustomer = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("You have no permission to delete customer!", "AuthException", 100));
		if (administratorDao.findOne(current.getUsername()) == null)
			return g.toJson(new ErrorDTO("You have no permission to delete customer!", "AuthException", 100));
		
		String username = request.params("username");
		Customer customer = customerDao.findOne(username);
		if (customer == null)
			return g.toJson(new ErrorDTO("Customer does not exists!", "AuthException", 100));
		
		customerDao.delete(username);
		return g.toJson(customerToCustomerDTO.convert(customer));
	};
	
	public static Route handleSearchSortFilter = (request, response) -> {
		String payload = request.body();
		System.out.println(payload);
		CustomerSSFDTO customer = gsonDate.fromJson(payload, CustomerSSFDTO.class);
		System.out.println(customer);
		
		List<Customer> results = customerDao.ssfCustomers(customer);
		response.type("application/json");
		return g.toJson(customerToCustomerDTO.convert(results));
	};
	
	public static Route editCustomer = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("You have no permission to edit customer!", "AuthException", 100));
		
		String username = request.params("username");
		Customer customer = customerDao.findOne(username);
		if (customer == null)
			return g.toJson(new ErrorDTO("You have no permission to edit customer!", "AuthException", 100));
		
		String payload = request.body();
		System.out.println(payload);
		Customer newCustomer = g.fromJson(payload, Customer.class);
		if(newCustomer.getPassword().equals(""))
			newCustomer.setPassword(customer.getPassword());
		customerDao.edit(username, newCustomer);
		return g.toJson(customerToCustomerDTO.convert(newCustomer));
	};
	
	public static Route getSuspiciousCustomers = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));		
		if(administratorDao.findOne(current.getUsername()) == null)
			return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
		
		List<Customer> customers = customerDao.findAll();
		for (Customer customer : customers) {
			List<Cancelation> cancelations = cancelationDao.findAll()
														   .stream()
														   .filter(c -> c.getUsername().equals(customer.getUsername()) && customer.checkSuspicious(c))
														   .collect(Collectors.toList());
			if (cancelations.size() > 5) {
				customer.setSuspicious(true);
				customerDao.add(customer);
				customerDao.saveChanges();
			}
		}
		
		List<Customer> filtered = customerDao.findAll().stream().filter(c -> c.isSuspicious()).collect(Collectors.toList());
		return g.toJson((new CustomerToCustomerDTO()).convert(filtered));
	};
}
