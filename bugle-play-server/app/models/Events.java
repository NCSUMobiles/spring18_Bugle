package models;

/**
 * @author Sumit Srivastava
 *
 */
public class Events {
	private int eId;
	private String eName;
	private String location;
	private String datetime;
	private String description;
	private String members;
	private int uId;
	private String status;
	
	public Events() {
	}

	public Events(String eName, String location, String datetime, String description, String members, int uId, String status) {
		this.eName = eName;
		this.location = location;
		this.datetime = datetime;
		this.description = description;
		this.members = members;
		this.uId = uId;
		this.status = status;
	}
	/**
	 * @return the eId
	 */
	public int geteId() {
		return eId;
	}

	/**
	 * @param eId
	 *            the eId to set
	 */
	public void seteId(int eId) {
		this.eId = eId;
	}

	/**
	 * @return the eName
	 */
	public String geteName() {
		return eName;
	}

	/**
	 * @param eName
	 *            the eName to set
	 */
	public void seteName(String eName) {
		this.eName = eName;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the datetime
	 */
	public String getDatetime() {
		return datetime;
	}

	/**
	 * @param datetime
	 *            the datetime to set
	 */
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the members
	 */
	public String getMembers() {
		return members;
	}

	/**
	 * @param members
	 *            the members to set
	 */
	public void setMembers(String members) {
		this.members = members;
	}

	/**
	 * @return the uIds
	 */
	public int getuId() {
		return uId;
	}

	/**
	 * @param uIds
	 *            the uIds to set
	 */
	public void setuId(int uId) {
		this.uId = uId;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Events [eId=" + eId + ", eName=" + eName + ", location=" + location + ", datetime=" + datetime
				+ ", description=" + description + ", members=" + members + ", uId=" + uId + ", status=" + status + "]";
	}
}
