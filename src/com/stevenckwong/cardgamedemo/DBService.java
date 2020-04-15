package com.stevenckwong.cardgamedemo;

import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import com.newrelic.api.agent.Trace;

public class DBService {

	MongoClient client;
	DB database;
	DBCollection usersInDB;
	DBCollection gamesInDB;
	
	public DBService() {
		
	}
	
	@Trace
	public void connect() {
		client = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		database = client.getDB("CardGameDB");
		usersInDB = database.getCollection("Users");
		gamesInDB = database.getCollection("Games");
	}

	@Trace
	public void disconnect() {
		if (client!=null) {
			client.close();
			client=null;
		}
		
	}
	
	@Trace
	public void logGame(ThreeCardGame game) {
		if (client==null) {
			connect();
		}
		
		gamesInDB.insert(this.toDBObject(game));
		
		disconnect();
	}
	
	@Trace
	public void saveUser(User user) {
		// Implement Save user to DB here
		User existingUser = lookupUser(user);
		
		if (client==null) {
			connect();
		}
		
		if (existingUser == null) {
			usersInDB.insert(this.toDBObject(user));
		} else {
			existingUser.setAccountBalance(user.getAccountBalance());
			// System.out.println(existingUser.getEmail()+existingUser.getAccountBalance());
			usersInDB.update(new BasicDBObject("_id",user.getEmail()), this.toDBObject(existingUser));
			
		}
		
		disconnect();
		
	}
	
	@Trace
	public User lookupUser(User user) {
		if (client==null) {
			connect();
		}
		
		DBCursor cursor = usersInDB.find(new BasicDBObject("_id",user.getEmail()));
		if (cursor.count()==0) {
			// user not found
			disconnect();
			return null;
		} else {
			DBObject obj = cursor.next();
			cursor.close();
			disconnect();
			return fromDBObject(obj);
		}
		
		
	}

	@Trace
	public ArrayList<User> getAllUsers() {
		
		ArrayList<User> users = new ArrayList<User>();
		
		if (client==null) {
			connect();
		}
		
		DBCursor cursor = usersInDB.find();
		
		if (cursor.count()==0) {
			// user not found
			disconnect();
			return null;
		} else {
			
			for (int idx=0;idx<cursor.count();idx++) {
				DBObject obj = cursor.next();
				users.add(fromDBObject(obj));
			}
			
			cursor.close();
			disconnect();
			return users;
		}
		
	}	

	@Trace
	public void deleteUser(User user) {
		if (client==null) {
			connect();
		}
		usersInDB.remove(new BasicDBObject("_id",user.getEmail()));
		disconnect();
	}	
	
	public User fromDBObject(DBObject obj) {
		User aUser = new User(	(String)obj.get("firstName"),
								(String)obj.get("lastName"),
								(String)obj.get("emailAddress"),
								"nopassword",
								(int)obj.get("accountBalance"));
		
		
		return aUser;
	}
	
	public DBObject toDBObject(User user) {
		
		DBObject dbUser = new BasicDBObject("_id",user.getEmail());
		dbUser.put("firstName", user.getFirstName());
		dbUser.put("lastName", user.getLastName());
		dbUser.put("emailAddress", user.getEmail());
		dbUser.put("accountBalance", user.getAccountBalance());
		
		return dbUser;
	}
	
	public DBObject toDBObject(ThreeCardGame game) {
		
		DBObject dbGame = new BasicDBObject();
		dbGame.put("winner", game.getWinner());
		dbGame.put("commission", game.getCommissionCharged());
		dbGame.put("dealercards", String.format("%d - %d - %d", game.getDealerCard(1), 
																game.getDealerCard(2), 
																game.getDealerCard(3)));
		dbGame.put("playercards", String.format("%d - %d - %d", game.getPlayerCard(1), 
																game.getPlayerCard(2), 
																game.getPlayerCard(3)));
		dbGame.put("betpool", game.getBetPool());
		
		return dbGame;
	}

}

