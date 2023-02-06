package web.controller;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import dao.AdministratorDao;
import dao.CustomerDao;
import dao.SalesmanDao;
import dao.UserDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import model.Customer;
import model.User;
import spark.Route;
import support.CustomerToCustomerDTO;
import support.UserToUserDTO;
import web.dto.ErrorDTO;
import web.dto.UserDTO;


public class AuthController {

	public static final UserDao userDao = Application.userDao;
	public static final AdministratorDao administratorDao = Application.administratorDao;
	public static final SalesmanDao salesmanDao = Application.salesmanDao;
	public static final CustomerDao customerDao = Application.customerDao;
	
	public static final UserToUserDTO userToUserDTO = new UserToUserDTO();
	public static final CustomerToCustomerDTO customerToCustomerDTO = new CustomerToCustomerDTO();

	public static final Gson g = Application.g;

	public static final Key key = Application.key;
	
	
	public static Route handleRegistration = (request, response) -> {
		response.type("application/json");
		
		String payload = request.body();
		Customer customer = g.fromJson(payload, Customer.class);	
		
		if(userDao.contains(customer.getUsername()))
			return g.toJson(new ErrorDTO("Username already exists!", "AuthException", 100));
		
		customerDao.add(customer);		
		return g.toJson(customerToCustomerDTO.convert(customer));
	};
	
	@SuppressWarnings("unchecked")
	public static Route handleLogin = (request, response) -> {
		response.type("application/json");

		String payload = request.body();
		Map<String, String> data =  g.fromJson(payload, HashMap.class);
		String username = data.get("username");
		String password = data.get("password");

		User user = userDao.findOne(username);
		if (user == null)
			return g.toJson(new ErrorDTO("User is not logged in!", "AuthException", 100));
		if (!(user.getPassword().equals(password.trim())))
			return g.toJson(new ErrorDTO("Invalid password!", "AuthException", 100));
		if (customerDao.findOne(user.getUsername()) != null) { 
			if (((Customer)user).isBlocked() || ((Customer)user).isDeleted())
				return g.toJson(new ErrorDTO("User blocked or deleted!", "AuthException", 100));
		}
			
		Date date = new Date();	// JWT will expire in 2 hours
		String jwt = Jwts.builder().setSubject(user.getUsername()).setExpiration(new Date(date.getTime() + 1000*7200L)).setIssuedAt(date).signWith(key).compact();
		
		UserDTO userDTO = userToUserDTO.convert(user);
		userDTO.setJwt(jwt);
		return g.toJson(userDTO);
	};
		
	public static User getCurrentUser(String auth) {
		String username = null;
		if ((auth != null) && (auth.contains("Bearer "))) {
			String jwt = auth.substring(auth.indexOf("Bearer ") + 7);
			try {
			    Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
				username = claims.getBody().getSubject();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		if (username == null)
			return null;
		
		return userDao.findOne(username);
	}
	
}
