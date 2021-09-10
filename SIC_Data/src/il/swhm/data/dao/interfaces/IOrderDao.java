package il.swhm.data.dao.interfaces;

import il.swhm.shared.entities.order.PushOrderHead;
import il.swhm.shared.enums.EntityStatus;


public interface IOrderDao {
	
	public PushOrderHead getOrder(int stationId,int orderId,EntityStatus status);
	public PushOrderHead addOrder(PushOrderHead order);
	public void saveOrder(PushOrderHead order);
	public void finishOrder(PushOrderHead order);
	public void cancelOrder(int stationId,int orderId);
}
