package il.swhm.data.sap;

import il.swhm.data.DataFacade;
import il.swhm.data.config.SapJcoConfiguration;
import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.Hospital;
import il.swhm.shared.entities.product.CustomerProduct;
import il.swhm.shared.entities.product.ManProduct;
import il.swhm.shared.entities.product.MaraManProduct;
import il.swhm.shared.entities.product.Product;
import il.swhm.shared.entities.product.SubProduct;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.text.DefaultEditorKit.CutAction;

import org.apache.log4j.Logger;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.google.common.collect.*;


public class SapManager {
	public static final String MARA_MAN_PRODUCTS_LIST_KEY = "MaraManProducts";
	private static Logger logger=Logger.getLogger(SapManager.class);
	private static String DESTINATION_NAME1 = "ABAP_AS_WITHOUT_POOL";
	private static String DESTINATION_NAME2 = "ABAP_AS_WITH_POOL";
	public static String CUSTOMER_PRODUCT_LIST_KEY="CustomerProduct";
	public static String MAN_PRODUCT_LIST_KEY="ManProduct";
	public static String SUB_PRODUCT_LIST_KEY="SubProducts";
	public static String USERS_LIST_KEY="Users";
	public static String HOSPITAL_ID="124"; ///124=Poria,193=Icilov
	public static String WERKS="6000"; //Poria=6000, iCLIOV=5000
	public static String KUNAG="";//pORIA=124, iCILOV=193


	private static SapManager instance;

	private SapManager(){

	}

