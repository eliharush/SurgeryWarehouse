package il.swhm.data.sap;

import il.swhm.data.DataFacade;
import il.swhm.data.config.CrmJcoConfiguration;
import il.swhm.shared.entities.inventorycount.InventoryCount;
import il.swhm.shared.entities.inventorycount.InventoryCountLine;
import il.swhm.shared.entities.order.PushOrderHead;
import il.swhm.shared.entities.order.PushOrderLine;
import il.swhm.shared.entities.transmit.OrderHead;
import il.swhm.shared.entities.transmit.OrderLine;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class CrmManager {
	private static Logger logger=Logger.getLogger(CrmManager.class);
	private static String DESTINATION_NAME1 = "CRM_AS_WITHOUT_POOL";
	private static String DESTINATION_NAME2 = "CRM_AS_WITH_POOL";
	public static String CUSTOMER_PRODUCT_LIST_KEY="CustomerProduct";
	public static String USERS_LIST_KEY="Users";


	private static CrmManager instance;

	private CrmManager(){

	}

	private void init(){
		Properties connectProperties = new Properties();
		CrmJcoConfiguration conf=DataFacade.getCrmJcoConfiguration();
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, conf.getHost());
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  conf.getSystemNumber());
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, conf.getClient());
		connectProperties.setProperty(DestinationDataProvider.JCO_USER,   conf.getUser());
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, conf.getPassword());
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, conf.getLanguage());

		createDestinationDataFile(DESTINATION_NAME1, connectProperties);
		connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, conf.getPoolCapacity());
		connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,    conf.getPeakLimit());
		createDestinationDataFile(DESTINATION_NAME2, connectProperties);
	}

	private void createDestinationDataFile(String destinationName, Properties connectProperties)
	{
		File destCfg = new File(destinationName+".jcoDestination");
		try
		{
			FileOutputStream fos = new FileOutputStream(destCfg, false);
			connectProperties.store(fos, "for tests only !");
			fos.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to create the destination files", e);
		}
	}



	public static CrmManager getInstance(){
		if(instance==null){
			try{
				instance=new CrmManager();
				instance.init();
			}
			catch (Exception e) {
				logger.error("Could not init Sap Manager.",e);
				return null;
			}
		}
		return instance;
	}


	public OrderHead sendCountOrders(List<InventoryCount> counts,int transmitId, String user) throws Exception{
		OrderHead order=new OrderHead();
		if(counts==null || counts.size()==0){
			return null;
		}
		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME1);
		CrmJcoConfiguration conf=DataFacade.getCrmJcoConfiguration();
		logger.debug("Got destination ["+DESTINATION_NAME1+"] for CRM with user ["+conf.getUser()+"].");
		JCoFunction function = destination.getRepository().getFunction(DataFacade.getCrmJcoConfiguration().getOrderBapi());
		if (function == null)
			throw new RuntimeException(" RFC ["+DataFacade.getCrmJcoConfiguration().getOrderBapi()+"] not found in CRM swhm SYS.");
		logger.debug("Success in connecting to BAPI DataFacade.getCrmJcoConfiguration().getOrderBapi()");
		//simple fields:
		function.getImportParameterList().setValue("IV_SOLD_TO_PARTY", counts.get(0).getCustomer().getHospital().getId());
		function.getImportParameterList().setValue("IV_MOBILE_ID", StringUtils.leftPad(String.valueOf(counts.get(0).getStationId()),10,"0"));
		function.getImportParameterList().setValue("IV_MOBILE_USER_NAME",user );
		function.getImportParameterList().setValue("IV_MOBILE_PROCESS_IDX", transmitId);
		function.getImportParameterList().setValue("IV_EXEC_IN_BACKGROUND", "X");
		//new 10/7/14	IV_PROCESS_TYPE for InvCount
		String eType=counts.get(0).getType().toString();
		if (eType.equals("FULL"))
			eType="FULL_COUNT";
		else
			eType="PART_COUNT";
		function.getImportParameterList().setValue("IV_PROCESS_TYPE", eType);
		
		order.setHospitalId(counts.get(0).getCustomer().getHospital().getId());
		order.setStationId(counts.get(0).getStationId());
		order.setTransmitId(transmitId);
		order.setUserName(user);
		List<OrderLine> lines=new ArrayList<OrderLine>();
		JCoTable orders = function.getImportParameterList().getTable("IT_ITEM_DATA");
		SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormatter=new SimpleDateFormat("HHmmss");
		int count=0;
		
		
		
		
		for(InventoryCount ic:counts){
			//Alternatives finding
			Map<String, InventoryCountLine> altLines=new HashMap<String,InventoryCountLine>();
			//get all lines into an hashmap
			for(InventoryCountLine icl:ic.getLines()){
				////if(icl.getProduct().getMitadef().equals("כ"))
					altLines.put(icl.getProduct().getProductId(), icl);
			}
			
			//run through lines again and fix alternate quantities
			for(InventoryCountLine icl:ic.getLines()){
				//if(icl.getProduct().getAlternative()!=null || altLines.get(icl.getProduct().getAlternative())!=null){
				if(icl.getProduct().getAlternative()!=null && altLines.get(icl.getProduct().getAlternative())!=null){	
					//fix only lines that has alternative and we have counted this alternative
					InventoryCountLine altLine = altLines.get(icl.getProduct().getAlternative());
		
//  Lead product order minus all count qty alter products will be the new order
							double altQty = altLine.getOrderQty()-icl.getCountQty(); //calc same percent in alternative
					
						
							if(altQty >0){
								
								if(altQty >=altLine.getProduct().getStdQuantity())
									//altLine.setOrderQty(altLine.getProduct().getStdQuantity());
									icl.setOrderQty(altLine.getProduct().getStdQuantity());
								else
									//altLine.setOrderQty(altQty);	
									icl.setOrderQty(altQty);
							}
							else{
								
								//altLine.setOrderQty(0);
								icl.setOrderQty(0);
							}
							//update the lead
					//	altLines.put(altLine.getProduct().getProductId(), altLine);//30/9 done switch
							//put the master po
							//altLines.put(icl.getProduct().getProductId(), altLine);
							altLines.put(icl.getProduct().getProductId(), icl);
						logger.info(icl.getProduct().getProductId()+"newOrderQty"+icl.getOrderQty());
//rmove the alternative lines connected to Lead product
					//altLines.remove(icl.getProduct().getProductId());//kick this line out;//30/9 done switch
					altLines.remove(altLine.getProduct().getProductId());//kick this line out
					}
			}//end For
			
			//Prepare orders
			for(InventoryCountLine icl:altLines.values()){
				if(icl.getOrderQty()>0){
					double orderQty=icl.getOrderQty();
					double minimalQty=icl.getProduct().getMinimalOrderCount();
					if( orderQty<=minimalQty){
						icl.setOrderQty(minimalQty);
					}
					else{
						double div=Math.round(orderQty/minimalQty);
						icl.setOrderQty(div*minimalQty);
					}
					orders.insertRow(count);
					orders.setValue("SHIP_TO_PARTY", ic.getCustomer().getId());
					orders.setValue("swhm_MATERIAL", icl.getProduct().getProductId());
					if(icl.getProduct().getFactor()==0){
						orders.setValue("QUAN_FOR_SALES", icl.getOrderQty());
					}
					else{
						orders.setValue("QUAN_FOR_SALES", icl.getOrderQty()/icl.getProduct().getFactor());
					}
					orders.setValue("UOM_FOR_SALES", icl.getProduct().getSalesUnit());
					orders.setValue("DOCUMENT_TYPE", icl.getProduct().getDocType());
					orders.setValue("ITEM_CATEGORY", icl.getProduct().getCategory());
					orders.setValue("LOCATION_IDX", icl.getProduct().getId());
					orders.setValue("CUST_MATERIAL", icl.getProduct().getCustProductId());// add 07/10/14 elih
					orders.setValue("MATERIAL_DESC", icl.getProduct().getCustProductDesc());//add 07/10/14 elih
					orders.setValue("STANDARD_QUAN", icl.getProduct().getStdQuantity());//add 07/10/14 elih
					orders.setValue("COUNTED_QUAN", icl.getCountQty());//add 07/10/14 elih
					//set time stamp
					if(icl.getCountTime()==null){
						icl.setCountTime(new Date());
					}
					orders.setValue("COUNTED_ON", dateFormatter.format(icl.getCountTime()));
					orders.setValue("COUNTED_AT", timeFormatter.format(icl.getCountTime()));
					
					OrderLine line=new OrderLine();
					line.setCustomerId(ic.getCustomer().getId());
					line.setDocType(icl.getProduct().getDocType());
					line.setProdCategory(icl.getProduct().getCategory());
					line.setProductId(icl.getProduct().getProductId());
					line.setProductRunningId(icl.getProduct().getId());
					if(icl.getProduct().getFactor()==0){
						line.setSalesQty(icl.getOrderQty());
					}
					else{
						line.setSalesQty(icl.getOrderQty()/icl.getProduct().getFactor());
					}
					line.setSalesUnit(icl.getProduct().getSalesUnit());
					lines.add(line);
					
					count++;
				}
			}
		}
		order.setLines(lines);
		if(lines.size()==0){
			return order;//no lines to transmit - set it as Ok
		}
		function.getImportParameterList().setValue("IT_ITEM_DATA", orders);
		
		
		function.execute(destination);
		logger.debug("EV_CRM_PROCESS_IDX"+function.getExportParameterList().getString("EV_CRM_PROCESS_IDX"));
		
		logger.debug("Function executed. Hospital ["+counts.get(0).getCustomer().getHospital().getId()+"] " +
				"StationId ["+StringUtils.leftPad(String.valueOf(counts.get(0).getStationId()),10,"0")+"] " +
				"User ["+user+"] TransmitId ["+transmitId+"]");
		//Receiving flag 'X'  IT'S CORRECT MEANS NO ERRORS
		if(!function.getExportParameterList().getString("EV_RCV_SUCCESS_FLAG").equals("X")){
			JCoTable codes = function.getExportParameterList().getTable("ET_ERROR_MSG");
			StringBuffer sb=new StringBuffer();
			for (int i = 0; i < codes.getNumRows(); i++) 
			{
				codes.setRow(i);
				sb.append(codes.getString("ERROR_MSG")).append(" ");
			}
			logger.warn("Received error from Bapi error code ["+function.getExportParameterList().getString("EV_RCV_SUCCESS_FLAG")+"] message ["+sb.toString()+"]");
			throw new RuntimeException(sb.toString());
		}
		
		return order;
	}

