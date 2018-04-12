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
import models.Events;
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
			String createUsers = "CREATE TABLE IF NOT EXISTS users (u_id integer PRIMARY KEY, u_name text NOT NULL, email text NOT NULL, mobile text, dob text, password text NOT NULL, type text NOT NULL, description text, location text, website text)";
			String createEvents = "CREATE TABLE IF NOT EXISTS events (e_id integer PRIMARY KEY, e_name text NOT NULL, location text, datetime text, description text, members text, u_id integer, status text)";
			String createApplicants = "CREATE TABLE IF NOT EXISTS applicants (a_id integer PRIMARY KEY, u_id integer NOT NULL, e_id integer NOT NULL, status text)";
			String createChats = "CREATE TABLE IF NOT EXISTS chats (c_id integer PRIMARY KEY, c_name text NOT NULL, u_ids text, status text)";
			try (Statement stmt = con.createStatement()) {
				stmt.execute(createUsers);
				stmt.execute(createEvents);
				stmt.execute(createApplicants);
				stmt.execute(createChats);
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
				"INSERT INTO users (u_name, email, mobile, password, type, description, location, website) values ('Organization 1','org@org1.com','1236987894','o1pw','org','This is a very big Volunteering organization based in US. For more information visit: xyz.org1.com','Raleigh','xyz.org1.com')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, password, type, description, location, website) values ('Organization 2','org@org2.com','1236547234','o2pw','org','This is the biggest Volunteering organization based in Atlanta, US. For more information visit: xyz.org2.com','Atlanta','xyz.org2.com')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, password, type, description, location, website) values ('Organization 3','org@org3.com','1236547894','o3pw','org','This is the third volunteer organization. We do volunteer work for this and that. For more information visit: xyz.org3.com','Chicago','xyz.org3.com')");
		// users-volunteers
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type) values ('Default User','usr1@vol.com','1232233421','21.08.93','pwd1','vol')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type) values ('Flower User','usr2@vol.com','1231231233','19.01.90','pwd2','vol')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type) values ('Google User','usr3@vol.com','1233213458','18.01.95','pwd3','vol')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type) values ('Facebook User','usr4@vol.com','123764543','24.09.85','pwd4','vol')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type) values ('Twitter User','usr5@vol.com','123987456','30.09.89','pwd5','vol')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type) values ('Snapchat User','usr62@vol.com','1239873458','11.11.90','pwd6','vol')");
		insertStatements.add(
				"INSERT INTO users (u_name, email, mobile, dob, password, type) values ('Alien User','usr7@vol.com','1230978345','16.08.94','pwd7','vol')");
		// events
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 1','Raleigh','24.5.18 11:00AM','description of a volunteering event!! come volunteer with us','12',1,'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 2','Colorado','19.7.18 01:00PM','description of an exciting volunteering event!! come volunteer with us','22',1,'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 123','San Jose','11.6.18 10:00AM','description this is a volunteering event!! come volunteer with us in San Jose.','50',1,'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 234','New York','16.5.18 03:00PM','description welcome to the volunteering event!! come volunteer with us','10',2,'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 23','Atlanta','01.7.18 09:00AM','description volunteering event!! come volunteer with us in Atlanta','5',2,'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 47','Florida','15.5.18 11:00AM','description very interesting volunteering event!! come volunteer with us','7',3,'active')");
		insertStatements.add(
				"INSERT INTO events (e_name, location, datetime, description, members, u_id, status) values ('Event 74','Chicago','21.4.118 08:00AM','description volunteering event. Big volunteering event need lots of volunteers!! come volunteer with us!','100',3,'active')");
		Connection con = null;
		try {
			con = db.getConnection();
			boolean status = true;
			for (String insertStatement : insertStatements) {
				try (PreparedStatement pstmt = con.prepareStatement(insertStatement)) {
					int recordsInserted = pstmt.executeUpdate();
					status = recordsInserted > 0;
				} catch (Exception e) {
					LOG.error("Error while generating mock data, for Insert statement: " + insertStatement);
					e.printStackTrace();
				}
			}
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
			try (Statement stmt = con.createStatement()) {
				boolean status = stmt.executeUpdate(resetChats) >= 0 && stmt.executeUpdate(resetApplicants) >= 0
						&& stmt.executeUpdate(resetEvents) >= 0 && stmt.executeUpdate(resetUsers) >= 0;
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
		String selectQuery = "SELECT * FROM users WHERE u_id IN (SELECT u_id from applicants where e_id = ?)";
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
		}
		return false;
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
		LOG.debug("Updating Applicant status");
		Connection con = null;
		String updateStatement = "UPDATE applicants set status = ? WHERE u_id = ? AND e_id = ?";
		try {
			con = db.getConnection();
			try (PreparedStatement pstmt = con.prepareStatement(updateStatement)) {
				pstmt.setString(1, status);
				pstmt.setInt(2, uId);
				pstmt.setInt(3, eId);
				return pstmt.executeUpdate() > 0;
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

}
