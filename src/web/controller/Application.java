package web.controller;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFiles;

import java.io.File;
import java.lang.reflect.Type;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import dao.impl.AdministratorDaoImpl;
import dao.impl.CancelationDaoImpl;
import dao.impl.CommentDaoImpl;
import dao.impl.CustomerDaoImpl;
import dao.impl.ManifestationDaoImpl;
import dao.impl.SalesmanDaoImpl;
import dao.impl.ShoppingCartDaoImpl;
import dao.impl.TicketDaoImpl;
import dao.impl.UserDaoImpl;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


public class Application {

	public static Gson g = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
	        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
	                throws JsonParseException {
	            return new Date(json.getAsJsonPrimitive().getAsLong());
	        }
    }).registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(date.getTime()))
	  .create();
	
	public static Gson gsonDate = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
		@Override
		public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
			String dateStr = element.getAsString();
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	        try {
	            return format.parse(dateStr);
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        return null;
		}}).create(); 
	
	public static final AdministratorDaoImpl administratorDao = new AdministratorDaoImpl();
	public static final CustomerDaoImpl customerDao = new CustomerDaoImpl();
	public static final SalesmanDaoImpl salesmanDao = new SalesmanDaoImpl();
	public static final UserDaoImpl userDao = new UserDaoImpl(administratorDao, customerDao, salesmanDao);

	public static final ManifestationDaoImpl manifestationDao = new ManifestationDaoImpl();
	public static final TicketDaoImpl ticketDao = new TicketDaoImpl();
	public static final CancelationDaoImpl cancelationDao = new CancelationDaoImpl();
	
	
	public static final ShoppingCartDaoImpl cartDao = new ShoppingCartDaoImpl();
	public static final CommentDaoImpl commentDao = new CommentDaoImpl();
	
	public static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	
	public static void main(String[] args) throws Exception {	
		port(8080);

		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
		path("/rest", () -> {
			
			post("/registration",	AuthController.handleRegistration);
			post("/login", 			AuthController.handleLogin);

			get("/:username",							UserController.getUser);
			get("/:username/tickets", 					TicketController.getTicketsForUser);
			get("/can-comment/:manifestationid",		TicketController.checkCanUserComment);
			get("/:username/manifestations", 			ManifestationController.getManifestationsForUser);
			//get("/:username/reserved-tickets",	UserController.getReservedTicketsForUser);
			
			path("/tickets", () -> {
				get("/",       			TicketController.getAllTickets);
				get("/:id", 			TicketController.getOneTicket);
				get("/salesman", 		TicketController.getTicketsForSalesman);

				post("/",				TicketController.createTicket);
				post("/ssf", 			TicketController.handleSearchSortFilter);
				put("/:id/cancel",		TicketController.cancelTicket);
				//delete("/:id", 			TicketController.deleteTicket);				
			});
			
			path("/comments", () -> {
				get("/admin/:manifestationId",		CommentController.getCommentsAdmin);
				get("/customer/:manifestationId", 	CommentController.getCommentsCustomer);
				get("/salesman/:manifestationId",	CommentController.getCommentsSalesman);	
				post("/"						,	CommentController.addCommentCustomer);
				put("/change-status"			,	CommentController.changeCommentStatus);
			});		
			
			path("/manifestations", () -> {
				get("/",       				ManifestationController.getAllManifestations);
				get("/:id", 				ManifestationController.getOneManifestation);
				get("/page/:number",		ManifestationController.getPage);
				post("/",					ManifestationController.createManifestation);
				put("/:id", 				ManifestationController.editManifestation);
				delete("/:id", 				ManifestationController.deleteManifestation);
				get("/salesman/:username",  ManifestationController.getManifestationsForUser);
				put("/:id/approve",			ManifestationController.approveManifestaion);
				post("/ssf/:number",		ManifestationController.handleSearchSortFilter);
				get("/rating/:id",		    ManifestationController.getRating);
			});
			
			path("/cart", () -> {
				get("/", 			ShoppingCartController.getCart);
				post("/", 			ShoppingCartController.addItemToCart);
				post("/submit", 	ShoppingCartController.submitCart);
			});
				
			path("/salesmans", () -> {
				get("/", 				SalesmanController.getAllSalesmans);
				post("/", 				SalesmanController.createSalesman);
				post("/ssf", 			SalesmanController.handleSearchSortFilter);
				put("/:username", 		SalesmanController.editSalesman);
				delete("/:username",	SalesmanController.deleteSalesman);
			});
			
			path("/customers", () -> {
				get("/", 				CustomerController.getAllCustomers);
				get("/suspicious",		CustomerController.getSuspiciousCustomers);
				get("/:username", 		CustomerController.getOneCustomer);
				put("/:username",		CustomerController.blockCustomer);
				post("/:username", 		CustomerController.editCustomer);
				delete("/:username", 	CustomerController.deleteCustomer);
				post("/ssf/ssf", 		CustomerController.handleSearchSortFilter);
			});

		});
	}
}
