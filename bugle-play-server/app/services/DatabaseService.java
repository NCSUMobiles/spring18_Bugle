package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import models.Applicants;
import models.Chats;
import models.Events;
import models.Messages;
import models.Users;
import play.Logger;
import play.Logger.ALogger;
import play.db.Database;
import util.Strings;

/**
 * @author Sumit Srivastava
 *
 */
public class DatabaseService {

	private static final ALogger LOG = Logger.of(DatabaseService.class);
	private Database db;
	private DatabaseExecutionContext executionContext;

	@Inject
	public DatabaseService(Database db, DatabaseExecutionContext executionContext) {
		LOG.debug("Initializing Database service");
		System.out.println("DBSERVICE");
		this.db = db;
		this.executionContext = executionContext;
		initializeDatabase();
		LOG.debug("Initialized Database service");
	}

	/**
	 * This method initializes the database if it doesn't already exist.
	 */
	public void initializeDatabase() {
		LOG.debug("Initializing database...");
		Connection con = null;
		try {
			con = db.getConnection();
			// Initialize database here.
			String createUsers = "CREATE TABLE IF NOT EXISTS users (u_id SERIAL PRIMARY KEY, u_name text NOT NULL, email text NOT NULL, mobile text, dob text, password text NOT NULL, type text NOT NULL, description text, location text, website text, gprofid text)";
			String createEvents = "CREATE TABLE IF NOT EXISTS events (e_id SERIAL PRIMARY KEY, e_name text NOT NULL, location text, datetime text, description text, members text, u_id integer, status text)";
			String createApplicants = "CREATE TABLE IF NOT EXISTS applicants (a_id SERIAL PRIMARY KEY, u_id integer NOT NULL, e_id integer NOT NULL, status text)";
			String createChats = "CREATE TABLE IF NOT EXISTS chats (c_id SERIAL PRIMARY KEY, c_name text NOT NULL, u_id integer, e_id integer, status text, m_id integer)";
			String createMessages = "CREATE TABLE IF NOT EXISTS messages (m_id SERIAL PRIMARY KEY, e_id integer, msg text, status text, unique(e_id))";
			String createFunction = "CREATE OR REPLACE FUNCTION update_mId() RETURNS trigger AS ' BEGIN   IF NEW.m_id IS NULL THEN    NEW.m_id := (select m_id from chats where e_id = NEW.e_id and m_id IS NOT NULL LIMIT 1);   END IF;   RETURN NEW; END' LANGUAGE 'plpgsql'";
			String dropTrigger = "DROP TRIGGER IF EXISTS update_chat_mId on \"chats\"";
			String createTrigger = "CREATE TRIGGER update_chat_mId before insert on chats for each row execute procedure update_mId()";
			try (Statement stmt = con.createStatement()) {
				LOG.debug("Creating users table...");
				stmt.execute(createUsers);
				LOG.debug("Creating events table...");
				stmt.execute(createEvents);
				LOG.debug("Creating applicants table...");
				stmt.execute(createApplicants);
				LOG.debug("Creating chats table...");
				stmt.execute(createChats);
				LOG.debug("Creating messages table...");
				stmt.execute(createMessages);
				LOG.debug("Creating trigger function...");
				stmt.execute(createFunction);
				LOG.debug("Dropping chats trigger(if exists)...");
				stmt.execute(dropTrigger);
				LOG.debug("Creating chats trigger...");
				stmt.execute(createTrigger);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Error while executing query for initializing database.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error while getting DB connection for initializing database.");
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the database connection from initialize database method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Initialized database.");
	}

	public boolean mockDatabase() {
		LOG.debug("Generating mock data...");
		List<String> insertStatements = new ArrayList<String>();
		// users-organizations
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, password, type, description, location, website) values ('Organization 1','org1@org.com','1236987894','o1','org','This is a very big Volunteering organization based in US. For more information visit: xyz.org1.com','Raleigh','xyz.org1.com')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, password, type, description, location, website) values ('Organization 2','org2@org.com','1236547234','o2','org','This is the biggest Volunteering organization based in Atlanta, US. For more information visit: xyz.org2.com','Atlanta','xyz.org2.com')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, password, type, description, location, website) values ('Organization 3','org3@org.com','1236547894','o3','org','This is the third volunteer organization. We do volunteer work for this and that. For more information visit: xyz.org3.com','Chicago','xyz.org3.com')");
		// users-volunteers
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type, description) values ('John Smith','usr1@vol.com','1232233421','08/21/1993','v1','vol', 'Reliable, Dependable and always on time. I would love to volunteer for you!')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type, description) values ('Peter Parker','usr2@vol.com','1231231233','01/19/1990','v2','vol', 'I am an enthusiastic Volunteer and I have volunteered for many events.')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type, description) values ('Phil Coulson','usr3@vol.com','1233213458','01/18/1995','v3','vol', 'I am a very good person and I volunteer.')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type, description) values ('Harry Potter','usr4@vol.com','123764543','09/24/1985','v4','vol', 'I like to volunteer for food banks and farms.')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type, description) values ('Melinda May','usr5@vol.com','123987456','09/30/1989','v5','vol', 'Full Time Website developer looking for some volunteer work.')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type, description) values ('Will Smith','usr6@vol.com','1239873458','11/11/1990','v6','vol', 'Teacher looking for volunteering opportunities.')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type, description) values ('Daisy Johnson','usr7@vol.com','1230978345','08/16/1994','v7','vol', 'I am a lawyer and I love to volunteer.')");
		// events
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 1','Raleigh','05/24/2018 11:00AM','description of a volunteering event!! come volunteer with us','12',(select u_id from users where u_name='Organization 1' limit 1),'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 28','Colorado','07/19/2018 01:00PM','description of an exciting volunteering event!! come volunteer with us','22',(select u_id from users where u_name='Organization 1' limit 1),'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 123','San Jose','06/11/2018 10:00AM','description this is a volunteering event!! come volunteer with us in San Jose.','50',(select u_id from users where u_name='Organization 1' limit 1),'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 234','New York','05/16/2018 03:00PM','description welcome to the volunteering event!! come volunteer with us','10',(select u_id from users where u_name='Organization 2' limit 1),'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 23','Atlanta','07/01/2018 09:00AM','description volunteering event!! come volunteer with us in Atlanta','5',(select u_id from users where u_name='Organization 2' limit 1),'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 47','Florida','05/15/2018 11:00AM','description very interesting volunteering event!! come volunteer with us','7',(select u_id from users where u_name='Organization 3' limit 1),'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 74','Chicago','04/21/2018 08:00AM','description volunteering event. Big volunteering event need lots of volunteers!! come volunteer with us!','100',(select u_id from users where u_name='Organization 3' limit 1),'active')");
		// applicants
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='John Smith' limit 1), (select e_id from events where e_name='Event 1' limit 1), 'approved')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='Will Smith' limit 1), (select e_id from events where e_name='Event 28' limit 1), 'applied')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='Peter Parker' limit 1), (select e_id from events where e_name='Event 1' limit 1), 'applied')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='Phil Coulson' limit 1), (select e_id from events where e_name='Event 23' limit 1), 'applied')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='Harry Potter' limit 1), (select e_id from events where e_name='Event 234' limit 1), 'applied')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='Melinda May' limit 1), (select e_id from events where e_name='Event 1' limit 1), 'applied')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='John Smith' limit 1), (select e_id from events where e_name='Event 74' limit 1), 'applied')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='Daisy Johnson' limit 1), (select e_id from events where e_name='Event 1' limit 1), 'applied')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='Peter Parker' limit 1), (select e_id from events where e_name='Event 47' limit 1), 'applied')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='Phil Coulson' limit 1), (select e_id from events where e_name='Event 234' limit 1), 'applied')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='Harry Potter' limit 1), (select e_id from events where e_name='Event 74' limit 1), 'applied')");
		insertStatements.add(
				"INSERT INTO applicants (u_id, e_id, status) values ((select u_id from users where u_name='Melinda May' limit 1), (select e_id from events where e_name='Event 123' limit 1), 'applied')");
		// messages - inserting default messages
		insertStatements.add(
				"INSERT INTO messages (e_id, msg, status) values ((SELECT e_id from events where e_name='Event 1' LIMIT 1),'<p style=\"text-align: center !important; font-weight: bold !important;\">Welcome to Event 1: Chat<p><br>','active')");
		insertStatements.add(
				"INSERT INTO messages (e_id, msg, status) values ((SELECT e_id from events where e_name='Event 28' LIMIT 1),'<p style=\"text-align: center !important; font-weight: bold !important;\">Welcome to Event 28: Chat<p><br>','active')");
		insertStatements.add(
				"INSERT INTO messages (e_id, msg, status) values ((SELECT e_id from events where e_name='Event 123' LIMIT 1),'<p style=\"text-align: center !important; font-weight: bold !important;\">Welcome to Event 123: Chat<p><br>','active')");
		insertStatements.add(
				"INSERT INTO messages (e_id, msg, status) values ((SELECT e_id from events where e_name='Event 234' LIMIT 1),'<p style=\"text-align: center !important; font-weight: bold !important;\">Welcome to Event 234: Chat<p><br>','active')");
		insertStatements.add(
				"INSERT INTO messages (e_id, msg, status) values ((SELECT e_id from events where e_name='Event 23' LIMIT 1),'<p style=\"text-align: center !important; font-weight: bold !important;\">Welcome to Event 23: Chat<p><br>','active')");
		insertStatements.add(
				"INSERT INTO messages (e_id, msg, status) values ((SELECT e_id from events where e_name='Event 47' LIMIT 1),'<p style=\"text-align: center !important; font-weight: bold !important;\">Welcome to Event 47: Chat<p><br>','active')");
		insertStatements.add(
				"INSERT INTO messages (e_id, msg, status) values ((SELECT e_id from events where e_name='Event 74' LIMIT 1),'<p style=\"text-align: center !important; font-weight: bold !important;\">Welcome to Event 74: Chat<p><br>','active')");
		// chats - organizers
		insertStatements.add(
				"INSERT INTO chats (c_name, u_id, e_id, status, m_id) values ((('Event 1')::text || ': Chat'), (SELECT u_id from events where e_name='Event 1' order by u_id LIMIT 1), (SELECT e_id from events where e_name='Event 1' LIMIT 1), 'active', (SELECT m_id FROM messages where e_id IN (SELECT e_id from events where e_name='Event 1' LIMIT 1)))");
		insertStatements.add(
				"INSERT INTO chats (c_name, u_id, e_id, status, m_id) values ((('Event 28')::text || ': Chat'), (SELECT u_id from events where e_name='Event 28' order by u_id LIMIT 1), (SELECT e_id from events where e_name='Event 28' LIMIT 1), 'active', (SELECT m_id FROM messages where e_id IN (SELECT e_id from events where e_name='Event 28' LIMIT 1)))");
		insertStatements.add(
				"INSERT INTO chats (c_name, u_id, e_id, status, m_id) values ((('Event 123')::text || ': Chat'), (SELECT u_id from events where e_name='Event 123' order by u_id LIMIT 1), (SELECT e_id from events where e_name='Event 123' LIMIT 1), 'active', (SELECT m_id FROM messages where e_id IN (SELECT e_id from events where e_name='Event 123' LIMIT 1)))");
		insertStatements.add(
				"INSERT INTO chats (c_name, u_id, e_id, status, m_id) values ((('Event 234')::text || ': Chat'), (SELECT u_id from events where e_name='Event 234' order by u_id LIMIT 1), (SELECT e_id from events where e_name='Event 234' LIMIT 1), 'active', (SELECT m_id FROM messages where e_id IN (SELECT e_id from events where e_name='Event 234' LIMIT 1)))");
		insertStatements.add(
				"INSERT INTO chats (c_name, u_id, e_id, status, m_id) values ((('Event 23')::text || ': Chat'), (SELECT u_id from events where e_name='Event 23' order by u_id LIMIT 1), (SELECT e_id from events where e_name='Event 23' LIMIT 1), 'active', (SELECT m_id FROM messages where e_id IN (SELECT e_id from events where e_name='Event 23' LIMIT 1)))");
		insertStatements.add(
				"INSERT INTO chats (c_name, u_id, e_id, status, m_id) values ((('Event 47')::text || ': Chat'), (SELECT u_id from events where e_name='Event 47' order by u_id LIMIT 1), (SELECT e_id from events where e_name='Event 47' LIMIT 1), 'active', (SELECT m_id FROM messages where e_id IN (SELECT e_id from events where e_name='Event 47' LIMIT 1)))");
		insertStatements.add(
				"INSERT INTO chats (c_name, u_id, e_id, status, m_id) values ((('Event 74')::text || ': Chat'), (SELECT u_id from events where e_name='Event 74' order by u_id LIMIT 1), (SELECT e_id from events where e_name='Event 74' LIMIT 1), 'active', (SELECT m_id FROM messages where e_id IN (SELECT e_id from events where e_name='Event 74' LIMIT 1)))");
		// chats - approved volunteers
		insertStatements.add(
				"INSERT INTO chats (c_name, u_id, e_id, status) values ((('Event 1')::text || ': Chat'), (SELECT u_id from applicants where e_id in (SELECT e_id from events where e_name='Event 1' LIMIT 1) order by u_id LIMIT 1), (SELECT e_id from events where e_name='Event 1' LIMIT 1), 'active')");
		Connection con = null;
		try {
			con = db.getConnection();
			boolean status = true;
			LOG.debug("Inserting records...");
			for (String insertStatement : insertStatements) {
				try (PreparedStatement pstmt = con.prepareStatement(insertStatement)) {
					int recordsInserted = pstmt.executeUpdate();
					status = recordsInserted > 0;
				} catch (Exception e) {
					LOG.error("Error while inserting records, for Insert statement: " + insertStatement);
					e.printStackTrace();
				}
			}
			LOG.debug("Mock data generation complete!");
			return status;
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for generating mock data.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from generate mock data method");
					e.printStackTrace();
				}
			}
		}
	}

	public boolean resetDatabase() {
		LOG.debug("Resetting Database...");
		Connection con = null;
		try {
			con = db.getConnection();
			String resetChats = "DELETE FROM chats";
			String resetApplicants = "DELETE FROM applicants";
			String resetEvents = "DELETE FROM events";
			String resetUsers = "DELETE FROM users";
			String resetMessages = "DELETE FROM messages";
			try (Statement stmt = con.createStatement()) {
				boolean status = stmt.executeUpdate(resetChats) >= 0 && stmt.executeUpdate(resetApplicants) >= 0
						&& stmt.executeUpdate(resetEvents) >= 0 && stmt.executeUpdate(resetUsers) >= 0
						&& stmt.executeUpdate(resetMessages) >= 0;
				LOG.debug("Database reset status: " + status);
				return status;
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Error while executing query for resetting database.");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error while getting DB connection for resetting database.");
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the database connection from reset database method.");
					e.printStackTrace();
				}
			}
		}
	}

	public boolean insertUser(Users user) {
		LOG.debug("Inserting user");
		String insertStatement = "INSERT INTO users (u_name, email, mobile, dob, password, type, description, location, website) VALUES(?,?,?,?,?,?,?,?,?)";
		Connection con = null;
		try {
			con = db.getConnection();
			PreparedStatement pstmt = con.prepareStatement(insertStatement);
			pstmt.setString(1, user.getuName());
			pstmt.setString(2, user.getEmail());
			pstmt.setString(3, user.getMobile());
			pstmt.setString(4, user.getDob());
			pstmt.setString(5, user.getPassword());
			pstmt.setString(6, user.getType());
			pstmt.setString(7, user.getDescription());
			pstmt.setString(8, user.getLocation());
			pstmt.setString(9, user.getWebsite());
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			LOG.error("Error while inserting user.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from insert user method");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Method to validate user login information
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	public Users validateLogin(String email, String password) {
		LOG.debug("Validating Login for: " + email);
		Users loginUser = null;
		String selectQuery = "SELECT * from users where email = ? and password = ?";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setString(1, email);
				selectStatement.setString(2, password);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					loginUser = new Users();
					loginUser.setuId(rs.getInt("u_id"));
					loginUser.setuName(rs.getString("u_name"));
					loginUser.setEmail(rs.getString("email"));
					loginUser.setMobile(rs.getString("mobile"));
					loginUser.setDob(rs.getString("dob"));
					loginUser.setPassword(rs.getString("password"));
					loginUser.setType(rs.getString("type"));
					loginUser.setDescription(rs.getString("description"));
					loginUser.setWebsite(rs.getString("website"));
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for validating Login.");
				e.printStackTrace();
				return loginUser;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for validating Login.");
			e.printStackTrace();
			return loginUser;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from validate Login method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Validated User login.");
		return loginUser;
	}

	public List<Users> getOrganizations() {
		LOG.debug("Fetching Organizations.");
		List<Users> organizations = new ArrayList<Users>();
		String selectQuery = "SELECT * from users where type = ?";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setString(1, Strings.ORG);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					Users user = new Users();
					user.setuId(rs.getInt("u_id"));
					user.setuName(rs.getString("u_name"));
					user.setEmail(rs.getString("email"));
					user.setMobile(rs.getString("mobile"));
					user.setDob(rs.getString("dob"));
					user.setPassword(rs.getString("password"));
					user.setType(rs.getString("type"));
					user.setDescription(rs.getString("description"));
					user.setWebsite(rs.getString("website"));
					organizations.add(user);
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for fetching Organizations.");
				e.printStackTrace();
				return organizations;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for fetching Organizations.");
			e.printStackTrace();
			return organizations;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from get Organizations method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Fetched Organizations.");
		return organizations;
	}

	public boolean insertEvent(Events event) {
		LOG.debug("Inserting Event and creating its correspondign chat group");
		String insertEvent = "INSERT INTO events (e_name, location, datetime, description, members, u_id, status) VALUES(?,?,?,?,?,?,?) RETURNING e_id";
		// when an event is created, its chat group should also be created.
		// then its messages entry should be created.
		String insertMessage = "INSERT INTO messages (e_id, msg, status) VALUES(?,?,?) RETURNING m_id";
		// then its chat group should be created.
		String insertChat = "INSERT INTO chats (c_name, u_id, e_id, m_id, status) VALUES(?,?,?,?,?)";
		Connection con = null;
		try {
			con = db.getConnection();
			LOG.debug("Inserting event...");
			PreparedStatement psEvent = con.prepareStatement(insertEvent);
			psEvent.setString(1, event.geteName());
			psEvent.setString(2, event.getLocation());
			psEvent.setString(3, event.getDatetime());
			psEvent.setString(4, event.getDescription());
			psEvent.setString(5, event.getMembers());
			psEvent.setInt(6, event.getuId());
			psEvent.setString(7, event.getStatus());
			int eId = 0;
			psEvent.execute();
			ResultSet rs = psEvent.getResultSet();
			if (rs.next()) {
				eId = rs.getInt(1);
			}
			if (eId > 0) {
				LOG.debug("Event inserted! Event ID is: " + eId);
				LOG.debug("Inserting message...");
				PreparedStatement psMessage = con.prepareStatement(insertMessage);
				psMessage.setInt(1, eId);
				psMessage.setString(2, "<p>Welcome to " + event.geteName() + ": Chat<p><br>");
				psMessage.setString(3, Strings.STATUS_ACTIVE);
				int mId = 0;
				psMessage.execute();
				ResultSet rs3 = psMessage.getResultSet();
				if (rs3.next()) {
					mId = rs3.getInt(1);
				}
				if (mId > 0) {
					LOG.debug("Message inserted! Message ID is: " + mId);
					LOG.debug("Inserting chat...");
					PreparedStatement psChat = con.prepareStatement(insertChat);
					psChat.setString(1, event.geteName().concat(": Chat"));
					psChat.setInt(2, event.getuId());
					psChat.setInt(3, eId);
					psChat.setInt(4, mId);
					psChat.setString(5, Strings.STATUS_ACTIVE);
					return psChat.executeUpdate() > 0;
				} else {
					// There is a chance of having corrupted event inserted here if chat could not
					// be inserted after event insertion. Should delete the inserted event here?
					LOG.debug("Could not Insert Message!! aborting... mID:" + mId);
					return false;
				}
			} else {
				LOG.debug("Could not Insert Event!! aborting... eID:" + eId);
				return false;
			}

		} catch (Exception e) {
			LOG.error("Error while inserting event and corresponding chat group.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from insert Event method");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * returns list of all events created by an organization with given user id
	 * 
	 * @param orgId
	 * @return
	 */
	public List<Events> getEvents(int uId) {
		LOG.debug("Fetching Events for: orgID: " + uId);
		List<Events> events = new ArrayList<Events>();
		String selectQuery = "SELECT * from events where u_id = ? and status='active'";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setInt(1, uId);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					Events event = new Events();
					event.seteId(rs.getInt("e_id"));
					event.seteName(rs.getString("e_name"));
					event.setLocation(rs.getString("location"));
					event.setDatetime(rs.getString("datetime"));
					event.setDescription(rs.getString("description"));
					event.setMembers(rs.getString("members"));
					event.setuId(rs.getInt("u_id"));
					event.setStatus(rs.getString("status"));
					events.add(event);
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for fetching events.");
				e.printStackTrace();
				return events;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for fetching events.");
			e.printStackTrace();
			return events;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from get Events method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Fetched Events.");
		return events;
	}

	/**
	 * returns a list of all applicants for an event
	 * 
	 * @param eId
	 *            the event ID.
	 * @return
	 */
	public List<Users> getEventApplicants(int eId) {
		LOG.debug("Fetching Event applicants for event ID: " + eId);
		List<Users> users = new ArrayList<Users>();
		String selectQuery = "SELECT * FROM users WHERE u_id IN (SELECT u_id from applicants where status = 'applied' and e_id = ?)";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setInt(1, eId);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					Users user = new Users();
					user.setuId(rs.getInt("u_id"));
					user.setuName(rs.getString("u_name"));
					user.setEmail(rs.getString("email"));
					user.setMobile(rs.getString("mobile"));
					user.setDob(rs.getString("dob"));
					user.setPassword(rs.getString("password"));
					user.setType(rs.getString("type"));
					user.setDescription(rs.getString("description"));
					user.setWebsite(rs.getString("website"));
					users.add(user);
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for fetching event's applicants.");
				e.printStackTrace();
				return users;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for fetching event's applicants.");
			e.printStackTrace();
			return users;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from get Event Applicants method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Fetched applicants for the event.");
		return users;
	}

	/**
	 * return a list of all the events applied to by a volunteer
	 * 
	 * @param uId
	 *            the user ID of the volunteer
	 * @return
	 */
	public List<Events> getApplicantEvents(int uId) {
		LOG.debug("Fetching Applicant's Events for user ID: " + uId);
		List<Events> events = new ArrayList<Events>();
		String selectQuery = "SELECT * from events where e_id IN (SELECT e_id from applicants where u_id = ?)";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setInt(1, uId);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					Events event = new Events();
					event.seteId(rs.getInt("e_id"));
					event.seteName(rs.getString("e_name"));
					event.setLocation(rs.getString("location"));
					event.setDatetime(rs.getString("datetime"));
					event.setDescription(rs.getString("description"));
					event.setMembers(rs.getString("members"));
					event.setuId(rs.getInt("u_id"));
					event.setStatus(rs.getString("status"));
					events.add(event);
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for fetching applicant's events.");
				e.printStackTrace();
				return events;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for fetching applicant's events.");
			e.printStackTrace();
			return events;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from get Applicant Events method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Fetched Events for the applicant.");
		return events;
	}

	/**
	 * adds new entry in applicant table.
	 * 
	 * @param eId
	 *            the event ID
	 * @param uId
	 *            the user ID
	 * @return
	 */
	public boolean insertApplicant(Applicants applicant) {
		LOG.debug("Inserting Applicant");
		String insertStatement = "INSERT INTO applicants (u_id, e_id, status) VALUES(?,?,?)";
		Connection con = null;
		try {
			con = db.getConnection();
			PreparedStatement pstmt = con.prepareStatement(insertStatement);
			pstmt.setInt(1, applicant.getuId());
			pstmt.setInt(2, applicant.geteId());
			pstmt.setString(3, applicant.getStatus());
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			LOG.error("Error while inserting Applicant.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from insert Applicant method");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Updates the status of multiple applicants for a single event
	 * 
	 * @param eId
	 *            the event ID
	 * @param uIds
	 *            the list of user IDs
	 * @param status
	 *            the status
	 * @return
	 */
	public boolean updateApplicantsStatus(int eId, List<Integer> uIds, String status) {
		LOG.debug("Updating Applicant status for event ID: " + eId);
		if (uIds == null || uIds.size() == 0) {
			LOG.info("No applicants to approve");
			return false;
		} else {
			boolean executionStatus = true;
			for (int uId : uIds) {
				executionStatus = updateApplicantStatus(uId, eId, status);
				if (!executionStatus) {
					LOG.info("Status Updation failed for uID: " + uId);
				}
			}
			if (executionStatus) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Updates the status of the applicant with given uId and eID.
	 * 
	 * @param eId
	 *            the event ID
	 * @param uId
	 *            the user ID
	 * @param status
	 *            the status
	 * @return
	 */
	public boolean updateApplicantStatus(int uId, int eId, String status) {
		LOG.debug("Updating Applicant status for uId: " + uId);
		Connection con = null;
		String updateStatement = "UPDATE applicants set status = ? WHERE u_id = ? AND e_id = ?";
		try {
			con = db.getConnection();
			try (PreparedStatement pstmt = con.prepareStatement(updateStatement)) {
				pstmt.setString(1, status);
				pstmt.setInt(2, uId);
				pstmt.setInt(3, eId);
				if (pstmt.executeUpdate() > 0) {
					return updateChats(con, uId, eId, status);
				} else {
					LOG.debug("Could not update status of applicant: " + uId);
					return false;
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for updating Applicant Status.");
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for updating Applicant Status.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from update Applicant Status method.");
					e.printStackTrace();
				}
			}
		}
	}

	private boolean updateChats(Connection con, int uId, int eId, String status) {
		LOG.debug("Updating the Chats based on approved/rejected applicant.");
		if (Strings.STATUS_APPROVED.equalsIgnoreCase(status)) {
			LOG.debug("The applicant " + uId + " is approved. Adding him to the chat for event: " + eId);
			String insertStatement = "INSERT INTO chats (c_name, u_id, e_id, status) values ((select e_name from events where e_id = ? limit 1)::text || ': Chat', ?, ?, 'active')";
			try (PreparedStatement pstmt = con.prepareStatement(insertStatement)) {
				pstmt.setInt(1, eId);
				pstmt.setInt(2, uId);
				pstmt.setInt(3, eId);
				return pstmt.executeUpdate() > 0;
			} catch (Exception e) {
				LOG.error("Error while inserting into chats: " + insertStatement);
				e.printStackTrace();
				return false;
			}
		} else if (Strings.STATUS_REJECTED.equalsIgnoreCase(status)) {
			LOG.debug("The applicant " + uId + " is rejected. Deactivating him from the chat for event: " + eId);
			String updateStatement = "DELETE FROM chats where u_id = ? and e_id = ?";
			try (PreparedStatement pstmt = con.prepareStatement(updateStatement)) {
				pstmt.setInt(1, uId);
				pstmt.setInt(2, eId);
				// status can be = 0 too as the user might not have been added in the chat
				// initially.
				return pstmt.executeUpdate() >= 0;
			} catch (Exception e) {
				LOG.error("Error while deleting from chats: " + updateStatement);
				e.printStackTrace();
				return false;
			}
		}
		LOG.debug("Weird? Applicant is neither approved nor rejected. Nothing needs to be updated in chats.");
		return true;
	}

	public boolean updateUser(Users user) {
		LOG.debug("Updating User ID: " + user.getuId());
		Connection con = null;
		String updateStatement = "UPDATE users set mobile = ? , dob = ? , password = ? , description = ? , website = ? , location = ? WHERE u_id = ? and u_name = ? and email = ?";
		try {
			con = db.getConnection();
			try (PreparedStatement pstmt = con.prepareStatement(updateStatement)) {
				pstmt.setString(1, user.getMobile());
				pstmt.setString(2, user.getDob());
				pstmt.setString(3, user.getPassword());
				pstmt.setString(4, user.getDescription());
				pstmt.setString(5, user.getWebsite());
				pstmt.setString(6, user.getLocation());
				pstmt.setInt(7, user.getuId());
				pstmt.setString(8, user.getuName());
				pstmt.setString(9, user.getEmail());
				return pstmt.executeUpdate() > 0;
			} catch (Exception e) {
				LOG.error("Error while executing query for updating User.");
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for updating User.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from update User method.");
					e.printStackTrace();
				}
			}
		}
	}

	public boolean updateEvent(Events event) {
		LOG.debug("Updating Event ID: " + event.geteId());
		Connection con = null;
		String updateStatement = "UPDATE events set e_name = ? , location = ? , datetime = ? , description = ? , members = ? , status = ? WHERE e_id = ?";
		try {
			con = db.getConnection();
			try (PreparedStatement pstmt = con.prepareStatement(updateStatement)) {
				pstmt.setString(1, event.geteName());
				pstmt.setString(2, event.getLocation());
				pstmt.setString(3, event.getDatetime());
				pstmt.setString(4, event.getDescription());
				pstmt.setString(5, event.getMembers());
				pstmt.setString(6, event.getStatus());
				pstmt.setInt(7, event.geteId());
				return pstmt.executeUpdate() > 0;
			} catch (Exception e) {
				LOG.error("Error while executing query for updating Event.");
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for updating Event.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from update Event method.");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Method to get the chat list for a given user ID.
	 * 
	 * @param uId
	 *            the user ID.
	 * @return
	 */
	public List<Chats> getChats(Integer uId) {
		LOG.debug("Fetching Chats.");
		List<Chats> chats = new ArrayList<Chats>();
		String selectQuery = "SELECT * from chats where u_id = ? and status = 'active'";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setInt(1, uId);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					Chats chat = new Chats();
					chat.setcId(rs.getInt("c_id"));
					chat.setcName(rs.getString("c_name"));
					chat.setuId(rs.getInt("u_id"));
					chat.seteId(rs.getInt("e_id"));
					chat.setmId(rs.getInt("m_id"));
					chat.setStatus(rs.getString("status"));
					chats.add(chat);
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for fetching Chats.");
				e.printStackTrace();
				return chats;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for fetching Chats.");
			e.printStackTrace();
			return chats;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from get Chats method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Fetched Chats.");
		return chats;
	}

	/**
	 * Method to fetch the message for the given message id.
	 * 
	 * @param cId
	 *            the chat ID.
	 * @param eId
	 *            the event ID.
	 * @return
	 */
	public Messages getMessage(int mId) {
		LOG.debug("getting Message " + mId);
		Messages message = null;
		String selectQuery = "SELECT * from messages where m_id = ?";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setInt(1, mId);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					message = new Messages();
					message.setmId(rs.getInt("m_id"));
					message.seteId(rs.getInt("e_id"));
					message.setMsg(rs.getString("msg"));
					message.setStatus(rs.getString("status"));
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for Get Message.");
				e.printStackTrace();
				return message;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for Get Message.");
			e.printStackTrace();
			return message;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from Get Message method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Fetched message.");
		return message;
	}

	/**
	 * Method to save the message in the database
	 * 
	 * @return
	 */
	public boolean saveMessage(Messages message) {
		LOG.debug("Inserting message");
		String insertStatement = "INSERT INTO messages (e_id, msg, status) VALUES (?,?,?) ON CONFLICT (e_id) DO UPDATE SET msg = excluded.msg, status = excluded.status";
		Connection con = null;
		try {
			con = db.getConnection();
			PreparedStatement pstmt = con.prepareStatement(insertStatement);
			pstmt.setInt(1, message.geteId());
			pstmt.setString(2, message.getMsg());
			pstmt.setString(3, message.getStatus());
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			LOG.error("Error while inserting message.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from insert message method");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This method fetches the message that was just inserted via POST to fetch the
	 * message ID for which it was stored.
	 * 
	 * @param cId
	 *            the chat ID
	 * @param eId
	 *            the event ID
	 * @return
	 */
	public Messages readDBMessage(int eId) {
		LOG.debug("getting DB Message for EventID: " + eId);
		Messages message = null;
		String selectQuery = "SELECT * from messages where e_id = ?";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setInt(1, eId);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					message = new Messages();
					message.setmId(rs.getInt("m_id"));
					message.seteId(rs.getInt("e_id"));
					message.setMsg(rs.getString("msg"));
					message.setStatus(rs.getString("status"));
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for Read DB Message.");
				e.printStackTrace();
				return message;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for Read DB Message.");
			e.printStackTrace();
			return message;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from Read DB Message method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Fetched DB message.");
		return message;
	}

	/**
	 * Method to return the user details for given user id.
	 * 
	 * @param uId
	 *            the user ID.
	 * @return
	 */
	public Users getUser(int uId) {
		LOG.debug("getting User: " + uId);
		Users user = null;
		String selectQuery = "SELECT * from users where u_id = ?";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setInt(1, uId);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					user = new Users();
					user.setuId(rs.getInt("u_id"));
					user.setuName(rs.getString("u_name"));
					user.setEmail(rs.getString("email"));
					user.setMobile(rs.getString("mobile"));
					user.setDob(rs.getString("dob"));
					user.setPassword(rs.getString("password"));
					user.setType(rs.getString("type"));
					user.setDescription(rs.getString("description"));
					user.setWebsite(rs.getString("website"));
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for Get User.");
				e.printStackTrace();
				return user;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for Get User.");
			e.printStackTrace();
			return user;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from Get User method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Fetched user.");
		return user;
	}

	public String getApplicantStatus(int uId, int eId) {
		LOG.debug("getting applicant status for user: " + uId + ", for event: " + eId);
		String selectQuery = "SELECT status from applicants where u_id = ? and e_id = ?";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setInt(1, uId);
				selectStatement.setInt(2, eId);
				ResultSet rs = selectStatement.executeQuery();
				if (rs.next()) {
					String status = rs.getString("status");
					LOG.debug("found application, status is: " + status);
					return status;
				} else {
					LOG.debug("application not found for user: " + uId + ", for event: " + eId);
					return null;
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for get applicant status.");
				e.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for get applicant status.");
			e.printStackTrace();
			return null;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from get applicant status method.");
					e.printStackTrace();
				}
			}
		}
	}

	public boolean deleteApplicant(int uId, int eId) {
		LOG.debug("Deleting Applicant");
		String deleteStatement = "DELETE FROM applicants WHERE u_id = ? AND e_id = ?";
		Connection con = null;
		try {
			con = db.getConnection();
			PreparedStatement pstmt = con.prepareStatement(deleteStatement);
			pstmt.setInt(1, uId);
			pstmt.setInt(2, eId);
			int updateCount = pstmt.executeUpdate();
			LOG.debug("update count: " + updateCount);
			return updateCount > 0;
		} catch (Exception e) {
			LOG.error("Error while deleting Applicant.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from delete Applicant method");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This method saves the Google Profile of a user to the database and uses the
	 * google profile id as the password.
	 * 
	 * @param name
	 *            the name
	 * @param email
	 *            the email
	 * @param gprofid
	 *            the google Profile ID.
	 * @return
	 */
	public boolean saveGProfile(String name, String email, String gprofid, String type) {
		LOG.debug("Inserting Google Profile");
		String insertStatement = "INSERT INTO users (u_name, email, password, type, gprofid) VALUES(?,?,?,?,?)";
		Connection con = null;
		try {
			con = db.getConnection();
			PreparedStatement pstmt = con.prepareStatement(insertStatement);
			pstmt.setString(1, name);
			pstmt.setString(2, email);
			pstmt.setString(3, gprofid);
			pstmt.setString(4, type);
			pstmt.setString(5, gprofid);
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			LOG.error("Error while inserting Google Profile.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from insert Google Profile method.");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This method fetches the Google user from the database.
	 * 
	 * @param pId
	 * @return
	 */
	public Users getGoogleUser(String pId) {
		LOG.debug("getting Google User: " + pId);
		Users user = null;
		String selectQuery = "SELECT * from users where gprofid = ?";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setString(1, pId);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					user = new Users();
					user.setuId(rs.getInt("u_id"));
					user.setuName(rs.getString("u_name"));
					user.setEmail(rs.getString("email"));
					user.setMobile(rs.getString("mobile"));
					user.setDob(rs.getString("dob"));
					user.setPassword(rs.getString("password"));
					user.setType(rs.getString("type"));
					user.setDescription(rs.getString("description"));
					user.setWebsite(rs.getString("website"));
					user.setGprofid(rs.getString("gprofid"));
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for Get Google User.");
				e.printStackTrace();
				return user;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for Get Google User.");
			e.printStackTrace();
			return user;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from Get Google User method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Fetched user.");
		return user;
	}

	/**
	 * This method returns the list of users who are approved for a given event ID.
	 * 
	 * @param eId
	 *            the event ID.
	 * @return
	 */
	public List<Users> getApprovedVolunteers(int eId) {
		LOG.debug("Fetching Approved Volunteers for Event ID: " + eId);
		List<Users> volunteers = new ArrayList<Users>();
		String selectQuery = "SELECT * from users where type = 'vol' and u_id in (select u_id from applicants where status='approved' and e_id=?)";
		Connection con = null;
		try {
			con = db.getConnection();
			try (PreparedStatement selectStatement = con.prepareStatement(selectQuery)) {
				selectStatement.setInt(1, eId);
				ResultSet rs = selectStatement.executeQuery();
				while (rs.next()) {
					Users user = new Users();
					user.setuId(rs.getInt("u_id"));
					user.setuName(rs.getString("u_name"));
					user.setEmail(rs.getString("email"));
					user.setMobile(rs.getString("mobile"));
					user.setDob(rs.getString("dob"));
					user.setPassword(rs.getString("password"));
					user.setType(rs.getString("type"));
					user.setDescription(rs.getString("description"));
					user.setWebsite(rs.getString("website"));
					volunteers.add(user);
				}
			} catch (Exception e) {
				LOG.error("Error while executing query for fetching Approved Volunteers.");
				e.printStackTrace();
				return volunteers;
			}
		} catch (Exception e) {
			LOG.error("Error while getting DB connection for fetching Approved Volunteers.");
			e.printStackTrace();
			return volunteers;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from get Approved Volunteers method.");
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Fetched Approved Volunteers.");
		return volunteers;
	}

	/**
	 * This method deletes an event and its associated applicants, chats and
	 * messages for the given event ID.
	 * 
	 * @param eId
	 *            the event ID.
	 * @return
	 */
	public boolean deleteEventCascade(int eId) {
		LOG.debug("Cascading Delete for Event ID: " + eId);
		String deleteEvent = "DELETE FROM events WHERE e_id = ?";
		String deleteApplicants = "DELETE FROM applicants WHERE e_id = ?";
		String deleteChats = "DELETE FROM chats WHERE e_id = ?";
		String deleteMessages = "DELETE FROM messages WHERE e_id = ?";
		Connection con = null;
		try {
			con = db.getConnection();
			PreparedStatement psdm = con.prepareStatement(deleteMessages);
			psdm.setInt(1, eId);
			PreparedStatement psdc = con.prepareStatement(deleteChats);
			psdc.setInt(1, eId);
			PreparedStatement psda = con.prepareStatement(deleteApplicants);
			psda.setInt(1, eId);
			PreparedStatement psde = con.prepareStatement(deleteEvent);
			psde.setInt(1, eId);
			return (psdm.executeUpdate() > 0 && psdc.executeUpdate() > 0 && psda.executeUpdate() > 0
					&& psde.executeUpdate() > 0);
		} catch (Exception e) {
			LOG.error("Error while cascade deleting Event.");
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOG.error("Error while closing the connection from delete Event Cascade method");
					e.printStackTrace();
				}
			}
		}
	}

}
