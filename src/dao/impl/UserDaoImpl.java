package dao.impl;

import java.util.List;

import dao.UserDao;
import model.Administrator;
import model.Customer;
import model.Salesman;
import model.User;

public class UserDaoImpl implements UserDao {
	
	private AdministratorDaoImpl administratorDao;
	private CustomerDaoImpl customerDao;
	private SalesmanDaoImpl salesmanDao;
	
	public UserDaoImpl(AdministratorDaoImpl adi, CustomerDaoImpl cdi, SalesmanDaoImpl sdi) {
		administratorDao = adi;
		customerDao = cdi;
		salesmanDao = sdi;
	}	
	
	@Override
	public User findOne(String username) {
		Administrator admin = administratorDao.findOne(username);
		Salesman salesman = salesmanDao.findOne(username);
		Customer customer = customerDao.findOne(username);

		User user = admin;
		if (user == null)
			user = salesman;
		if (user == null)
			user = customer;
		
		return user;
	}

	@Override
	public boolean contains(String username) {
		return (administratorDao.contains(username) || salesmanDao.contains(username) || customerDao.contains(username)) ? true : false;
	}
	
	
	@Override
	public List<User> findAll() {
		return null;
	}
	
	@Override
	public void load(String path) {
		return;
	}

	@Override
	public void saveChanges() {
		return;
	}

}
