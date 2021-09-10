package il.swhm.data.dao.impl;

import il.swhm.data.MyBatisUtil;
import il.swhm.data.dao.interfaces.IOrderDao;
import il.swhm.data.mappers.OrderMapper;
import il.swhm.shared.entities.order.PushOrderHead;
import il.swhm.shared.entities.order.PushOrderLine;
import il.swhm.shared.enums.EntityStatus;

import org.apache.ibatis.session.SqlSession;

public class OrderDao implements IOrderDao {

	@Override
	public PushOrderHead getOrder(int stationId, int orderId,EntityStatus status) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			OrderMapper mapper=session.getMapper(OrderMapper.class);
			return mapper.selectPushOrderByOrder(stationId,orderId,status);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public PushOrderHead addOrder(PushOrderHead order) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			OrderMapper mapper=session.getMapper(OrderMapper.class);
			PushOrderHead existingOrder= mapper.selectPushOrderByCustomer(order.getStationId(), order.getCustomer().getHospital().getId(),order.getCustomer().getId());
			if(existingOrder==null){
				mapper.insertPushOrder(order);
			}
			else{
				mapper.deletePushOrderLines(existingOrder.getStationId(),existingOrder.getOrderId());
				mapper.updateOrderType(existingOrder.getStationId(), existingOrder.getOrderId(), order.getType());
				order.setOrderId(existingOrder.getOrderId());
			}
			session.commit();
			return order;
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public void saveOrder(PushOrderHead order) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			OrderMapper mapper=session.getMapper(OrderMapper.class);
			mapper.deletePushOrderLines(order.getStationId(),order.getOrderId());
			for(PushOrderLine line:order.getLines()){
				mapper.insertPushOrderLine(order, line);
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
	public void finishOrder(PushOrderHead order) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			OrderMapper mapper=session.getMapper(OrderMapper.class);
			mapper.deletePushOrderLines(order.getStationId(),order.getOrderId());
			for(PushOrderLine line:order.getLines()){
				mapper.insertPushOrderLine(order, line);
			}
			EntityStatus status=EntityStatus.COMPLETED;
			mapper.updateOrderStatusAndApprover(order.getStationId(),order.getOrderId(), status,(order.getApprover()!=null) ? order.getApprover().getId() : null);
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}


	@Override
	public void cancelOrder(int stationId, int orderId) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			OrderMapper mapper=session.getMapper(OrderMapper.class);
			mapper.updateOrderStatusAndApprover(stationId, orderId, EntityStatus.CANCELED,null);
			session.commit();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}		
	}


}