//Send history count new function RFC 21-10-2014 
	public OrderHead sendHistoryCount(List<InventoryCount>  counts,int transmitId, String user) throws Exception{
 OrderHead order=new OrderHead();
	if(counts==null ){
			return null;
		}
		String rfcName="ZMOBILE_EXPORT_COUNTING_DATA";// constant 
		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME1);
		CrmJcoConfiguration conf=DataFacade.getCrmJcoConfiguration();
		logger.debug("Got destination ["+DESTINATION_NAME1+"] for CRM with user ["+conf.getUser()+"].");
		JCoFunction function = destination.getRepository().getFunction(rfcName);
		if (function == null)
			throw new RuntimeException(" RFC ["+rfcName+"] not found in CRM swhm SYS.");
		logger.debug("Success in connecting to BAPI "+rfcName);
		//simple fields:
		/*:
			14.	IV_PROCESS_TYPE (סוג תהליך (ערכים קבועים), CHAR10)
			15.	IV_SOLD_TO_PARTY (מס' לקוח, CHAR10)
			16.	IV_MOBILE_ID (מזהה Mobile, CHAR10)
			17.	IV_MOBILE_USER_NAME (שם המשתמש שנשלח ה-Mobile, CHAR12)
			18.	IV_MOBILE_PROCESS_IDX (מזהה התהליך ב-Mobile (נומרטור),  NUMC10)
			19.	IT_COUNTING_DATA 
*/
		function.getImportParameterList().setValue("IV_SOLD_TO_PARTY", counts.get(0).getCustomer().getHospital().getId());
		function.getImportParameterList().setValue("IV_MOBILE_ID", StringUtils.leftPad(String.valueOf(counts.get(0).getStationId()),10,"0"));
		function.getImportParameterList().setValue("IV_MOBILE_USER_NAME",user );
		function.getImportParameterList().setValue("IV_MOBILE_PROCESS_IDX", transmitId);
		//function.getImportParameterList().setValue("IV_EXEC_IN_BACKGROUND", "X");
		//new 10/7/14	IV_PROCESS_TYPE for InvCount
	
		String eType=counts.get(0).getType().toString();
		if (eType.equals("FULL"))
			eType="FULL_COUNT";
		else
			eType="PART_COUNT";
		function.getImportParameterList().setValue("IV_PROCESS_TYPE", eType);
		
		order.setHospitalId(counts.get(0).getCustomer().getHospital().getId());
		order.setStationId(counts.get(0).getStationId());
		order.setTransmitId(transmitId);
		order.setUserName(user);
		List<OrderLine> lines=new ArrayList<OrderLine>();
		JCoTable orders = function.getImportParameterList().getTable("IT_COUNTING_DATA");
		SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormatter=new SimpleDateFormat("HHmmss");
		int count=0;
		
	//run for all lines
		for(InventoryCount ic:counts){
			//Alternatives finding
			Map<String, InventoryCountLine> altLines=new HashMap<String,InventoryCountLine>();
			Map<String, String> altgroups=new HashMap<String,String>();
			int countGroup=0;
			String prodGroupValue="";
			//get all lines into an hashmap
			for(InventoryCountLine icl:ic.getLines()){
				////if(icl.getProduct().getMitadef().equals("כ"))
					altLines.put(icl.getProduct().getProductId(), icl);
			}
			
			//run through lines again and fix alternate quantities
			for(InventoryCountLine icl:ic.getLines()){
				
				//Handle group Id for alternate 
				if(icl.getProduct().getAlternative()!=null && altLines.get(icl.getProduct().getAlternative())!=null){	
							countGroup++;
							InventoryCountLine altLine = altLines.get(icl.getProduct().getAlternative());				
							altgroups.put(icl.getProduct().getProductId(), Integer.toString(countGroup));
							altgroups.put(altLine.getProduct().getProductId(), Integer.toString(countGroup));//update to alternate
								
					}
				
			}//end For
		//	Map<String, InventoryCountLine> altLines=new HashMap<String,InventoryCountLine>();
			//get all lines into an hashmap
			for(InventoryCountLine icl:ic.getLines()){
				//if(icl.getOrderQty()>0){
				  	orders.insertRow(count);
					orders.setValue("SHIP_TO_PARTY", ic.getCustomer().getId());
					orders.setValue("swhm_MATERIAL", icl.getProduct().getProductId());
					
					orders.setValue("CUST_UOM", icl.getProduct().getCustomerUnit());
					orders.setValue("CUST_MATERIAL", icl.getProduct().getCustProductId());// add 07/10/14 elih
					orders.setValue("MATERIAL_DESC", icl.getProduct().getCustProductDesc());//add 07/10/14 elih
					orders.setValue("STANDARD_QUAN", icl.getProduct().getStdQuantity());//add 07/10/14 elih
					orders.setValue("FLOOR", icl.getProduct().getLot1());//add 07/10/14 elih
					orders.setValue("CUPBOARD", icl.getProduct().getLot2());//add 07/10/14 elih
					orders.setValue("SHELF", icl.getProduct().getLot3());//add 07/10/14 elih
					orders.setValue("COUNTED_QUAN", icl.getCountQty());//add 07/10/14 elih
					//set time stamp
					if(icl.getCountTime()==null){
						icl.setCountTime(new Date());
					}
					orders.setValue("COUNTED_ON", dateFormatter.format(icl.getCountTime()));
					orders.setValue("COUNTED_AT", timeFormatter.format(icl.getCountTime()));	
					orders.setValue("SIGNED_BY", ic.getApprover().getId());//6.13.	SIGNED_BY changed by ELIH 14-01-2015
					prodGroupValue=altgroups.get(icl.getProduct().getProductId());
					if (prodGroupValue==null)
					{
						countGroup++;
						orders.setValue("TEMP_GROUP_ID", Integer.toString(countGroup));
					}
					else
					{
						orders.setValue("TEMP_GROUP_ID", prodGroupValue);
					}
					count++;
				//}
			}
		}
	
		function.getImportParameterList().setValue("IT_COUNTING_DATA", orders);
		
		
		function.execute(destination);
	//	logger.debug("EV_CRM_PROCESS_IDX"+function.getExportParameterList().getString("EV_CRM_PROCESS_IDX"));
		
		logger.debug("Function executed. customer history  ["+counts.get(0).getCustomer().getId()+"] " +
				"StationId ["+StringUtils.leftPad(String.valueOf(counts.get(0).getStationId()),10,"0")+"] " +
				"User ["+user+"] TransmitId ["+transmitId+"]");
		//Receiving flag 'X'  IT'S CORRECT MEANS NO ERRORS
		if(!function.getExportParameterList().getString("EV_RCV_SUCCESS_FLAG").equals("X")){
			JCoTable codes = function.getExportParameterList().getTable("ET_ERROR_MSG");
			StringBuffer sb=new StringBuffer();
			for (int i = 0; i < codes.getNumRows(); i++) 
			{
				codes.setRow(i);
				sb.append(codes.getString("ERROR_MSG")).append(" ");
			}
			logger.warn("Received error from Bapi error code ["+function.getExportParameterList().getString("EV_RCV_SUCCESS_FLAG")+"] message ["+sb.toString()+"]");
			throw new RuntimeException(sb.toString());
		}
		
		return order;
	}

