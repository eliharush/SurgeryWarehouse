package il.swhm.data.mappers;

import il.swhm.shared.entities.HistoryRecord;
import il.swhm.shared.entities.order.PushOrderHead;
import il.swhm.shared.entities.order.PushOrderLine;
import il.swhm.shared.enums.EntityStatus;
import il.swhm.shared.enums.OrderType;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
	
	public PushOrderHead selectPushOrderByOrder(@Param("stationId") int stationId,@Param("orderId") int orderId,@Param("status") EntityStatus status);
	public PushOrderHead selectPushOrderByCustomer(@Param("stationId") int stationId,@Param("hospitalId") String hospitalId,@Param("customerId") String customerId);
	
	public List<PushOrderHead> selectPushOrdersForTransmit(@Param("stationId") int stationId);
	public List<PushOrderHead> selectPushOrdersForTransmitAndOrders(@Param("stationId") int stationId,@Param("orderIds") List<Integer> orderIds);
	public int insertPushOrder(@Param("order") PushOrderHead order);
	public void insertPushOrderLine(@Param("order") PushOrderHead order,@Param("ol") PushOrderLine ol);
	public void deletePushOrderHead(@Param("stationId") int stationId,@Param("orderId") int orderId);
	public void deletePushOrderLines(@Param("stationId") int stationId,@Param("orderId") int orderId);
	public void updateOrderStatusAndApprover(@Param("stationId") int stationId,@Param("orderId") int orderId,@Param("newStatus") EntityStatus status,@Param("approverId") String approverId);
	public List<HistoryRecord> selectHistory();
	public void updateOrderType(@Param("stationId") int stationId,@Param("orderId") int orderId,@Param("type") OrderType type);
}
