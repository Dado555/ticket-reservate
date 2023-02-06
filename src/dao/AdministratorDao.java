package dao;

import model.Administrator;


public interface AdministratorDao extends GenericDao<String, Administrator>{
	
	public boolean contains(String username);
	
}
