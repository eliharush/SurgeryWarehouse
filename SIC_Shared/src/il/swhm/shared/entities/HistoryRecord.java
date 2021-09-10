package il.swhm.shared.entities;

import il.swhm.shared.entities.transmit.Transmit;


public class HistoryRecord {
	private Transmitable entity;
	private Transmit transmit;
	
	public Transmit getTransmit() {
		return transmit;
	}
	public void setTransmit(Transmit transmit) {
		this.transmit = transmit;
	}
	public Transmitable getEntity() {
		return entity;
	}
	public void setEntity(Transmitable entity) {
		this.entity = entity;
	}
	
	

	
}
