package il.swhm.web.rest;

import il.swhm.data.DataFacade;
import il.swhm.data.sap.SapManager;
import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.product.CustomerProduct;
import il.swhm.shared.entities.product.ManProduct;
import il.swhm.shared.entities.product.MaraManProduct;
import il.swhm.shared.entities.product.SubProduct;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

@Path("/import")
public class ImportResource {

	private static Logger logger=Logger.getLogger(ImportResource.class);
	
	@Path("/connected")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response isConnected(){
		try{
			boolean connected=false;
			if(SapManager.getInstance().isInLan()){
				connected=true;
			}
			return Response.ok().entity(connected).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response importDataFromSap(){
		try{
			if(!SapManager.getInstance().isInLan()){
				logger.warn("User not in lan");
				return Response.serverError().entity("Communication Error").build();
			}
			HashMap<String, List<? extends Object>> dataMap= SapManager.getInstance().getDataList();
			List<Customer> customers=new ArrayList<Customer>();
			List<CustomerProduct> customerProducts=(List<CustomerProduct>) dataMap.get(SapManager.CUSTOMER_PRODUCT_LIST_KEY);
			List<ManProduct> manProducts=(List<ManProduct>) dataMap.get(SapManager.MAN_PRODUCT_LIST_KEY);
			List<SubProduct> subProducts=(List<SubProduct>)dataMap.get(SapManager.SUB_PRODUCT_LIST_KEY);
			List<MaraManProduct> maraManProducts = (List<MaraManProduct>)dataMap.get(SapManager.MARA_MAN_PRODUCTS_LIST_KEY);
			for(CustomerProduct cp:customerProducts){
				if(!customers.contains(cp.getCustomer())){
					customers.add(cp.getCustomer());
				}
			}
			
			DataFacade.getImportDao().insertCustomers(customers);
			DataFacade.getImportDao().insertProducts(customerProducts);
			DataFacade.getImportDao().insertManProducts(manProducts);
			DataFacade.getImportDao().insertSubProducts(subProducts);
			DataFacade.getImportDao().insertMaraManProducts(maraManProducts);
			DataFacade.getImportDao().insertApprovers((List<Approver>) dataMap.get(SapManager.USERS_LIST_KEY));
			DataFacade.getImportDao().setLastLoadTime();
			return Response.ok().build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	@GET
	@Path("/last")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLastUpdateDate(){
		try{
			boolean isSameDay;
			Date lastDate= DataFacade.getImportDao().getLastLoadTime();
			if(lastDate==null){
				isSameDay=false;
			}
			else{
				isSameDay=DateUtils.isSameDay(new Date(),lastDate);
			}

			return Response.ok().entity(isSameDay).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	
}
