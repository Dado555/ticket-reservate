package dao.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import dao.CancelationDao;
import model.Cancelation;
import web.controller.Application;

public class CancelationDaoImpl implements CancelationDao {

	private List<Cancelation> cancelations = new ArrayList<>();
	
	public CancelationDaoImpl() {
		load("./data/cancelations.json");			
	}
	
	@Override
	public List<Cancelation> findAll() {
		return cancelations;
	}

	@Override
	public void saveChanges() {
		Type gsonType = new TypeToken<List<Cancelation>>(){}.getType();
		try {
			String jsonOut = Application.g.toJson(cancelations, gsonType);
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/cancelations.json"));
			writer.write(jsonOut);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		};
	}

	@Override
	public void load(String contextPath) {
		Type gsonType = new TypeToken<List<Cancelation>>(){}.getType();
		try {
			setCancelations(Application.g.fromJson(new FileReader(contextPath), gsonType));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void setCancelations(List<Cancelation> cancelations1) {
		for(Cancelation entry: cancelations1) {
			this.cancelations.add(new Cancelation(entry));
		}
	}
	
	@Override
	public boolean add(Cancelation cancelation) {
		cancelations.add(cancelation);
		return true;
	}
	
	@Override
	public Cancelation findOne(Integer id) {
		return null;
	}

	
}
