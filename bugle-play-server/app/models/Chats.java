package models;

/**
 * @author Sumit Srivastava
 *
 */
public class Chats {
	private int cId;
	private String cName;
	private int uId;
	private int eId;
	private String status;
	private int mId;

	public Chats() {
	}

	public Chats(String cName, int uId, int eId, String status) {
		this.cName = cName;
		this.uId = uId;
		this.eId = eId;
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

	/**
	 * @return the mId
	 */
	public int getmId() {
		return mId;
	}

	/**
	 * @param mId the mId to set
	 */
	public void setmId(int mId) {
		this.mId = mId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Chats [cId=" + cId + ", cName=" + cName + ", uId=" + uId + ", eId=" + eId + ", status=" + status
				+ ", mId=" + mId + "]";
	}

}
