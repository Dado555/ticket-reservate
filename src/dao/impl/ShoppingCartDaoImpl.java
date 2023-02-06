package dao.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import dao.ShoppingCartDao;
import dao.TicketDao;
import model.ShoppingCart;
import model.ShoppingCartItem;
import web.controller.Application;


public class ShoppingCartDaoImpl implements ShoppingCartDao {

	private final Map<Integer, ShoppingCart> carts = new HashMap<>();
	private final TicketDao ticketDao = Application.ticketDao;
	
	public ShoppingCartDaoImpl() {
		load("./data/shopping-cart.json");
	}
	
	@Override
	public List<ShoppingCart> findAll() {
		return carts.values().stream().collect(Collectors.toList());
	}

	@Override
	public ShoppingCart findOne(Integer id) {
		return carts.getOrDefault(id, null);
	}
	@Override
	public void saveChanges() {
		Type gsonType = new TypeToken<HashMap<Integer, ShoppingCart>>(){}.getType();
		try {
			String jsonOut = Application.g.toJson(carts, gsonType);
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/shopping-cart.json"));
			writer.write(jsonOut);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		};
	}
	
	@Override
	public void add(ShoppingCart cart) {
		carts.put(cart.getCartID(), cart);
		saveChanges();
	}
	
	@Override
	public void load(String contextPath) {
		Type gsonType = new TypeToken<HashMap<Integer, ShoppingCart>>(){}.getType();
		try {
			setCarts(Application.g.fromJson(new FileReader(contextPath), gsonType));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void setCarts(Map<Integer, ShoppingCart> cart) {
		for(Map.Entry<Integer, ShoppingCart> entry: cart.entrySet()) {
			this.carts.put(entry.getKey(), new ShoppingCart(entry.getValue()));
			
			for (ShoppingCartItem item : entry.getValue().getItems()) {
				List<Integer> ids = new ArrayList<>();
				item.getTickets().forEach(t -> ids.add(t.getId()));
				
				item.setTickets(ticketDao.findAll().stream()
												   .filter(t -> ids.contains(t.getId()))
												   .collect(Collectors.toList())
								);
			}
		}
	}


}
