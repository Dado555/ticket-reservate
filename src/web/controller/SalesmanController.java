package web.controller;

import java.util.List;

import com.google.gson.Gson;

import dao.impl.AdministratorDaoImpl;
import dao.impl.SalesmanDaoImpl;
import model.Salesman;
import model.User;
import spark.Route;
import support.SalesmanToSalesmanDTO;
import web.dto.ErrorDTO;
import web.dto.SalesmanSSFDTO;

public class SalesmanController {

	public static final AdministratorDaoImpl administratorDao = Application.administratorDao;
	public static final SalesmanDaoImpl salesmanDao = Application.salesmanDao;
	
	public static final SalesmanToSalesmanDTO salesmanToSalesmanDTO = new SalesmanToSalesmanDTO();
	
	public static Gson g = Application.g;
	public static Gson gson = Application.gsonDate;
	
	
	public static Route getAllSalesmans = (request, response) -> {
		List<Salesman> salesmans = salesmanDao.findAllExcludingDeleted();
		response.type("application/json");
		System.out.println(salesmanToSalesmanDTO.convert(salesmans));
		return g.toJson(salesmanToSalesmanDTO.convert(salesmans));
	};
	
	
	public static Route createSalesman = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("You have no permission to create salesman!", "AuthException", 100));
		if(administratorDao.findOne(current.getUsername()) == null)
			return g.toJson(new ErrorDTO("You have no permission to create salesman!", "AuthException", 100));
		
		String payload = request.body();
		Salesman salesman = g.fromJson(payload, Salesman.class);			
		salesmanDao.add(salesman);
		return g.toJson(salesmanToSalesmanDTO.convert(salesman));
	};
	
	public static Route editSalesman = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("You have no permission to edit salesman!", "AuthException", 100));
		if (administratorDao.findOne(current.getUsername()) == null)
			return g.toJson(new ErrorDTO("You have no permission to edit salesman!", "AuthException", 100));

		String username = request.params("username");
		if (salesmanDao.findOne(username) == null)
			return g.toJson(new ErrorDTO("Salesman does not exist!", "ExistentException", 200));
		
		String payload = request.body();
		Salesman newSalesman = gson.fromJson(payload, Salesman.class);
		/*
		Salesman newSalesman = null;
		try {
			newSalesman = gson.fromJson(payload, Salesman.class);
		} catch (Exception e) {
			newSalesman = g.fromJson(payload, Salesman.class);
		}
		*/
		salesmanDao.edit(username, newSalesman);
		return g.toJson(salesmanToSalesmanDTO.convert(newSalesman));
	};
	
	public static Route deleteSalesman = (request, response) -> {
		response.type("application/json");

		User current = AuthController.getCurrentUser(request.headers("Authorization"));
		if (current == null)
			return g.toJson(new ErrorDTO("You have no permission to delete salesman!", "AuthException", 100));
		if (administratorDao.findOne(current.getUsername()) == null)
			return g.toJson(new ErrorDTO("You have no permission to delete salesman!", "AuthException", 100));
		
		String username = request.params("username");
		Salesman salesman = salesmanDao.findOne(username);
		if (salesman == null)
			return g.toJson(new ErrorDTO("Salesman does not exist!", "ExistentException", 200));
		
		salesmanDao.delete(username);
		return g.toJson(salesmanToSalesmanDTO.convert(salesman));
	};
	
	public static Route handleSearchSortFilter = (request, response) -> {
		String payload = request.body();
		System.out.println(payload);
		SalesmanSSFDTO salesman = g.fromJson(payload, SalesmanSSFDTO.class);
		System.out.println(salesman);
		
		List<Salesman> results = salesmanDao.ssfSalesmans(salesman);
		response.type("application/json");
		return g.toJson(results);
	};
	
}
