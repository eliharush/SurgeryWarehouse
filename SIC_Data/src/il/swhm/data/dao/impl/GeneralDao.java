package il.swhm.data.dao.impl;

import il.swhm.data.DataFacade;
import il.swhm.data.MyBatisUtil;
import il.swhm.data.config.CrmJcoConfiguration;
import il.swhm.data.dao.interfaces.IGeneralDao;
import il.swhm.data.mappers.BackupMapper;
import il.swhm.data.mappers.GeneralMapper;
import il.swhm.data.mappers.InvCountMapper;
import il.swhm.data.mappers.OrderMapper;
import il.swhm.data.sap.CrmManager;
import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.HistoryRecord;
import il.swhm.shared.entities.Transmitable;
import il.swhm.shared.entities.inventorycount.InventoryCount;
import il.swhm.shared.entities.inventorycount.InventoryCountLine;
import il.swhm.shared.entities.order.PushOrderHead;
import il.swhm.shared.entities.product.ManProduct;
import il.swhm.shared.entities.product.MaraManProduct;
import il.swhm.shared.entities.product.Product;
import il.swhm.shared.entities.transmit.OrderHead;
import il.swhm.shared.entities.transmit.OrderLine;
import il.swhm.shared.entities.transmit.Transmit;
import il.swhm.shared.enums.EntityStatus;
import il.swhm.shared.enums.EntityType;
import il.swhm.shared.enums.TransmitionStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.NMTOKENDatatypeValidator;
import com.sun.org.apache.xml.internal.utils.StylesheetPIHandler;

public class GeneralDao implements IGeneralDao {

	Logger logger=Logger.getLogger(GeneralDao.class);

