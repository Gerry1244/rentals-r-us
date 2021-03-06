package com.libertymutual.goforcode.spark.app.controllers;

import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.JsonHelper;

import static spark.Spark.notFound;

import java.util.Map;

import org.javalite.activejdbc.LazyList;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentApiController {

	public static final Route details = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			String idAsString = req.params("id");
			int id = Integer.parseInt(idAsString);
			Apartment apartment = Apartment.findById(id);
			if (apartment != null) {
				res.header("Content-Type", "application/json");
				return apartment.toJson(true);
			}
			notFound("Did not find that apartment.");
			return "{}";
		}
	};
	public static final Route create = (Request req, Response res) -> {
		String json = req.body();
		Map map = JsonHelper.toMap(json);
		Apartment apartment = new Apartment();
		apartment.fromMap(map);
		
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			apartment.saveIt();
			res.status(201);
			return apartment.toJson(true);
		}
		
	};

	public static final Route mine = (Request req, Response res) -> {
		User currentUser = req.session().attribute("currentUser");
		long id = (long) currentUser.getId();
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			LazyList<Apartment> apartments = Apartment.where("user_id = ?", id);
			res.header("Content-Type", "application/json");
			return apartments.toJson(true);
		}
	};
	
	public static final Route index = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			LazyList<Apartment> apartments = Apartment.where("is_active = ?", true);
			res.header("Content-Type", "application/json");
			return apartments.toJson(true);
		}
	};
	
	public static final Route activate = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			String idAsString = req.params("id");
			int id = Integer.parseInt(idAsString);
			Apartment apartment = Apartment.findById(id);
			apartment.set("is_active", true);
			apartment.saveIt();
			res.header("Content-Type", "application/json");
			return apartment.toJson(true);
		}
	};

	public static final Route deactivate = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			String idAsString = req.params("id");
			int id = Integer.parseInt(idAsString);
			Apartment apartment = Apartment.findById(id);
			apartment.set("is_active", false);
			apartment.saveIt();
			res.header("Content-Type", "application/json");
			return apartment.toJson(true);
		}
	};

	public static final Route like = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			String idAsString = req.params("id");
			int id = Integer.parseInt(idAsString);
			Apartment apartment = Apartment.findById(id);
			User currentUser = req.session().attribute("currentUser");
			apartment.add(currentUser);
			res.header("Content-Type", "application/json");
			return apartment.toJson(true);
		}
	};

}