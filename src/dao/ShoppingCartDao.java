package dao;

import model.ShoppingCart;

public interface ShoppingCartDao extends GenericDao<Integer, ShoppingCart>{
	
	void add(ShoppingCart cart);
}
