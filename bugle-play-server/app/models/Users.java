package models;

/**
 * 
 * @author Sumit Srivastava
 *
 */
public class Users {
	private int uId;
	private String uName;
	private String email;
	private String mobile;
	private String dob;
	private String password;
	private String type;
	private String description;
	private String location;
	private String website;
	private String gprofid;

	public Users() {
	}
	
	public Users(String uName, String email, String mobile, String dob, String password, String type, String description, String location, String website) {
		this.uName = uName;
		this.email = email;
		this.mobile = mobile;
		this.dob = dob;
		this.password = password;
		this.type = type;
		this.description = description;
		this.location = location;
		this.website = website;
	}
	
	/**
	 * @return the uId
	 */
	public int getuId() {
		return uId;
	}

	/**
	 * @param uId
	 *            the uId to set
	 */
	public void setuId(int uId) {
		this.uId = uId;
	}

	/**
	 * @return the uName
	 */
	public String getuName() {
		return uName;
	}

	/**
	 * @param uName
	 *            the uName to set
	 */
	public void setuName(String uName) {
		this.uName = uName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile
	 *            the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the dob
	 */
	public String getDob() {
		return dob;
	}

	/**
	 * @param dob
	 *            the dob to set
	 */
	public void setDob(String dob) {
		this.dob = dob;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * @param website
	 *            the website to set
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * @return the gprofid
	 */
	public String getGprofid() {
		return gprofid;
	}

	/**
	 * @param gprofid the gprofid to set
	 */
	public void setGprofid(String gprofid) {
		this.gprofid = gprofid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Users [uId=" + uId + ", uName=" + uName + ", email=" + email + ", mobile=" + mobile + ", dob=" + dob
				+ ", password=" + password + ", type=" + type + ", description=" + description + ", location="
				+ location + ", website=" + website + ", gprofid=" + gprofid + "]";
	}

}
