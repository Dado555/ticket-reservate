package dao.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import dao.SalesmanDao;
import model.Salesman;
import web.controller.Application;
import web.dto.SalesmanSSFDTO;


public class SalesmanDaoImpl implements SalesmanDao {

	private final Map<String, Salesman> salesmans = new HashMap<>();;
	
	public SalesmanDaoImpl() {
		load("./data/salesman.json");
	}
	
	@Override
	public List<Salesman> findAll() {
		return salesmans.values().stream().collect(Collectors.toList());
	}

	@Override
	public Salesman findOne(String id) {
		return salesmans.getOrDefault(id, null);
	}
	
	@Override
	public boolean contains(String username) {
		return salesmans.containsKey(username);
	}
	
	@Override
	public void add(Salesman salesman) {
		salesmans.put(salesman.getUsername(), salesman);
		saveChanges();
	}
	
	@Override
	public boolean edit(String username, Salesman newSalesman) {
		Salesman oldSalesman = findOne(username);
		oldSalesman.setPassword(newSalesman.getPassword());
		oldSalesman.setFirstName(newSalesman.getFirstName());
		oldSalesman.setLastName(newSalesman.getLastName());
		oldSalesman.setGender(newSalesman.getGender());
		oldSalesman.setBirthDate(newSalesman.getBirthDate());
		salesmans.replace(username, oldSalesman);
		saveChanges();
		return true;
	}

	@Override
	public void load(String contextPath) {
		Type gsonType = new TypeToken<HashMap<String, Salesman>>(){}.getType();
		try {
			setSalesmans(Application.g.fromJson(new FileReader(contextPath), gsonType));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveChanges() {
		Type gsonType = new TypeToken<HashMap<String, Salesman>>(){}.getType();
		try {
			String jsonOut = Application.g.toJson(salesmans, gsonType);
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/salesman.json"));
			writer.write(jsonOut);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		};
	}

	private void setSalesmans(Map<String, Salesman> salesmans1) {
		for(Map.Entry<String, Salesman> entry: salesmans1.entrySet()) {
			this.salesmans.put(entry.getKey(), new Salesman(entry.getValue()));
		}
	}

	@Override
	public Salesman delete(String username) {
		Salesman s = findOne(username);
		s.setDeleted(true);
		salesmans.replace(username, s);
		saveChanges();
		return s;
	}

	@Override
	public List<Salesman> findAllExcludingDeleted() {
		return salesmans.values().stream().filter(t -> !t.isDeleted()).collect(Collectors.toList());
	}

	@Override
	public Salesman block(String username) {
		Salesman s = findOne(username);
		s.setBlocked(true);
		salesmans.replace(username, s);
		saveChanges();
		return s;
	}

	@Override
	public List<Salesman> ssfSalesmans(SalesmanSSFDTO requirements) {
		List<Salesman> salesmansList = findAllExcludingDeleted();
		if(requirements.getUsername() != null && !requirements.getUsername().isEmpty())
			salesmansList = byUsername(requirements.getUsername(), salesmansList);
		if(requirements.getFirstName() != null && !requirements.getFirstName().isEmpty())
			salesmansList = byFirstName(requirements.getFirstName(), salesmansList);
		if(requirements.getLastName() != null && !requirements.getLastName().isEmpty())
			salesmansList = byLastName(requirements.getLastName(), salesmansList);
		if(requirements.getSortType() != null && !requirements.getSortType().isEmpty())
			sortBy(salesmansList, requirements.getSortType());
		if(requirements.getFilterType() != null && !requirements.getFilterType().isEmpty() && !requirements.getFilterType().toLowerCase().equals("empty"))
			salesmansList = filterBy(salesmansList, requirements.getFilterType());

		return salesmansList;
	}

	@Override
	public void sortBy(List<Salesman> salesmansList, String sortType) {
		if(sortType.equals("usernameAscending"))
			Collections.sort(salesmansList, compareByUsername);
		else if(sortType.equals("usernameDesceding"))
			Collections.sort(salesmansList, Collections.reverseOrder(compareByUsername));
		else if(sortType.equals("firstNameAsceding"))
			Collections.sort(salesmansList, compareByFirstName);
		else if(sortType.equals("firstNameDesceding"))
			Collections.sort(salesmansList, Collections.reverseOrder(compareByFirstName));
		else if(sortType.equals("lastNameAsceding"))
			Collections.sort(salesmansList, compareByLastName);
		else if(sortType.equals("lastNameDesceding"))
			Collections.sort(salesmansList, Collections.reverseOrder(compareByLastName));
	}
	
	public Comparator<Salesman> compareByUsername = (Salesman m1, Salesman m2) -> m1.getUsername().toLowerCase().compareTo(m2.getUsername().toLowerCase());
	public Comparator<Salesman> compareByFirstName = (Salesman m1, Salesman m2) -> m1.getFirstName().toLowerCase().compareTo(m2.getFirstName().toLowerCase());
	public Comparator<Salesman> compareByLastName = (Salesman m1, Salesman m2) -> m1.getLastName().toLowerCase().compareTo(m2.getLastName().toLowerCase());
	
	@Override
	public List<Salesman> filterBy(List<Salesman> salesmansList, String filterType) {
		System.out.println(filterType);
		Boolean isBlocked = false;
		if(filterType.toLowerCase().equals("blocked"))
			isBlocked = true;
		else if(filterType.toLowerCase().equals("present"))
			isBlocked = false;
		final Boolean isIt = isBlocked;
		return salesmansList.stream()
				 .filter(m -> m.getBlocked() == isIt)
			     .collect(Collectors.toList());
	}

	@Override
	public List<Salesman> byUsername(String username, List<Salesman> salesmansList) {
		return salesmansList.stream()
				 .filter(m -> m.getUsername().toLowerCase().contains(username.toLowerCase()))
				 .collect(Collectors.toList());
	}

	@Override
	public List<Salesman> byFirstName(String username, List<Salesman> salesmansList) {
		return salesmansList.stream()
				 .filter(m -> m.getFirstName().toLowerCase().contains(username.toLowerCase()))
				 .collect(Collectors.toList());
	}

	@Override
	public List<Salesman> byLastName(String username, List<Salesman> salesmansList) {
		return salesmansList.stream()
				 .filter(m -> m.getLastName().toLowerCase().contains(username.toLowerCase()))
				 .collect(Collectors.toList());
	}

}
