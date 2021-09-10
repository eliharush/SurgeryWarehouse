package il.swhm.web.rest;

import il.swhm.data.dao.impl.GeneralDao;
import il.swhm.shared.entities.Approver;
import il.swhm.shared.entities.HistoryRecord;
import il.swhm.shared.entities.product.Product;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

@Path("/general")
public class GeneralResource {
	private static Logger logger=Logger.getLogger(GeneralResource.class);
	
	@Path("/approvers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllApprovers(){
		try{
			GeneralDao dao=new GeneralDao();
			List<Approver> approvers=dao.getAllApprovers();
			return Response.ok().entity(approvers).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	@Path("/history")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHistory(){
		try{
			GeneralDao dao=new GeneralDao();
			List<HistoryRecord> hrec=dao.getHistory();
			return Response.ok().entity(hrec).build();

		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	@Path("/generalProduc/{custProductId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGeneralProduct(@PathParam("custProductId") String custProductId){
		try{
			GeneralDao dao=new GeneralDao();
			Product prod= dao.selectGeneralProductForCustProductId(custProductId);
			return Response.ok().entity(prod).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}

}
