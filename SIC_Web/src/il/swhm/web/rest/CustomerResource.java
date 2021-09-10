package il.swhm.web.rest;

import il.swhm.data.dao.impl.GeneralDao;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.product.ManProduct;
import il.swhm.shared.entities.product.MaraManProduct;
import il.swhm.shared.entities.product.Product;
import il.swhm.web.config.SICConfiguration;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

@Path("/customer")
public class CustomerResource {

	private static Logger logger=Logger.getLogger(CustomerResource.class);
	private static String EMPTY_SEARCH="__EMPTY__";
	private UriInfo ui;
	private String hospitalId;
	
	public CustomerResource(@Context UriInfo ui) {
		this.ui=ui;
		hospitalId=this.ui.getPathParameters().get("hospitalId").get(0);
	}

	@Path("/{customerId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerById(@PathParam("customerId") String customerId){
		try{
			if(customerId.equals(EMPTY_SEARCH)){
				customerId="";
			}
			customerId="%" + customerId + "%";
			GeneralDao dao=new GeneralDao();
			List<Customer> customers= dao.getCustomer(SICConfiguration.getInstance().getStationId(),hospitalId, customerId);
			return Response.ok().entity(customers).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	@Path("/{customerId}/products")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerProducts(@PathParam("customerId") String customerId){
		try{
			GeneralDao dao=new GeneralDao();
			List<Product> products= dao.getCustomerProducts(hospitalId, customerId);
			return Response.ok().entity(products).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	//new
	@Path("/{productId}/hospitalProducts")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHospitalProducts(@PathParam("productId") String productId){
		try{
			GeneralDao dao=new GeneralDao();
			List<Product> products= dao.getHospitalProducts(hospitalId, productId);
			return Response.ok().entity(products).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	@Path("/{customerId}/manProducts")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerManProducts(@PathParam("customerId") String customerId){
		try{
			GeneralDao dao=new GeneralDao();
			List<ManProduct> manProducts= dao.getCustomerManProducts(hospitalId, customerId);
			return Response.ok().entity(manProducts).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	@Path("/{customerId}/maraManProducts")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerMaraManProducts(@PathParam("customerId") String customerId){
		try{
			GeneralDao dao=new GeneralDao();
			List<MaraManProduct> manProducts= dao.getMaraManProducts(hospitalId, customerId);
			return Response.ok().entity(manProducts).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	
	
	
	
}
