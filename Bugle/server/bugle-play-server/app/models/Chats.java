package models;

/**
 * @author Sumit Srivastava
 *
 */
public class Chats {
	private int cId;
	private String cName;
	private String uIds;
	private String status;

	public Chats() {
	}
	
	public Chats(String cName, String uIds, String status) {
		this.cName = cName;
		this.uIds = uIds;
		this.status = status;
	}
	
	/**
	 * @return the cId
	 */
	public int getcId() {
		return cId;
	}

	/**
	 * @param cId
	 *            the cId to set
	 */
	public void setcId(int cId) {
		this.cId = cId;
	}

	/**
	 * @return the cName
	 */
	public String getcName() {
		return cName;
	}

	/**
	 * @param cName
	 *            the cName to set
	 */
	public void setcName(String cName) {
		this.cName = cName;
	}

	/**
	 * @return the uIds
	 */
	public String getuIds() {
		return uIds;
	}

	/**
	 * @param uIds
	 *            the uIds to set
	 */
	public void setuIds(String uIds) {
		this.uIds = uIds;
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
		return "Chats [cId=" + cId + ", cName=" + cName + ", uIds=" + uIds + ", status=" + status + "]";
	}
}
