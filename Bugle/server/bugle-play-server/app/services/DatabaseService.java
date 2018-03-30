package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import models.Events;
import models.Users;
import play.Logger;
import play.Logger.ALogger;
import play.db.Database;

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
		LOG.debug("Initializing database.");
		Connection con = null;
		try {
			con = db.getConnection();
			// Initialize database here.
			String createUsers = "CREATE TABLE IF NOT EXISTS users (u_id integer PRIMARY KEY, u_name text NOT NULL, email text NOT NULL, mobile text, dob text, password text NOT NULL, type text NOT NULL, description text, location text, website text)";
			String createEvents = "CREATE TABLE IF NOT EXISTS events (e_id integer PRIMARY KEY, e_name text NOT NULL, location text, datetime text, description text, members text, u_id integer, status text)";
			String createApplicants = "CREATE TABLE IF NOT EXISTS applicants (a_id integer PRIMARY KEY, u_id integer NOT NULL, e_id integer NOT NULL, status text)";
			String createChats = "CREATE TABLE IF NOT EXISTS chats (c_id integer PRIMARY KEY, c_name text NOT NULL, u_ids text, status text)";
			try (Statement stmt = con.createStatement()) {
				stmt.execute(createUsers);
				stmt.execute(createEvents);
				stmt.execute(createApplicants);
				stmt.execute(createChats);
			} catch (SQLException e) {
				e.printStackTrace();
				LOG.error("Error while executing query for initializing database.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error while creating DB connection for initializing database.");
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
			LOG.error("Error while creating DB connection for validating Login.");
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
				selectStatement.setString(1, "org");
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
			LOG.error("Error while creating DB connection for fetching Organizations.");
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
		LOG.debug("Inserting Event");
		String insertStatement = "INSERT INTO events (e_name, location, datetime, description, members, u_id, status) VALUES(?,?,?,?,?,?,?)";
		Connection con = null;
		try {
			con = db.getConnection();
			PreparedStatement pstmt = con.prepareStatement(insertStatement);
			pstmt.setString(1, event.geteName());
			pstmt.setString(2, event.getLocation());
			pstmt.setString(3, event.getDatetime());
			pstmt.setString(4, event.getDescription());
			pstmt.setString(5, event.getMembers());
			pstmt.setInt(6, event.getuId());
			pstmt.setString(7, event.getStatus());
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			LOG.error("Error while inserting event.");
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
		String selectQuery = "SELECT * from events where u_id = ?";
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
			LOG.error("Error while creating DB connection for fetching events.");
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

	// public List<Users> getEventApplicants(int eId){return null;} // to return
	// list of all applicants for an event.

	// public List<Events> getApplicantEvents(int uId){return null;} // to return
	// list of all events applied for by an applicant.

	// public boolean updateVolunteerStatus(int eId, List<Integer> uIds){return
	// false;} // to update approval status of volunteers for a particular event.

}