	@Override
	public List<Customer> getCustomer(int stationId,String hospitalId, String customerId) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			GeneralMapper mapper=session.getMapper(GeneralMapper.class);
			return mapper.selectCustomer(stationId,hospitalId, customerId);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}
	private void sendGet(String cartcode,Date countDate,String statusN) throws Exception {
		 String statusCart="&status="+statusN;//Special status
		 SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd");
		 
		 String nDate=dateFormatter.format(countDate);
		 CrmJcoConfiguration conf=DataFacade.getCrmJcoConfiguration();
		 String url="";
			String confUser=conf.getUser().trim();
			if (confUser.equals("rfc_user"))//production
				 url = "http://10.0.0.24/swhm/report/updateCartTaskFromSap"+"?cartCode="+cartcode+statusCart+"&cartDate="+nDate;
			else	 
				 url = "http://10.0.0.24/swhmTest/report/updateCartTaskFromSap"+"?cartCode="+cartcode+statusCart+"&cartDate="+nDate;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		// optional default is GET
		con.setRequestMethod("GET");
		 
		//add request header
	//	con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = con.getResponseCode();
		logger.debug("\nSending 'GET' request to URL : " + url);
		logger.debug("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		logger.debug("Success update customer cart to finish without order " +cartcode);
		System.out.println(response.toString());
 
	}
	@Override
	public List<Product> getCustomerProducts(String hospitalId, String customerId) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			GeneralMapper mapper=session.getMapper(GeneralMapper.class);
			return mapper.selectProductsForCustomer(hospitalId, customerId);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}
	@Override
	public List<Product> getHospitalProducts(String hospitalId, String productId) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			GeneralMapper mapper=session.getMapper(GeneralMapper.class);
			return mapper.selectProductsForHospital(hospitalId, productId);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}


	@Override
	public List<ManProduct> getCustomerManProducts(String hospitalId,
			String customerId) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			GeneralMapper mapper=session.getMapper(GeneralMapper.class);
			return mapper.selectManProductsForCustomer(hospitalId, customerId);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public boolean transmitCounts(int stationId, Map<Integer,EntityType> entities, String user) {
		SqlSession session=null;
		SqlSession masterSqlSession=null;

		try{
			session=MyBatisUtil.getInstance().openSession();
			InvCountMapper mapper=session.getMapper(InvCountMapper.class);
			OrderMapper orderMapper=session.getMapper(OrderMapper.class);
			GeneralMapper gm=session.getMapper(GeneralMapper.class);
			Transmit transmit=new Transmit();
			gm.insertTransmit(stationId, transmit);
			List<Integer> countIds=new ArrayList<Integer>();
			List<Integer> orderIds=new ArrayList<Integer>();

			for(Integer entityId:entities.keySet()){
				gm.insertTransmitLine(stationId, transmit.getTransmitId(), entityId,entities.get(entityId));
				if(entities.get(entityId).equals(EntityType.COUNT)){
					countIds.add(entityId);
				}
				else{
					orderIds.add(entityId);
				}
			}

			session.commit();
			List<InventoryCount> ics=null;
			List<InventoryCount> ich=null;;//history
			
			
			
			if(countIds.size()>0){
				ics=mapper.selectInventoryCountsForTransmitAndCounts(stationId, countIds);
				ich=mapper.selectInventoryCountsForTransmitAndCounts(stationId, countIds);//for history
			//	ich=mapper.selectInventoryCountByCount(stationId,ics.get(0).getCountId(),EntityStatus.COMPLETED);
				
			
			}
			List<PushOrderHead> orders=null;
			if(orderIds.size()>0){
				orders=orderMapper.selectPushOrdersForTransmitAndOrders(stationId, orderIds);
			}
			OrderHead countOrder=null,historyCount=null;
			OrderHead regularOrder=null;
			try{
				//count order
				if(ics!=null && ics.size()>0){
					
					//step 1 orders
					countOrder= CrmManager.getInstance().sendCountOrders(ics, transmit.getTransmitId(),user);
				
				}
				///Order
				if(orders!=null && orders.size()>0){
					regularOrder= CrmManager.getInstance().sendOrders(orders, transmit.getTransmitId(),user);
				}
				//check for history !!!
				if(ich!=null && ich.size()>0){
					
					//step 2 history
					historyCount= CrmManager.getInstance().sendHistoryCount(ich,transmit.getTransmitId(),user);
				
				
				
				boolean needOrder=false;//History
				for(InventoryCount icx:ich){
					  needOrder=false;//History
					for(InventoryCountLine linex:icx.getLines()){
						if(linex.getOrderQty()>0){ //elih is needed to send history
								needOrder=true;
							}
						}//internal for 
						if(!needOrder){
							sendGet(icx.getCustomer().getId().trim(),icx.getCountDate(),"1");//Inventory count Add date to update function to full order
							sendGet(icx.getCustomer().getId().trim(),icx.getCountDate(),"5");//Finish Add date to update function to full order
							}
						
						 
							
					}//main for
				}//history if
			}
			catch (Exception e) {
				transmit.setErrMessage(e.getMessage());
				transmit.setTransStatus(TransmitionStatus.ERROR);
				gm.updateTransmit(stationId, transmit);
				session.commit();
				return false;
			}
			if(ics!=null){
				for(InventoryCount ic:ics){
					mapper.updateCountStatusAndApprover(stationId, ic.getCountId(), EntityStatus.TRANSMITED,ic.getApprover().getId());
				}
			}
			if(orders!=null){
				for(PushOrderHead order:orders){
					orderMapper.updateOrderStatusAndApprover(stationId, order.getOrderId(), EntityStatus.TRANSMITED,order.getApprover().getId());
				}
			}

			transmit.setTransStatus(TransmitionStatus.OK);
			gm.updateTransmit(stationId, transmit);
			session.commit();
			try{
				masterSqlSession=MyBatisUtil.getInstance("master").openSession();
				BackupMapper backupMapper= masterSqlSession.getMapper(BackupMapper.class);
				if(countOrder!=null){
					backupMapper.insertOrderHead(countOrder);
					for(OrderLine line:countOrder.getLines()){
						backupMapper.insertOrderLine(countOrder, line);
					}
				}
				if(regularOrder!=null){
					backupMapper.insertOrderHead(regularOrder);
					for(OrderLine line:regularOrder.getLines()){
						backupMapper.insertOrderLine(countOrder, line);
					}
				}
				masterSqlSession.commit();
				gm.updateTransmitToMaster(stationId, transmit.getTransmitId());
				session.commit();
			}
			catch (Exception e) {
				logger.error("Transmit was not written to master DB. station id ["+stationId+"] transmit id["+transmit.getTransmitId()+"]",e);
			}
			return true;
		}
		finally{
			if(session!=null){
				session.close();
			}
			if(masterSqlSession!=null){
				masterSqlSession.close();
			}
		}

	}

	@Override
	public List<HistoryRecord> getHistory() {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			InvCountMapper mapper=session.getMapper(InvCountMapper.class);
			OrderMapper orderMapper=session.getMapper(OrderMapper.class);
			List<HistoryRecord> historyRecords = new ArrayList<HistoryRecord>();
			historyRecords.addAll(mapper.selectHistory());
			historyRecords.addAll(orderMapper.selectHistory());

			Collections.sort(historyRecords, new Comparator<HistoryRecord>() {
				public int compare(HistoryRecord m1, HistoryRecord m2) {

					Date date1,date2;
					if(m1.getEntity() instanceof InventoryCount){
						date1 = ((InventoryCount)m1.getEntity()).getCountDate();
					}
					else{
						date1 = ((PushOrderHead)m1.getEntity()).getOrderDate();
					}

					if(m2.getEntity() instanceof InventoryCount){
						date2 = ((InventoryCount)m2.getEntity()).getCountDate();
					}
					else{
						date2 = ((PushOrderHead)m2.getEntity()).getOrderDate();
					}


					return date2.compareTo(date1);
				}
			});



			return historyRecords;

		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}


	@Override
	public List<Approver> getAllApprovers() {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			GeneralMapper mapper=session.getMapper(GeneralMapper.class);
			return mapper.selectApprovers();
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public void cleanDb(int stationId,int period) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			BackupMapper bm=session.getMapper(BackupMapper.class);
			List<HashMap<String, Object>> counts= bm.selectOldCounts(period);
			List<Integer> transmitIds=new ArrayList<Integer>();
			List<Integer> countIds=new ArrayList<Integer>();
			Integer transId,countId;
			if(counts!=null && counts.size()>0){
				for(HashMap<String, Object> rec:counts){

					transId=(rec.get("transmit_id")==null)?null : Integer.valueOf(rec.get("transmit_id").toString());
					countId=Integer.valueOf(rec.get("count_id").toString());
					if(transId!=null && !transmitIds.contains(transId)){
						transmitIds.add(transId);
					}
					if(!countIds.contains(countId)){
						countIds.add(countId);
					}
				}
				if(transmitIds.size()>0){
					bm.deleteTransmitHead(stationId, transmitIds);
					bm.deleteTransmitLine(stationId, transmitIds);
				}
				if(countIds.size()>0){
					bm.deleteCountHead(stationId, countIds);
					bm.deleteCountLine(stationId, countIds);
				}
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
	public Product selectGeneralProductForCustProductId(String custProductId) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			GeneralMapper mapper=session.getMapper(GeneralMapper.class);
			List<Product> products=mapper.selectGeneralProductByCustProductId(custProductId);
			return (products==null || products.size()==0) ? null : products.get(0);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public List<Transmitable> getEntitiesForTransmit(int stationId) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			List<Transmitable> entities=new ArrayList<Transmitable>();
			InvCountMapper mapper=session.getMapper(InvCountMapper.class);
			OrderMapper orderMapper=session.getMapper(OrderMapper.class);
			entities.addAll(mapper.selectInventoryCountsForTransmit(stationId));
			entities.addAll(orderMapper.selectPushOrdersForTransmit(stationId));

			//sort entities by date
			Collections.sort(entities, new Comparator<Transmitable>() {
				public int compare(Transmitable m1, Transmitable m2) {

					Date date1,date2;
					if(m1 instanceof InventoryCount){
						date1 = ((InventoryCount)m1).getCountDate();
					}
					else{
						date1 = ((PushOrderHead)m1).getOrderDate();
					}

					if(m2 instanceof InventoryCount){
						date2 = ((InventoryCount)m2).getCountDate();
					}
					else{
						date2 = ((PushOrderHead)m2).getOrderDate();
					}


					return date2.compareTo(date1);
				}
			});


			return entities;
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}

	@Override
	public List<MaraManProduct> getMaraManProducts(String hospitalId,
			String customerId) {
		SqlSession session=null;
		try{
			session=MyBatisUtil.getInstance().openSession();
			GeneralMapper mapper=session.getMapper(GeneralMapper.class);
			return mapper.selectMaraProducts(hospitalId, customerId);
		}
		finally{
			if(session!=null){
				session.close();
			}
		}
	}



}
