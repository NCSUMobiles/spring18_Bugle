package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;

import models.Applicants;
import models.Chats;
import models.Events;
import models.Messages;
import models.Users;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.DatabaseService;
import util.Strings;

/**
 * This controller contains an action to handle HTTP requests to the
 * application's home page.
 */
@Singleton
public class HomeController extends Controller {

	private final DatabaseService databaseService;
	private static final ALogger LOG = Logger.of(HomeController.class);

	@Inject
	public HomeController(DatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	/**
	 * An action that renders an HTML page with a welcome message. The configuration
	 * in the <code>routes</code> file means that this method will be called when
	 * the application receives a <code>GET</code> request with a path of
	 * <code>/</code>.
	 */
	public Result index() {
		return ok(views.html.index.render());
	}

	public Result options(String path) {
		LOG.debug("options request handled for path:" + path);
		return ok().withHeaders(Strings.CORS, Strings.STAR)
				.withHeaders("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
				.withHeaders("Access-Control-Allow-Headers",
						"Accept, Origin, Content-type, X-Json, X-Prototype-Version, X-Requested-With")
				.withHeaders("Access-Control-Allow-Credentials", "true").withHeaders("Access-Control-Max-Age", "3600");
	}

	public Result getOrganizations() {
		LOG.debug("getOrganizations method called.");
		List<Users> organizations = databaseService.getOrganizations();
		return ok(createSuccessResponse("organizations", new Gson().toJson(organizations))).withHeader(Strings.CORS,
				Strings.STAR);
	}

	public Result getEvents(String orgId) {
		LOG.debug("getEvents method called.");
		List<Events> events = databaseService.getEvents(Integer.valueOf(orgId));
		return ok(createSuccessResponse("events", new Gson().toJson(events))).withHeader(Strings.CORS, Strings.STAR);
	}

	/**
	 * Creates a new Event. Sample JSON to create event:
	 * 
	 * <pre>
	 * {
	 * "e_name":"Event 1",
	 * "location":"Hunt Library",
	 * "datetime":"04.14.2018 11:00AM",
	 * "description":"Volunteering Event at Hunt Library",
	 * "members":"12",
	 * "u_id":"4",
	 * "status":"active"
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result createEvent() {
		LOG.debug("createEvent method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for event.").withHeader(Strings.CORS, Strings.STAR);
		} else {
			String name = json.findPath("e_name").textValue();
			String location = json.findPath("location").textValue();
			String datetime = json.findPath("datetime").textValue();
			String description = json.findPath("description").textValue();
			String members = json.findPath("members").textValue();
			int uId = json.findPath("u_id").intValue();
			String status = json.findPath("status").textValue();

			Events event = new Events(name, location, datetime, description, members, uId, status);

			// save to DB and return response.
			if (databaseService.insertEvent(event)) {
				return ok(createSuccessResponse(Strings.MESSAGE, "Event Created")).withHeader(Strings.CORS,
						Strings.STAR);
			} else {
				return ok(createErrorResponse("Failed to create Event")).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	/**
	 * Method to validate user login
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public Result validateLogin() {
		LOG.debug("validateLogin method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for login.").withHeader(Strings.CORS, Strings.STAR);
		} else {
			String email = json.findPath("email").textValue().toLowerCase();
			String password = json.findPath("password").textValue();

			// validate from DB and return response.
			Users loginUser = databaseService.validateLogin(email, password);
			if (loginUser != null) {
				LOG.debug("Login Successful.");
				return ok(createSuccessResponse(Strings.USER, new Gson().toJson(loginUser))).withHeader(Strings.CORS,
						Strings.STAR);
			} else {
				LOG.debug("Incorrect user credentials.");
				return ok(createErrorResponse(Strings.LOGIN_FAIL)).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	/**
	 * Creates new user. Sample JSON to create new user:
	 * 
	 * <pre>
	 * {
	 * "u_name":"Harry",
	 * "email":"harry@gmail.com",
	 * "mobile":"1234567890",
	 * "dob":"06.11.94",
	 * "password":"harrioscar",
	 * "type":"vol"
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	public Result createUser() {
		LOG.debug("createUser method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for user.").withHeader(Strings.CORS, Strings.STAR);
		} else {

			String name = json.findPath("u_name").textValue();
			String email = json.findPath("email").textValue();
			if (email != null) {
				email = email.toLowerCase();
			}
			String mobile = json.findPath("mobile").textValue();
			String dob = json.findPath("dob").textValue();
			String password = json.findPath("password").textValue();
			String type = json.findPath("type").textValue();
			String description = json.findPath("description").textValue();
			String location = json.findPath("location").textValue();
			String website = json.findPath("website").textValue();

			Users user = new Users(name, email, mobile, dob, password, type, description, location, website);

			// save to DB and return response.
			if (databaseService.insertUser(user)) {
				user = databaseService.validateLogin(email, password);
				return ok(createSuccessResponse(Strings.USER, new Gson().toJson(user))).withHeader(Strings.CORS,
						Strings.STAR);
			} else {
				return ok(createErrorResponse("Failed to Create User")).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	/**
	 * 
	 * @param vId
	 * @return
	 */
	public Result getVolunteerEvents(String vId) {
		LOG.debug("getVolunteerEvents method called.");
		List<Events> events = databaseService.getApplicantEvents(Integer.valueOf(vId));
		return ok(createSuccessResponse("events", new Gson().toJson(events))).withHeader(Strings.CORS, Strings.STAR);
	}

	/**
	 * 
	 * @param eId
	 * @return
	 */
	public Result getEventVolunteers(String eId) {
		LOG.debug("getEventVolunteers method called.");
		List<Users> volunteers = databaseService.getEventApplicants(Integer.valueOf(eId));
		return ok(createSuccessResponse("volunteers", new Gson().toJson(volunteers))).withHeader(Strings.CORS,
				Strings.STAR);
	}

	/**
	 * Applies Volunteers for events. Sample JSON for application:
	 * 
	 * <pre>
	 * {
	 * "u_id":123,
	 * "e_id":12
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	public Result applyEvent() {
		LOG.debug("applyEvent method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for Applicant.").withHeader(Strings.CORS, Strings.STAR);
		} else {

			int uId = json.findPath("u_Id").intValue();
			int eId = json.findPath("e_Id").intValue();

			Applicants applicant = new Applicants(uId, eId, Strings.STATUS_APPLIED);

			// save to DB and return response.
			if (databaseService.insertApplicant(applicant)) {
				return ok(createSuccessResponse(Strings.MESSAGE, "Applied successfully.")).withHeader(Strings.CORS,
						Strings.STAR);
			} else {
				return ok(createErrorResponse("Could not apply to event.")).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	/**
	 * Approves the volunteers for a particular event as specified by the JSON.<br>
	 * Sample JSON:
	 * 
	 * <pre>
	 * {
	 *   "e_Id": 1,
	 *   "u_Ids": "1,2,3"
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	public Result approveVolunteers() {
		LOG.debug("approveVolunteers method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for Volunteers.").withHeader(Strings.CORS, Strings.STAR);
		} else {

			int eId = json.findPath("e_Id").intValue();
			String uIdsJson = json.findPath("u_Ids").textValue();

			List<Integer> uIds = new ArrayList<Integer>();

			if (uIdsJson == null || uIdsJson.length() == 0) {
				return ok(createErrorResponse("No Volunteers to Approve!")).withHeader(Strings.CORS, Strings.STAR);
			} else {
				String tokens[] = uIdsJson.split(Strings.COMMA);
				for (String token : tokens) {
					uIds.add(Integer.valueOf(token.trim()));
				}
			}

			// save to DB and return response.
			if (databaseService.updateApplicantsStatus(eId, uIds, Strings.STATUS_APPROVED)) {
				return ok(createSuccessResponse(Strings.MESSAGE, "Approved Volunteers succesfully"))
						.withHeader(Strings.CORS, Strings.STAR);
			} else {
				return ok(createErrorResponse("Could not approve Volunteers")).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	/**
	 * Rejects the volunteers for a particular event as specified by the JSON.<br>
	 * Sample JSON:
	 * 
	 * <pre>
	 * {
	 *   "e_Id": 1,
	 *   "u_Ids": "1,2,3"
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	public Result rejectVolunteers() {
		LOG.debug("rejectVolunteers method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for Volnteers.").withHeader(Strings.CORS, Strings.STAR);
		} else {

			int eId = json.findPath("e_Id").intValue();
			String uIdsJson = json.findPath("u_Ids").textValue();

			List<Integer> uIds = new ArrayList<Integer>();

			if (uIdsJson == null || uIdsJson.length() == 0) {
				return ok(createErrorResponse("No Vlounteers to Reject!")).withHeader(Strings.CORS, Strings.STAR);
			} else {
				String tokens[] = uIdsJson.split(Strings.COMMA);
				for (String token : tokens) {
					uIds.add(Integer.valueOf(token.trim()));
				}
			}

			// save to DB and return response.
			if (databaseService.updateApplicantsStatus(eId, uIds, Strings.STATUS_REJECTED)) {
				return ok(createSuccessResponse(Strings.MESSAGE, "Rejected Volunteers Succesfully"))
						.withHeader(Strings.CORS, Strings.STAR);
			} else {
				return ok(createErrorResponse("Could not reject volunteers")).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	public Result mockDB() {
		LOG.debug("mockDB method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for Mocking DB.").withHeader(Strings.CORS, Strings.STAR);
		} else {

			String bugleKey = json.findPath("bugle-key").textValue();

			if (Strings.BUGLE_DB_KEY.equals(bugleKey)) {
				// save to DB and return response.
				if (databaseService.mockDatabase()) {
					return ok(createSuccessResponse(Strings.MESSAGE, "Mocked Database Successfully"))
							.withHeader(Strings.CORS, Strings.STAR);
				} else {
					return ok(createErrorResponse("Unable to generate Mock database!")).withHeader(Strings.CORS,
							Strings.STAR);
				}
			} else {
				return ok(createErrorResponse(Strings.INCORRECT_KEY)).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public Result resetDB() {
		LOG.debug("resetDB method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for Resetting DB.").withHeader(Strings.CORS, Strings.STAR);
		} else {

			String bugleKey = json.findPath("bugle-key").textValue();

			if (Strings.BUGLE_DB_KEY.equals(bugleKey)) {
				// save to DB and return response.
				if (databaseService.resetDatabase()) {
					return ok(createSuccessResponse(Strings.MESSAGE, "Database reset Successfully"))
							.withHeader(Strings.CORS, Strings.STAR);
				} else {
					return ok(createErrorResponse("Unable to reset database!")).withHeader(Strings.CORS, Strings.STAR);
				}
			} else {
				return ok(createErrorResponse(Strings.INCORRECT_KEY)).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	/**
	 * This method edits the value of the specified column name in the User table in
	 * database.
	 * 
	 * @return
	 */
	public Result editUser() {
		LOG.debug("editUser method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for Editing User.").withHeader(Strings.CORS, Strings.STAR);
		} else {
			int uId = json.findPath(Strings.USERS_UID).intValue();
			String uName = json.findPath(Strings.USERS_UNAME).textValue();
			String email = json.findPath(Strings.USERS_EMAIL).textValue();
			String type = json.findPath(Strings.USERS_TYPE).textValue();
			String mobile = json.findPath(Strings.USERS_MOBILE).textValue();
			String dob = json.findPath(Strings.USERS_DOB).textValue();
			String password = json.findPath(Strings.USERS_PASSWORD).textValue();
			String description = json.findPath(Strings.USERS_DESCRIPTION).textValue();
			String website = json.findPath(Strings.USERS_WEBSITE).textValue();
			String location = json.findPath(Strings.USERS_LOCATION).textValue();

			Users user = new Users();
			user.setuId(uId);
			user.setuName(uName);
			user.setEmail(email);
			user.setType(type);
			user.setMobile(mobile);
			user.setPassword(password);
			user.setDescription(description);
			user.setWebsite(website);
			user.setLocation(location);
			user.setDob(dob);

			LOG.debug("Updating user ID: " + uId);

			if (databaseService.updateUser(user)) {
				LOG.debug("Updated user ID: " + uId);
				return ok(createSuccessResponse(Strings.USER, new Gson().toJson(user))).withHeader(Strings.CORS,
						Strings.STAR);
			} else {
				return ok(createErrorResponse("Unable to update user details.")).withHeader(Strings.CORS, Strings.STAR);
			}

		}
	}

	public Result editEvent() {
		LOG.debug("editEvent method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for Editing User.").withHeader(Strings.CORS, Strings.STAR);
		} else {
			int eId = json.findPath(Strings.EVENT_EID).intValue();
			String eName = json.findPath(Strings.EVENT_ENAME).textValue();
			String location = json.findPath(Strings.EVENT_LOCATION).textValue();
			String datetime = json.findPath(Strings.EVENT_DATETIME).textValue();
			String description = json.findPath(Strings.EVENT_DESCRIPTION).textValue();
			String members = json.findPath(Strings.EVENT_MEMBERS).textValue();
			String status = json.findPath(Strings.EVENT_STATUS).textValue();

			Events event = new Events();
			event.seteId(eId);
			event.seteName(eName);
			event.setLocation(location);
			event.setDatetime(datetime);
			event.setDescription(description);
			event.setMembers(members);
			event.setStatus(status);

			LOG.debug("Updating event ID: " + eId);

			if (databaseService.updateEvent(event)) {
				LOG.debug("Updated event ID: " + eId);
				return ok(createSuccessResponse(Strings.EVENT, new Gson().toJson(event))).withHeader(Strings.CORS,
						Strings.STAR);
			} else {
				return ok(createErrorResponse("Unable to update event details.")).withHeader(Strings.CORS,
						Strings.STAR);
			}
		}
	}

