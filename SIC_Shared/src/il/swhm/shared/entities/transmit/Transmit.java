package il.swhm.shared.entities.transmit;

import il.swhm.shared.entities.Transmitable;
import il.swhm.shared.enums.TransmitionStatus;

import java.util.Date;
import java.util.List;

public class Transmit {
	private int transmitId;
	private TransmitionStatus transStatus;
	private Date transDate;
	private String errMessage;
	private List<Transmitable> entities;

	public TransmitionStatus getTransStatus() {
		return transStatus;
	}
	public void setTransStatus(TransmitionStatus transStatus) {
		this.transStatus = transStatus;
	}
	public Date getTransDate() {
		return transDate;
	}
	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}
	public String getErrMessage() {
		return errMessage;
	}
	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}
	public int getTransmitId() {
		return transmitId;
	}
	public void setTransmitId(int transmitId) {
		this.transmitId = transmitId;
	}
	public List<Transmitable> getCounts() {
		return entities;
	}
	public void setCounts(List<Transmitable> counts) {
		this.entities = counts;
	}
	
	
	
	
}
