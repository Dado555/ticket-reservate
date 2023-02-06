package dao;

import model.User;


public interface UserDao  extends GenericDao<String, User>{
		
	public boolean contains(String username);

}
