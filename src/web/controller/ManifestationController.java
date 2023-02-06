package web.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import dao.AdministratorDao;
import dao.CommentDao;
import dao.CustomerDao;
import dao.ManifestationDao;
import dao.SalesmanDao;
import model.Comment;
import model.Manifestation;
import model.Salesman;
import model.User;
import model.enums.CommentStatus;
import model.enums.ManifastationStatus;
import spark.Route;
import web.dto.ErrorDTO;
import web.dto.ManifestationSSFDTO;

public class ManifestationController {

		public static final AdministratorDao administratorDao = Application.administratorDao;
		public static final SalesmanDao salesmanDao = Application.salesmanDao;	
		public static final CustomerDao customerDao = Application.customerDao;	
		public static final CommentDao commentDao = Application.commentDao;
		public static final ManifestationDao manifestationDao = Application.manifestationDao;
		
		public static final Gson g = Application.g;
		public static final Gson gsonDate = Application.gsonDate;
		
		
		public static Route getAllManifestations = (request, response) -> {
			List<Manifestation> manifestations = manifestationDao.findAllExcludingDeleted();
			manifestations.sort((m1, m2) -> m1.getDateTime().compareTo(m2.getDateTime()));
			response.type("application/json");
			return g.toJson(manifestations);
		};
		
		public static Route createManifestation = (request, response) -> {
			response.type("application/json");

			User current = AuthController.getCurrentUser(request.headers("Authorization"));			
			if (current == null)
				return g.toJson(new ErrorDTO("You have no permission to create manifestation!", "AuthException", 100));
			if (administratorDao.findOne(current.getUsername()) == null && salesmanDao.findOne(current.getUsername()) == null)
				return g.toJson(new ErrorDTO("You have no permission to create manifestation!", "AuthException", 100));
			
			String payload = request.body();
			int counter = manifestationDao.findAll().size();
			Manifestation manifestation = g.fromJson(payload, Manifestation.class);			
			manifestation.setInitialTicketsCount();
			manifestation.setId(counter+1);
			manifestation.setSeats(0);
			manifestation.setTickets(new ArrayList<Integer>());
			
			if(!manifestationDao.checkTimeLocation(manifestation))
				return g.toJson(new ErrorDTO("Manifestation already exists on entered date and time!", "AuthException", 100));
			
			manifestationDao.add(manifestation);
			Salesman s = salesmanDao.findOne(current.getUsername());
			s.addManifestation(manifestation.getId());
			salesmanDao.add(s);
			return g.toJson(manifestation);
		};
		
		public static Route handleSearchSortFilter = (request, response) -> {
			User current = AuthController.getCurrentUser(request.headers("Authorization"));
			String payload = request.body();
			Integer page = Integer.parseInt(request.params("number"));
			Integer toIndex = page * 8;
			System.out.println(payload);
			ManifestationSSFDTO manifestation = gsonDate.fromJson(payload, ManifestationSSFDTO.class);
			System.out.println(manifestation);
			Salesman salesman = null;
			if(current != null)
				salesman = salesmanDao.findOne(current.getUsername());
			List<Manifestation> results = manifestationDao.ssfManifestations(manifestation, salesman);
			response.type("application/json");
			
			if(page != -1) {
				if(toIndex > results.size()) {
					toIndex = results.size();
					if(toIndex <= (page-1)*8)
						return g.toJson(null);
				}
				results = results.subList((page-1)*8, toIndex);
			}
			return g.toJson(results);
			// results.forEach(r -> System.out.println(r.getTitle()));
			// results.forEach(r -> System.out.println(r.getDateTime()));
		};
		
		public static Route getOneManifestation = (request, response) -> {
			response.type("application/json");

			Integer id = Integer.parseInt(request.params("id"));
			Manifestation manifestation = manifestationDao.findOne(id);
			if (manifestation == null)
				return g.toJson(new ErrorDTO("Manifestation does not exist!", "ExistentException", 200));
			
			return g.toJson(manifestation);
		};
		
