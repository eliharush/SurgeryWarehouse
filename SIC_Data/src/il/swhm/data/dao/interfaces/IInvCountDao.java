package il.swhm.data.dao.interfaces;

import il.swhm.shared.entities.inventorycount.InventoryCount;
import il.swhm.shared.enums.EntityStatus;
import il.swhm.shared.enums.ICType;


public interface IInvCountDao {
	
	public InventoryCount getInventoryCount(int stationId,int countId,EntityStatus status);
	public InventoryCount addInventoryCount(InventoryCount inventoryCount);
	public void saveCount(InventoryCount inventoryCount);
	public void finishCount(InventoryCount inventoryCount);
	public void cancelCount(int stationId,int countId);
	
	public void restartCount(int stationId,int countId,ICType type,boolean deleteCurrent);
	
	
}
