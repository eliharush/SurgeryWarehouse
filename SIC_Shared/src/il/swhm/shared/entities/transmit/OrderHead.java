package il.swhm.shared.entities.transmit;

import java.util.List;

public class OrderHead {
	private int stationId;
	private int transmitId;
	private String hospitalId;
	private String userName;
	private List<OrderLine> lines;
	public int getStationId() {
		return stationId;
	}
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	public int getTransmitId() {
		return transmitId;
	}
	public void setTransmitId(int transmitId) {
		this.transmitId = transmitId;
	}
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public List<OrderLine> getLines() {
		return lines;
	}
	public void setLines(List<OrderLine> lines) {
		this.lines = lines;
	}


}
