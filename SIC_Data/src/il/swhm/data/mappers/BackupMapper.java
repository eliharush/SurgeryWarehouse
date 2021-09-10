package il.swhm.data.mappers;

import java.util.HashMap;
import java.util.List;

import il.swhm.shared.entities.transmit.OrderHead;
import il.swhm.shared.entities.transmit.OrderLine;

import org.apache.ibatis.annotations.Param;

public interface BackupMapper {
	public void insertOrderHead(@Param("ord") OrderHead ord);

	public void insertOrderLine(@Param("ord") OrderHead ord,@Param("orl") OrderLine orl);
	
	public List<HashMap<String, Object>> selectOldCounts(@Param("period") Integer period);
	public void deleteTransmitHead(@Param("stationId") int stationId,@Param("transmitIds") List<Integer> transmitIds);
	public void deleteTransmitLine(@Param("stationId") int stationId,@Param("transmitIds") List<Integer> transmitIds);
	public void deleteCountHead(@Param("stationId") int stationId,@Param("countIds") List<Integer> countIds);
	public void deleteCountLine(@Param("stationId") int stationId,@Param("countIds") List<Integer> countIds);
}