	private void init(){
		Properties connectProperties = new Properties();
		SapJcoConfiguration conf=DataFacade.getSapJcoConfiguration();
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, conf.getHost());
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  conf.getSystemNumber());
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, conf.getClient());
		connectProperties.setProperty(DestinationDataProvider.JCO_USER,   conf.getUser());
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, conf.getPassword());
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, conf.getLanguage());
		connectProperties.setProperty(DestinationDataProvider.JCO_CODEPAGE, conf.getCodePage());
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


	///
	//Change to support for multiple hospitals   change set number  : 10500
	///
	private JCoTable getCustomerAndProducts() throws JCoException{
		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME1);
		JCoFunction function = destination.getRepository().getFunction(DataFacade.getSapJcoConfiguration().getHospitalDataBapi());
		if (function == null)
			throw new RuntimeException(" RFC ["+DataFacade.getSapJcoConfiguration().getHospitalDataBapi()+"] not found in SAP swhm SYS.");
		try
		{
			//Add hospital id parameters
			///
			//Change to support for multiple hospitals
			///
			function.getImportParameterList().setValue("SOLD_TO",HOSPITAL_ID);
			//
			function.execute(destination);
		}
		catch(AbapException e)
		{
			throw new RuntimeException(e);
		}

		JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
		if (! (returnStructure.getString("TYPE").equals("")||returnStructure.getString("TYPE").equals("S"))  )
		{
			throw new RuntimeException(returnStructure.getString("MESSAGE"));
		}
		return function.getTableParameterList().getTable(DataFacade.getSapJcoConfiguration().getHospitalTableName());

	}
	
	private JCoTable getManufactorProducts() throws JCoException{
		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME1);
		JCoFunction function = destination.getRepository().getFunction(DataFacade.getSapJcoConfiguration().getManDataBapi());
		if (function == null)
			throw new RuntimeException(" RFC ["+DataFacade.getSapJcoConfiguration().getManDataBapi()+"] not found in SAP swhm SYS.");
		try
		{
			//Add hospital id parameters
			///
			//Change to support for multiple hospitals
			///
			function.getImportParameterList().setValue("SOLD_TO",HOSPITAL_ID);
			//
			function.execute(destination);
		}
		catch(AbapException e)
		{
			throw new RuntimeException(e);
		}

		JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
		if (! (returnStructure.getString("TYPE").equals("")|| returnStructure.getString("TYPE").equals("S"))  )
		{
			throw new RuntimeException(returnStructure.getString("MESSAGE"));
		}
		return function.getTableParameterList().getTable(DataFacade.getSapJcoConfiguration().getManTableName());

	}
	
	private JCoTable getMaraManufactorProducts() throws JCoException{
		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME1);
		JCoFunction function = destination.getRepository().getFunction(DataFacade.getSapJcoConfiguration().getMaraManDataBapi());
		if (function == null)
			throw new RuntimeException(" RFC ["+DataFacade.getSapJcoConfiguration().getMaraManDataBapi()+"] not found in SAP swhm SYS.");
		try
		{
			//Add hospital id parameters
			///
			//Change to support for multiple hospitals
			///
			function.getImportParameterList().setValue("WERKS",WERKS);
			//
			function.execute(destination);
		}
		catch(AbapException e)
		{
			throw new RuntimeException(e);
		}

		JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
		if (! (returnStructure.getString("TYPE").equals("")|| returnStructure.getString("TYPE").equals("S"))  )
		{
			throw new RuntimeException(returnStructure.getString("MESSAGE"));
		}
		return function.getTableParameterList().getTable(DataFacade.getSapJcoConfiguration().getMaraManTableName());

	}
	
	private JCoTable getSubstitutesProducts() throws JCoException{
		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME1);
		JCoFunction function = destination.getRepository().getFunction(DataFacade.getSapJcoConfiguration().getSubstituteDataBapi());
		if (function == null)
			throw new RuntimeException(" RFC ["+DataFacade.getSapJcoConfiguration().getSubstituteDataBapi()+"] not found in SAP swhm SYS.");
		try
		{
			//Add hospital id parameters
			///
			//Change to support for multiple hospitals
			///
			function.getImportParameterList().setValue("KUNAG",KUNAG);
			//
			function.execute(destination);
		}
		catch(AbapException e)
		{
			throw new RuntimeException(e);
		}

		JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
		if (! (returnStructure.getString("TYPE").equals("")|| returnStructure.getString("TYPE").equals("S"))  )
		{
			throw new RuntimeException(returnStructure.getString("MESSAGE"));
		}
		return function.getTableParameterList().getTable(DataFacade.getSapJcoConfiguration().getSubstituteTableName());
	}

	private JCoTable getAlternativProducts() throws JCoException{
		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME1);
		JCoFunction function = destination.getRepository().getFunction(DataFacade.getSapJcoConfiguration().getAlternativeDataBapi());
		if (function == null)
			throw new RuntimeException(" RFC ["+DataFacade.getSapJcoConfiguration().getAlternativeDataBapi()+"] not found in SAP swhm SYS.");
		try
		{
			function.execute(destination);
		}
		catch(AbapException e)
		{
			throw new RuntimeException(e);
		}

		JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
		if (! (returnStructure.getString("TYPE").equals("")|| returnStructure.getString("TYPE").equals("S"))  )
		{
			throw new RuntimeException(returnStructure.getString("MESSAGE"));
		}
		return function.getTableParameterList().getTable(DataFacade.getSapJcoConfiguration().getAlternativTableName());
	}

	
	
	private JCoTable getUsers() throws JCoException{
		JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME1);
		JCoFunction function = destination.getRepository().getFunction("ZWM_USERS_GET_DATA");
		if (function == null)
			throw new RuntimeException(" RFC ZWM_USERS_GET_DATA not found in SAP swhm SYS.");
		try
		{
			function.execute(destination);
		}
		catch(AbapException e)
		{
			throw new RuntimeException(e);
		}

		JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
		if (! (returnStructure.getString("TYPE").equals("")||returnStructure.getString("TYPE").equals("S"))  )
		{
			throw new RuntimeException(returnStructure.getString("MESSAGE"));
		}

		JCoTable codes = function.getTableParameterList().getTable(DataFacade.getSapJcoConfiguration().getUsersTableName());
		return codes;

	}


	public static SapManager getInstance(){
		if(instance==null){
			try{
				instance=new SapManager();
				instance.init();
			}
			catch (Exception e) {
				logger.error("Could not init Sap Manager.",e);
				return null;
			}
		}
		return instance;
	}




	public HashMap<String, List<? extends Object>> getDataList() throws Exception
	{
		HashMap<String, List<? extends Object>> res=new HashMap<String,List<? extends Object>>();
		List<CustomerProduct> products=new ArrayList<CustomerProduct>(); 
		JCoTable codes = getCustomerAndProducts();
		Double minOrderCount;
		for (int i = 0; i < codes.getNumRows(); i++)
		{
			codes.setRow(i);
			//if(codes.getString("MATNR_MITADEF").equals("כ")){
				Customer c=new Customer();
				Hospital h=new Hospital();
				h.setId(codes.getString("SOLD_TO"));
				c.setHospital(h);
				c.setId(codes.getString("SHIP_TO"));
				c.setName(codes.getString("NAME1"));
				Product p=new Product();
				p.setCategory(codes.getString("PSTYV"));
				p.setCustomerUnit(codes.getString("VRKME2"));
				p.setCustProductDesc(codes.getString("MAKTX"));
				p.setCustProductId(codes.getString("KDMAT"));
				p.setDocType(codes.getString("AUART"));
				p.setFactor(Double.valueOf(codes.getString("FACTOR")));
				p.setId(Integer.valueOf(codes.getString("RATZ")));
				p.setLot1(Integer.valueOf(codes.getString("KUNN2")));
				p.setLot2(Integer.valueOf(codes.getString("ARON")));
				p.setLot3(Integer.valueOf(codes.getString("MADAF")));
				p.setMitadef(codes.getString("MATNR_MITADEF"));
				p.setProductId(codes.getString("MATNR"));
				p.setProductType(codes.getString("MATNR_TYPE"));
				p.setSalesUnit(codes.getString("VRKME3"));
				p.setSite(codes.getString("WERKS"));
				p.setStdQuantity(Double.valueOf(codes.getString("KWMENG")));
				p.setCustomerUnitDesc(codes.getString("TVRKME2"));
				p.setSalesUnitDesc(codes.getString("TVRKME3"));
				p.setCustProductDesc2(codes.getString("MAKT2"));//ELIH 26/07/14
				minOrderCount = Double.valueOf(codes.getString("AUMNG"));
				p.setMinimalOrderCount(p.getFactor() * minOrderCount);
				
				p.setCommentCode(codes.getString("ZCOMMENT"));
				p.setComment(codes.getString("ZCOMMENTX35"));
				p.setCountFactor(Double.valueOf(codes.getString("FACTOR35")).intValue());
				CustomerProduct cp=new CustomerProduct();
				cp.setCustomer(c);
				cp.setProduct(p);
				products.add(cp);
			//}
		}


		
		//get manufactur code
		List<ManProduct> manProducts=new ArrayList<ManProduct>();
		JCoTable manProductsTable = getManufactorProducts();
		for(int i=0;i < manProductsTable.getNumRows(); i++){
			manProductsTable.setRow(i);
			ManProduct m=new ManProduct();
			m.setCustProductId(manProductsTable.getString("KDMAT"));
			m.setManProductId(manProductsTable.getString("MAKAT_YATZRAN"));
			m.setProductId(manProductsTable.getString("MATNR"));
			manProducts.add(m);
			
		}
		
		res.put(MAN_PRODUCT_LIST_KEY, manProducts);
		
		
		//get substitute code
		List<SubProduct> subProducts=new ArrayList<SubProduct>();
		JCoTable subProductTable = getSubstitutesProducts();
		for(int i=0;i < subProductTable.getNumRows(); i++){
			subProductTable.setRow(i);
			SubProduct m=new SubProduct();
			m.setProductId(subProductTable.getString("MATNR"));
			m.setSubProductId(subProductTable.getString("MATNR_ALTERNATIV"));
			subProducts.add(m);
			
		}
		
		res.put(SUB_PRODUCT_LIST_KEY, subProducts);
		
		
		//get  multi alterntives
		Multimap<String, String> alternatives = ArrayListMultimap.create();
		//Map<String,String> alternatives=new HashMap<String,String>();
		JCoTable altProductsTable = getAlternativProducts();
		for(int i=0;i < altProductsTable.getNumRows(); i++){
			altProductsTable.setRow(i);
		//	if alternatives.get(altProductsTable.getString("MATNR_ALTERNATIV")))
			alternatives.put( altProductsTable.getString("MATNR"),altProductsTable.getString("MATNR_ALTERNATIV"));
		}
		
		//get only one per prod  alterntives
		//Map<String,String> alternatives=new HashMap<String,String>();
		///JCoTable altProductsTable = getAlternativProducts();
		//for(int i=0;i < altProductsTable.getNumRows(); i++){
	//		altProductsTable.setRow(i);
	
	//		alternatives.put(altProductsTable.getString("MATNR_ALTERNATIV"), altProductsTable.getString("MATNR"));
	//	}
		
		
		//build product list
		//List<CustomerProduct> custProducts=new ArrayList<CustomerProduct>();
		//Map<String,String> cProducts=new HashMap<String,String>();
		Multimap<String, String> cProducts = ArrayListMultimap.create();
		//List<Product> copyProducts=new ArrayList<Product>();
		for(CustomerProduct cp2 : products){
			if (cp2.getProduct().getMitadef().equals("כ")){
				//if (cProducts.get(cp2.getProduct().getProductId())!=cp2.getCustomer().getId())
					cProducts.put(cp2.getProduct().getProductId(),cp2.getCustomer().getId());
			}
		}//end for 
		
		List<CustomerProduct> updatedProducts=new ArrayList<CustomerProduct>();
		updatedProducts.clear();
		for(CustomerProduct cp : products){
			
			Product p = cp.getProduct();
			Collection<String> alters= alternatives.get(p.getProductId());
			Iterator<String> altIter = alters.iterator();
			//get the favorite alternative product
	
			
			while (altIter.hasNext()){
				 String altProd = altIter.next();
		if (altProd!=null){
				 Collection  altcustomer=cProducts.get(altProd);
				 String customercp= cp.getCustomer().getId();
			if (altcustomer!=null && customercp!=null){
				if (altcustomer.contains(customercp) )
				{
					p.setAlternative(altProd);
					
					
				}
			}
			}
			}
				cp.setProduct(p);
			if (!updatedProducts.contains(cp))
				updatedProducts.add(cp);
			
		}
		
		res.put(CUSTOMER_PRODUCT_LIST_KEY,updatedProducts);
		
		
		//get mara manufactur code
		List<MaraManProduct> maraManProducts=new ArrayList<MaraManProduct>();
		JCoTable maraManProductsTable = getMaraManufactorProducts();
		for(int i=0;i < maraManProductsTable.getNumRows(); i++){
			maraManProductsTable.setRow(i);
			MaraManProduct m=new MaraManProduct();
			m.setManProductId(maraManProductsTable.getString("MFRPN").trim());
			m.setProductId(maraManProductsTable.getString("MATNR").trim());
			m.setManufactur(maraManProductsTable.getString("MFRNR").trim());
			maraManProducts.add(m);
		}
		
		res.put(MARA_MAN_PRODUCTS_LIST_KEY, maraManProducts);
		
		
		JCoTable users=getUsers();
		List<Approver> approvers=new ArrayList<Approver>();



		for (int i = 0; i < users.getNumRows(); i++)
		{
			users.setRow(i);
			Approver approver=new Approver();
			approver.setId(users.getString("USRID"));
			approver.setPassword(users.getString("PASSWORD"));
			approvers.add(approver);
		}

		res.put(USERS_LIST_KEY, approvers);
		return res;
	}

	public boolean isInLan(){
		try {
			InetAddress address = InetAddress.getByName(DataFacade.getSapJcoConfiguration().getHost());
			return address.isReachable(DataFacade.getSapJcoConfiguration().getConnectTestTimeout());
		}
		catch (Exception e) {
			return false;
		}
	}




}
