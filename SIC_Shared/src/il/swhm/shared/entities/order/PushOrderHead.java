package il.swhm.shared.entities.order;

import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.Transmitable;
import il.swhm.shared.enums.EntityStatus;
import il.swhm.shared.enums.EntityType;
import il.swhm.shared.enums.OrderType;

import java.util.Date;
import java.util.List;

public class PushOrderHead extends Transmitable {
	private int stationId;
	private int orderId;	
	private Customer customer;
	private Date orderDate;
	private EntityStatus status;
	private OrderType type;
	private Approver approver;
	
	private List<PushOrderLine> lines;
	
	
	public int getStationId() {
		return stationId;
	}
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public EntityStatus getStatus() {
		return status;
	}
	public void setStatus(EntityStatus status) {
		this.status = status;
	}
	public OrderType getType() {
		return type;
	}
	public void setType(OrderType type) {
		this.type = type;
	}
	public Approver getApprover() {
		return approver;
	}
	public void setApprover(Approver approver) {
		this.approver = approver;
	}
	public List<PushOrderLine> getLines() {
		return lines;
	}
	public void setLines(List<PushOrderLine> lines) {
		this.lines = lines;
	}
	@Override
	public EntityType getEntityType() {
		return EntityType.ORDER;
	}
	
	
	
}
