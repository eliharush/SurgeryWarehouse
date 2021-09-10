package il.swhm.shared.entities;

import il.swhm.shared.enums.ICType;
import il.swhm.shared.enums.OrderType;


public class Customer {
	private Hospital hospital;
	private String id;
	private String name;

	private int currentCountId;
	private ICType currentCountType;
	private int notTransmitedCountId;
	private ICType notTransmitedCountType;
	private int currentOrderId;
	private OrderType currentOrderType;
	private int notTransmitedOrderId;
	private OrderType notTransmitedOrderType;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Hospital getHospital() {
		return hospital;
	}
	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCurrentCountId() {
		return currentCountId;
	}
	public void setCurrentCountId(int currentCountId) {
		this.currentCountId = currentCountId;
	}
	public ICType getCurrentCountType() {
		return currentCountType;
	}
	public void setCurrentCountType(ICType currentCountType) {
		this.currentCountType = currentCountType;
	}
	
	
	public int getNotTransmitedCountId() {
		return notTransmitedCountId;
	}
	public void setNotTransmitedCountId(int notTransmitedCountId) {
		this.notTransmitedCountId = notTransmitedCountId;
	}
	public ICType getNotTransmitedCountType() {
		return notTransmitedCountType;
	}
	public void setNotTransmitedCountType(ICType notTransmitedCountType) {
		this.notTransmitedCountType = notTransmitedCountType;
	}
	
	
	public void setNotTransmitedCount(String notTransmitCount){
		if(notTransmitCount==null){
			return;
		}
		String[] args= notTransmitCount.split("--");
		
		this.setNotTransmitedCountId(Integer.valueOf(args[0]));
		this.setNotTransmitedCountType(ICType.valueOf(args[1]));
	}
	
	
	
	
	public int getCurrentOrderId() {
		return currentOrderId;
	}
	public void setCurrentOrderId(int currentOrderId) {
		this.currentOrderId = currentOrderId;
	}
	public OrderType getCurrentOrderType() {
		return currentOrderType;
	}
	public void setCurrentOrderType(OrderType currentOrderType) {
		this.currentOrderType = currentOrderType;
	}
	public int getNotTransmitedOrderId() {
		return notTransmitedOrderId;
	}
	public void setNotTransmitedOrderId(int notTransmitedOrderId) {
		this.notTransmitedOrderId = notTransmitedOrderId;
	}
	public OrderType getNotTransmitedOrderType() {
		return notTransmitedOrderType;
	}
	public void setNotTransmitedOrderType(OrderType notTransmitedOrderType) {
		this.notTransmitedOrderType = notTransmitedOrderType;
	}
	
	public void setNotTransmitedOrder(String notTransmitOrder){
		if(notTransmitOrder==null){
			return;
		}
		String[] args= notTransmitOrder.split("--");
		
		this.setNotTransmitedOrderId(Integer.valueOf(args[0]));
		this.setNotTransmitedOrderType(OrderType.valueOf(args[1]));
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hospital == null) ? 0 : hospital.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (hospital == null) {
			if (other.hospital != null)
				return false;
		} else if (!hospital.equals(other.hospital))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
	
}
