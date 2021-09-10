package il.swhm.data.mappers;

import il.swhm.shared.entities.HistoryRecord;
import il.swhm.shared.entities.inventorycount.InventoryCount;
import il.swhm.shared.entities.inventorycount.InventoryCountLine;
import il.swhm.shared.enums.EntityStatus;
import il.swhm.shared.enums.ICType;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface InvCountMapper {
	
	public InventoryCount selectInventoryCountByCount(@Param("stationId") int stationId,@Param("countId") int countId,@Param("icStatus") EntityStatus icStatus);
	public InventoryCount selectInventoryCountByCustomer(@Param("stationId") int stationId,@Param("hospitalId") String hospitalId,@Param("customerId") String customerId);
	
	public List<InventoryCount> selectInventoryCountsForTransmit(@Param("stationId") int stationId);
	public List<InventoryCount> selectInventoryCountsForTransmitAndCounts(@Param("stationId") int stationId,@Param("countIds") List<Integer> countIds);
	public int insertInventoryCount(@Param("ic") InventoryCount ic);
	public void insertInventoryCountLine(@Param("ic") InventoryCount ic,@Param("icl") InventoryCountLine icl);
	public void deleteInventoryCountHead(@Param("stationId") int stationId,@Param("countId") int countId);
	public void deleteInventoryCountLines(@Param("stationId") int stationId,@Param("countId") int countId);
	public void updateCountStatusAndApprover(@Param("stationId") int stationId,@Param("countId") int countId,@Param("newStatus") EntityStatus status,@Param("approverId") String approverId);
	public void updateCountType(@Param("stationId") int stationId,@Param("countId") int countId,@Param("type") ICType type);
	
	public void updateCountQuantities(@Param("stationId") int stationId,@Param("countId") int countId);
	public List<HistoryRecord> selectHistory();
}