		public static Route editManifestation = (request, response) -> {
			response.type("application/json");

			User current = AuthController.getCurrentUser(request.headers("Authorization"));			
			if (current == null)
				return g.toJson(new ErrorDTO("You have no permission to edit manifestation!", "AuthException", 100));
			if (administratorDao.findOne(current.getUsername()) == null && salesmanDao.findOne(current.getUsername()) == null)
				return g.toJson(new ErrorDTO("You have no permission to edit manifestation!", "AuthException", 100));
			
			System.out.println(request.params("id"));
			Integer id = Integer.parseInt(request.params("id"));
			if (manifestationDao.findOne(id) == null)
				return g.toJson(new ErrorDTO("Manifestation does not exist!", "ExistentException", 200));
			
			String payload = request.body();
			System.out.println(payload);
			Manifestation newManifestation = g.fromJson(payload, Manifestation.class);
			manifestationDao.edit(id, newManifestation);
			return g.toJson(newManifestation);
		};
		
		public static Route deleteManifestation = (request, response) -> {
			response.type("application/json");

			User current = AuthController.getCurrentUser(request.headers("Authorization"));
			if (current == null)
				return g.toJson(new ErrorDTO("You have no permission to delete manifestation!", "AuthException", 100));
			if (administratorDao.findOne(current.getUsername()) == null)
				return g.toJson(new ErrorDTO("You have no permission to delete manifestation!", "AuthException", 100));

			Integer id = Integer.parseInt(request.params("id"));
			Manifestation manifestation = manifestationDao.findOne(id);
			if (manifestation == null)
				return g.toJson(new ErrorDTO("Manifestation does not exist!", "ExistentException", 200));
			
			manifestation.setDeleted(true);
			return g.toJson(manifestation);
		};
		
		public static Route approveManifestaion = (request, response) -> {
			response.type("application/json");

			User current = AuthController.getCurrentUser(request.headers("Authorization"));
			if (current == null)
				return g.toJson(new ErrorDTO("You have no permission to delete manifestation!", "AuthException", 100));
			if (administratorDao.findOne(current.getUsername()) == null)
				return g.toJson(new ErrorDTO("You have no permission to approve manifestation!", "AuthException", 100));
			
			Integer id = Integer.parseInt(request.params("id"));
			Manifestation manifestation = manifestationDao.findOne(id);
			if (manifestation == null)
				return g.toJson(new ErrorDTO("Manifestation does not exist!", "ExistentException", 200));
			
			manifestation.setStatus(ManifastationStatus.ACTIVE);
			manifestationDao.saveChanges();
			return g.toJson(manifestation);
		};
		
		public static Route getManifestationsForUser = (request, response) -> {
			response.type("application/json");

			User current = AuthController.getCurrentUser(request.headers("Authorization"));
			if (current == null)
				return g.toJson(new ErrorDTO("User is not logged in!", "AuthException", 100));

			String username = request.params("username");
			Salesman salesman = salesmanDao.findOne(current.getUsername());
			if(salesman == null || !salesman.getUsername().equals(username))
				return g.toJson(new ErrorDTO("Permission denied!", "AuthException", 100));
			
			List<Manifestation> manifestations = manifestationDao.getManifestationsForSalesman(salesman);
			return g.toJson(manifestations);
		};
		
		public static Route getPage = (request, response) -> {
			response.type("application/json");
			Integer page = Integer.parseInt(request.params("number"));
			Integer toIndex = page * 8;
			List<Manifestation> manifestations = manifestationDao.findAllExcludingDeleted();//.subList((page-1)*8, page*8);
			if(toIndex > manifestations.size()) {
				toIndex = manifestations.size();
				if(toIndex <= (page-1)*8)
					return g.toJson(null);
			}
			manifestations = manifestations.subList((page-1)*8, toIndex);
			manifestations.sort((m1, m2) -> m1.getDateTime().compareTo(m2.getDateTime()));
			return g.toJson(manifestations);
		};
		
		public static Route getRating = (request, response) -> {
			response.type("application/json");
			Integer id = Integer.parseInt(request.params("id"));
			List<Comment> comments = commentDao.findAllForManifestation(id);
			
			double rating = 0;
			int counter = 0;
			for(Comment c: comments) {
				if(c.getStatus() == CommentStatus.ACCEPTED) {
					rating += c.getRating();
					counter++;
				}
			}
			if(rating > 0)
				rating = rating / counter;
			
			return g.toJson(rating);
		};
}