//	
	public boolean isInLan(){
		try {
			InetAddress address = InetAddress.getByName(DataFacade.getCrmJcoConfiguration().getHost());
			return address.isReachable(DataFacade.getCrmJcoConfiguration().getConnectTestTimeout());
		}
		catch (Exception e) {
			return false;
		}
	}

	public OrderHead sendOrders(List<PushOrderHead> orders, int transmitId,
			String user) throws Exception {
		OrderHead crmOrder=new OrderHead();
		if(orders==null || orders.size()==0){
			return null;
		}
		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME1);
		CrmJcoConfiguration conf=DataFacade.getCrmJcoConfiguration();
		logger.debug("Got destination ["+DESTINATION_NAME1+"] for CRM with user ["+conf.getUser()+"].");
		JCoFunction function = destination.getRepository().getFunction(DataFacade.getCrmJcoConfiguration().getOrderBapi());
		if (function == null)
			throw new RuntimeException(" RFC ["+DataFacade.getCrmJcoConfiguration().getOrderBapi()+"] not found in CRM swhm SYS.");
		logger.debug("Success in connecting to BAPI DataFacade.getCrmJcoConfiguration().getOrderBapi()");
		//simple fields:
		function.getImportParameterList().setValue("IV_SOLD_TO_PARTY", orders.get(0).getCustomer().getHospital().getId());
		function.getImportParameterList().setValue("IV_MOBILE_ID", StringUtils.leftPad(String.valueOf(orders.get(0).getStationId()),10,"0"));
		function.getImportParameterList().setValue("IV_MOBILE_USER_NAME",user );
		function.getImportParameterList().setValue("IV_MOBILE_PROCESS_IDX", transmitId);
		function.getImportParameterList().setValue("IV_EXEC_IN_BACKGROUND", "X");
		//new 10/7/14	IV_PROCESS_TYPE for InvCount
		String eType=orders.get(0).getType().toString();
		if (eType.equals("REGULAR"))
			eType="PLANNED";
		else{
			if (eType.equals("DESK"))
				eType="DESK";
			else
				eType="URGENT";
		}
	    function.getImportParameterList().setValue("IV_PROCESS_TYPE", eType);
	
		
		crmOrder.setHospitalId(orders.get(0).getCustomer().getHospital().getId());
		crmOrder.setStationId(orders.get(0).getStationId());
		crmOrder.setTransmitId(transmitId);
		crmOrder.setUserName(user);
		List<OrderLine> crmOrderLines=new ArrayList<OrderLine>();
		JCoTable ordersTable = function.getImportParameterList().getTable("IT_ITEM_DATA");
		
		int count=0;
		for(PushOrderHead order:orders){
			for(PushOrderLine orderLine:order.getLines()){
				if(orderLine.getOrderQty()>0){
					
					ordersTable.insertRow(count);
					ordersTable.setValue("SHIP_TO_PARTY", order.getCustomer().getId());
					ordersTable.setValue("swhm_MATERIAL", orderLine.getProduct().getProductId());
					if(orderLine.getProduct().getFactor()==0 || orderLine.isswhmProduct()){
						ordersTable.setValue("QUAN_FOR_SALES", orderLine.getOrderQty());
					}
					else{
						ordersTable.setValue("QUAN_FOR_SALES", orderLine.getOrderQty()/orderLine.getProduct().getFactor());
					}
					ordersTable.setValue("UOM_FOR_SALES", orderLine.getProduct().getSalesUnit());
					ordersTable.setValue("DOCUMENT_TYPE", orderLine.getProduct().getDocType());
					ordersTable.setValue("ITEM_CATEGORY", orderLine.getProduct().getCategory());
					ordersTable.setValue("LOCATION_IDX", orderLine.getProduct().getId());
					ordersTable.setValue("CUST_MATERIAL", orderLine.getProduct().getCustProductId());// add 07/10/14 elih
					ordersTable.setValue("MATERIAL_DESC", orderLine.getProduct().getCustProductId());//add 07/10/14 elih
					ordersTable.setValue("STANDARD_QUAN", orderLine.getProduct().getStdQuantity());//add 07/10/14 elih
					//ordersTable.setValue("COUNTED_QUAN", "0.000");//add 07/10/14 elih
					OrderLine line=new OrderLine();
					line.setCustomerId(order.getCustomer().getId());
					line.setDocType(orderLine.getProduct().getDocType());
					line.setProdCategory(orderLine.getProduct().getCategory());
					line.setProductId(orderLine.getProduct().getProductId());
					line.setProductRunningId(orderLine.getProduct().getId());
					if(orderLine.getProduct().getFactor()==0 || orderLine.isswhmProduct()){
						line.setSalesQty(orderLine.getOrderQty());
					}
					else{
						line.setSalesQty(orderLine.getOrderQty()/orderLine.getProduct().getFactor());
					}
					line.setSalesUnit(orderLine.getProduct().getSalesUnit());
					crmOrderLines.add(line);
					logger.debug("adding line. product ["+line.getProductId()+"] sales qty ["+line.getSalesQty()+"] customer ["+line.getCustomerId()+"]");
					count++;
				}
			}
		}
		crmOrder.setLines(crmOrderLines);
		if(crmOrderLines.size()==0){
			return crmOrder;//no lines to transmit - set it as Ok
		}
		function.getImportParameterList().setValue("IT_ITEM_DATA", ordersTable);
		
		
		function.execute(destination);
		logger.debug("EV_CRM_PROCESS_IDX:  "+function.getExportParameterList().getString("EV_CRM_PROCESS_IDX"));	
		logger.debug("Function executed. Hospital ["+orders.get(0).getCustomer().getHospital().getId()+"] " +
				"StationId ["+StringUtils.leftPad(String.valueOf(orders.get(0).getStationId()),10,"0")+"] " +
				"User ["+user+"] TransmitId ["+transmitId+"]");
		//Receiving flag 'X'  IT'S CORRECT MEANS NO ERRORS
		if(!function.getExportParameterList().getString("EV_RCV_SUCCESS_FLAG").equals("X")){
			JCoTable codes = function.getExportParameterList().getTable("ET_ERROR_MSG");
			StringBuffer sb=new StringBuffer();
			for (int i = 0; i < codes.getNumRows(); i++) 
			{
				codes.setRow(i);
				sb.append(codes.getString("ERROR_MSG")).append(" ");
			}
			logger.warn("Received error from Bapi error code ["+function.getExportParameterList().getString("EV_RCV_SUCCESS_FLAG")+"] message ["+sb.toString()+"]");
			throw new RuntimeException(sb.toString());
		}
		
		
			
		return crmOrder;
	}
}
