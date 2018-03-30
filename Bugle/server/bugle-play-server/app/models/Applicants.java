package models;

/**
 * @author Sumit Srivastava
 *
 */
public class Applicants {
	private int aId;
	private int uId;
	private int eId;
	private String status;
	
	public Applicants() {
	}
	
	public Applicants(int uId, int eId, String status) {
		this.uId = uId;
		this.eId = eId;
		this.status = status;
	}

	/**
	 * @return the aId
	 */
	public int getaId() {
		return aId;
	}

	/**
	 * @param aId
	 *            the aId to set
	 */
	public void setaId(int aId) {
		this.aId = aId;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Applicants [aId=" + aId + ", uId=" + uId + ", eId=" + eId + ", status=" + status + "]";
	}
}
