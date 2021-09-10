package il.swhm.shared.entities.inventorycount;

import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.Transmitable;
import il.swhm.shared.enums.EntityType;
import il.swhm.shared.enums.EntityStatus;
import il.swhm.shared.enums.ICType;

import java.util.Date;
import java.util.List;

public class InventoryCount extends Transmitable {
	private int stationId;
	private int countId;	
	private Customer customer;
	private Date countDate;
	private EntityStatus status;
	private ICType type;
	private Approver approver;
	
	private List<InventoryCountLine> lines;
	
	public int getStationId() {
		return stationId;
	}
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	public int getCountId() {
		return countId;
	}
	public void setCountId(int countId) {
		this.countId = countId;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Date getCountDate() {
		return countDate;
	}
	public void setCountDate(Date countDate) {
		this.countDate = countDate;
	}
	public EntityStatus getStatus() {
		return status;
	}
	public void setStatus(EntityStatus status) {
		this.status = status;
	}
	public List<InventoryCountLine> getLines() {
		return lines;
	}
	public void setLines(List<InventoryCountLine> lines) {
		this.lines = lines;
	}
	public ICType getType() {
		return type;
	}
	public void setType(ICType type) {
		this.type = type;
	}
	public Approver getApprover() {
		return approver;
	}
	public void setApprover(Approver approver) {
		this.approver = approver;
	}
	@Override
	public EntityType getEntityType() {
		return EntityType.COUNT;
	}
	
	
}
