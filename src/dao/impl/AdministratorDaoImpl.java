package dao.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import dao.AdministratorDao;
import model.Administrator;
import web.controller.Application;


public class AdministratorDaoImpl implements AdministratorDao {
	
	private Map<String, Administrator> administrators = new HashMap<>();

	public AdministratorDaoImpl() {
		load("./data/admin.json");
	}

	@Override
	public List<Administrator> findAll() {
		return administrators.values().stream().collect(Collectors.toList());
	}
	
	@Override
	public Administrator findOne(String username) {
		return administrators.getOrDefault(username, null);
	}
	
	@Override
	public void load(String contextPath) {
		Type gsonType = new TypeToken<HashMap<String, Administrator>>(){}.getType();
		try {
			setAdministrators(Application.g.fromJson(new FileReader(contextPath), gsonType));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean contains(String username) {
		return administrators.containsKey(username);
	}
	
	private void setAdministrators(Map<String, Administrator> administrators1) {
		for(Map.Entry<String, Administrator> entry: administrators1.entrySet()) {
			this.administrators.put(entry.getKey(), new Administrator(entry.getValue()));
		}
	}

	@Override
	public void saveChanges() {
		return;
	}
}