	/**
	 * This method gets the chats from the database for the user with the given user
	 * id.
	 * 
	 * @param vId
	 *            the user ID
	 * @return
	 */
	public Result getChats(String vId) {
		LOG.debug("getEvents method called.");
		List<Chats> chats = databaseService.getChats(Integer.valueOf(vId));
		return ok(createSuccessResponse("chats", new Gson().toJson(chats))).withHeader(Strings.CORS, Strings.STAR);
	}

	/**
	 * This method retrieves a message from the database for the given message id.
	 * 
	 * @param mId
	 *            the message ID
	 * @return
	 */
	public Result getMessage(String mId) {
		LOG.debug("getMessage method called.");
		Messages message = databaseService.getMessage(Integer.valueOf(mId));
		return ok(createSuccessResponse(Strings.MESSAGE, new Gson().toJson(message))).withHeader(Strings.CORS,
				Strings.STAR);
	}

	/**
	 * This method saves the message to the database
	 * 
	 * @return the saved message
	 */
	public Result saveMessage() {
		LOG.debug("saveMessage method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for saving Message.").withHeader(Strings.CORS, Strings.STAR);
		} else {
			int cId = Integer.valueOf(json.findPath("cId").textValue());
			int eId = Integer.valueOf(json.findPath("eId").textValue());
			String msg = json.findPath("message").textValue();

			Messages message = new Messages();
			message.seteId(eId);
			message.setMsg(msg);
			message.setStatus(Strings.STATUS_ACTIVE);

			LOG.debug("Saving message for Chat: " + cId + ", and Event: " + eId);

			if (databaseService.saveMessage(message)) {
				LOG.debug("Saved message for Chat: " + cId);
				// doing another DB call to read the message ID for this chatID and eventID.
				message = databaseService.readDBMessage(eId);
				// this newly fetched message would have message ID as well.
				return ok(createSuccessResponse(Strings.MESSAGE, new Gson().toJson(message))).withHeader(Strings.CORS,
						Strings.STAR);
			} else {
				return ok(createErrorResponse("Unable to save Message.")).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	/**
	 * This message returns the details of the user for the given user id.
	 * 
	 * @param uId
	 *            the user ID.
	 * @return
	 */
	public Result getUser(String uId) {
		LOG.debug("getUser method called.");
		Users user = databaseService.getUser(Integer.valueOf(uId));
		return ok(createSuccessResponse("user", new Gson().toJson(user))).withHeader(Strings.CORS, Strings.STAR);
	}

	/**
	 * Validate details for event details
	 * 
	 * @return json
	 */
	public Result validateDetails() {
		LOG.debug("validateDetails method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for saving Message.").withHeader(Strings.CORS, Strings.STAR);
		} else {
			int oId = json.findPath("orgID").intValue();
			int uId = json.findPath("userID").intValue();
			int eId = json.findPath("eventID").intValue();

			try {
				Users orgUser = databaseService.getUser(oId);

				String appliedStatus = databaseService.getApplicantStatus(uId, eId);

				ObjectNode result = Json.newObject();
				result.put(Strings.STATUS, Strings.SUCCESS);
				result.put("orgName", orgUser.getuName());
				result.put("appliedStatus", appliedStatus);
				return ok(result).withHeader(Strings.CORS, Strings.STAR);

			} catch (Exception e) {
				LOG.error("Error while validating Details for: ORGANIZATION ID: " + oId + ", USER ID: " + uId
						+ ", EVENT ID: " + eId);
				e.printStackTrace();
				return ok(createErrorResponse("Unable to validate Event Details!")).withHeader(Strings.CORS,
						Strings.STAR);
			}
		}
	}

	/**
	 * leave event for an applicant as specified by JSON.
	 * 
	 * @return
	 */
	public Result leaveEvent() {
		LOG.debug("leaveEvent method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for withdrawing Applicant.").withHeader(Strings.CORS, Strings.STAR);
		} else {

			int uId = json.findPath("u_Id").intValue();
			int eId = json.findPath("e_Id").intValue();

			// save to DB and return response.
			if (databaseService.deleteApplicant(uId, eId)) {
				return ok(createSuccessResponse(Strings.MESSAGE, "Left Event successfully.")).withHeader(Strings.CORS,
						Strings.STAR);
			} else {
				return ok(createErrorResponse("Could not leave event.")).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	/**
	 * Method to save the Google Profile of a User.
	 * 
	 * @return
	 */
	public Result saveGoogleProfile() {
		LOG.debug("saveGoogleProfile method called.");
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data for saving User's Google Profile.").withHeader(Strings.CORS,
					Strings.STAR);
		} else {
			String name = json.findPath("u_name").textValue();
			String email = json.findPath("email").textValue();
			if (email != null) {
				email = email.toLowerCase();
			}
			String gprofid = json.findPath("gprofid").textValue();
			String type = json.findPath("type").textValue();

			// save to DB and return response.
			if (databaseService.saveGProfile(name, email, gprofid, type)) {
				Users user = databaseService.validateLogin(email, gprofid);
				return ok(createSuccessResponse(Strings.USER, new Gson().toJson(user))).withHeader(Strings.CORS,
						Strings.STAR);
			} else {
				return ok(createErrorResponse("Failed to Create User")).withHeader(Strings.CORS, Strings.STAR);
			}
		}
	}

	/**
	 * Gets the Google profile of a saved Google user from database (if saved) or
	 * null if the Google Profile is not save in our database.
	 * 
	 * @param pId
	 *            the profileID of the Google user.
	 * @return
	 */
	public Result getGoogleProfile(String pId) {
		LOG.debug("getGoogleProfile method called.");
		Users user = databaseService.getGoogleUser(pId);
		return ok(createSuccessResponse("user", new Gson().toJson(user))).withHeader(Strings.CORS, Strings.STAR);
	}

	/**
	 * Gets a list of the approved volunteers for an event.
	 * 
	 * @param eId
	 *            the event ID.
	 * @return
	 */
	public Result getApprovedVolunteers(String eId) {
		LOG.debug("getApprovedVolunteers method called.");
		List<Users> users = databaseService.getApprovedVolunteers(Integer.valueOf(eId));
		return ok(createSuccessResponse("volunteers", new Gson().toJson(users))).withHeader(Strings.CORS, Strings.STAR);
	}

	/**
	 * This method returns the following JSON response:
	 * 
	 * <pre>
	 * {
	 *   "status":"success",
	 *   "&lt;key&gt;":"&lt;message&gt;"
	 * </pre>
	 * 
	 * @param key
	 *            the key
	 * @param message
	 *            the message
	 * @return
	 */
	private ObjectNode createSuccessResponse(String key, Object message) {
		ObjectNode result = Json.newObject();
		result.put(Strings.STATUS, Strings.SUCCESS);
		if (key != null && message != null) {
			result.put(key, (String) message);
		}
		return result;
	}

	/**
	 * This method returns the following JSON response:
	 * 
	 * <pre>
	 * {
	 *   "status":"error",
	 *   "message":"&lt;message&gt;"
	 * </pre>
	 * 
	 * @param message
	 *            the message
	 * @return
	 */
	private ObjectNode createErrorResponse(String message) {
		ObjectNode result = Json.newObject();
		result.put(Strings.STATUS, Strings.ERROR);
		result.put(Strings.MESSAGE, message);
		return result;
	}

}
