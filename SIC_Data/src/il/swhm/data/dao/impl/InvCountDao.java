package il.swhm.data.dao.impl;

import il.swhm.data.MyBatisUtil;
import il.swhm.data.dao.interfaces.IInvCountDao;
import il.swhm.data.mappers.InvCountMapper;
import il.swhm.shared.entities.inventorycount.InventoryCount;
import il.swhm.shared.entities.inventorycount.InventoryCountLine;
import il.swhm.shared.enums.EntityStatus;
import il.swhm.shared.enums.ICType;

import org.apache.ibatis.session.SqlSession;

public class InvCountDao implements IInvCountDao {

	@Override
	public InventoryCount getInventoryCount(int stationId, int countId,EntityStatus status) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			InvCountMapper mapper=session.getMapper(InvCountMapper.class);
			return mapper.selectInventoryCountByCount(stationId,countId,status);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public InventoryCount addInventoryCount(InventoryCount inventoryCount) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			InvCountMapper mapper=session.getMapper(InvCountMapper.class);
			InventoryCount existingIC= mapper.selectInventoryCountByCustomer(inventoryCount.getStationId(), inventoryCount.getCustomer().getHospital().getId(),inventoryCount.getCustomer().getId());
			if(existingIC==null){
				mapper.insertInventoryCount(inventoryCount);
			}
			else{
				mapper.deleteInventoryCountLines(existingIC.getStationId(),existingIC.getCountId());
				mapper.updateCountType(existingIC.getStationId(), existingIC.getCountId(), inventoryCount.getType());
				inventoryCount.setCountId(existingIC.getCountId());
			}
			session.commit();
			return inventoryCount;
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public void saveCount(InventoryCount inventoryCount) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			InvCountMapper mapper=session.getMapper(InvCountMapper.class);
			mapper.deleteInventoryCountLines(inventoryCount.getStationId(),inventoryCount.getCountId());
			for(InventoryCountLine line:inventoryCount.getLines()){
				mapper.insertInventoryCountLine(inventoryCount, line);
			}
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public void finishCount(InventoryCount inventoryCount) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			InvCountMapper mapper=session.getMapper(InvCountMapper.class);
			mapper.deleteInventoryCountLines(inventoryCount.getStationId(),inventoryCount.getCountId());
			boolean needOrder=false;
			for(InventoryCountLine line:inventoryCount.getLines()){
				mapper.insertInventoryCountLine(inventoryCount, line);
				//if(line.getOrderQty()>0){ //elih is needed to send history
					needOrder=true;
				//}
			}
			EntityStatus status=(needOrder) ? EntityStatus.COMPLETED : EntityStatus.WO_ORDER;
			mapper.updateCountStatusAndApprover(inventoryCount.getStationId(),inventoryCount.getCountId(), status,(inventoryCount.getApprover()!=null) ? inventoryCount.getApprover().getId() : null);
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}


	@Override
	public void restartCount(int stationId, int countId,ICType type,boolean deleteCurrent) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			InvCountMapper mapper=session.getMapper(InvCountMapper.class);
			if(deleteCurrent){
				mapper.deleteInventoryCountLines(stationId,countId);
				mapper.deleteInventoryCountHead(stationId, countId);
			}
			else{
				mapper.updateCountStatusAndApprover(stationId, countId, EntityStatus.IN_PROGRESS,null);
			}
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}

	}


	@Override
	public void cancelCount(int stationId, int countId) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			InvCountMapper mapper=session.getMapper(InvCountMapper.class);
			mapper.updateCountStatusAndApprover(stationId, countId, EntityStatus.CANCELED,null);
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}		
	}


}
