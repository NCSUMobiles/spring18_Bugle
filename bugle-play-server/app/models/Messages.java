package models;

/**
 * @author Sumit Srivastava
 *
 */
public class Messages {
	private int mId;
	private int cId;
	private int eId;
	private String msg;
	private String status;

	/**
	 * @return the mId
	 */
	public int getmId() {
		return mId;
	}

	/**
	 * @param mId
	 *            the mId to set
	 */
	public void setmId(int mId) {
		this.mId = mId;
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
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Messages [mId=" + mId + ", uId=" + cId + ", eId=" + eId + ", msg=" + msg + ", status=" + status + "]";
	}

}
