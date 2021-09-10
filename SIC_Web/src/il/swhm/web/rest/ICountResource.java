package il.swhm.web.rest;


import il.swhm.data.dao.impl.GeneralDao;
import il.swhm.data.dao.impl.InvCountDao;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.Hospital;
import il.swhm.shared.entities.inventorycount.InventoryCount;
import il.swhm.shared.entities.inventorycount.InventoryCountLine;
import il.swhm.shared.entities.product.Product;
import il.swhm.shared.enums.EntityStatus;
import il.swhm.shared.enums.ICType;
import il.swhm.web.config.SICConfiguration;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

@Path("/count")
public class ICountResource {

	private static Logger logger=Logger.getLogger(ICountResource.class);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNewInventoryCount(@QueryParam("hospitalId") String hospitalId,@QueryParam("customerId") String customerId,@QueryParam("type") ICType type){
		try{
			InvCountDao dao=new InvCountDao();
			GeneralDao gdao = new GeneralDao();
			InventoryCount ic=new InventoryCount();
			Hospital h=new Hospital();
			h.setId(hospitalId);
			Customer c=new Customer();
			c.setHospital(h);
			c.setId(customerId);
			ic.setCustomer(c);
			ic.setType(type);
			ic.setStationId(SICConfiguration.getInstance().getStationId());

			dao.addInventoryCount(ic);

			//if full - add all items
			if(type.equals(ICType.FULL)){
				List<Product> products = gdao.getCustomerProducts(hospitalId, customerId);
				List<InventoryCountLine> lines=new ArrayList<InventoryCountLine>();
				for(Product product:products){
					if(product.getMitadef().equals("כ")){
						InventoryCountLine line=new InventoryCountLine();
						line.setProduct(product);
						line.setCountQty(-1);
						line.setOrderQty(-1);
						lines.add(line);
					}
				}
				ic.setLines(lines);
				dao.saveCount(ic);
			}

			ic=dao.getInventoryCount(SICConfiguration.getInstance().getStationId(), ic.getCountId(),EntityStatus.IN_PROGRESS);
			return Response.ok().entity(ic).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}

	@Path("/{countId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInventoryCount(@PathParam("countId") int countId,@QueryParam("restart") boolean restart,@QueryParam("type") ICType type,@QueryParam("deleteCurrent") boolean deleteCurrent){
		try{
			InvCountDao dao=new InvCountDao();
			if(restart){
				dao.restartCount(SICConfiguration.getInstance().getStationId(), countId,type,deleteCurrent);
			}
			InventoryCount ic= dao.getInventoryCount(SICConfiguration.getInstance().getStationId(), countId,EntityStatus.IN_PROGRESS);
			return Response.ok().entity(ic).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}


	@Path("/{countId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInventoryCount(@QueryParam("finish") Boolean finish, InventoryCount ic){
		try{
			InvCountDao dao=new InvCountDao();
			if(finish){
				dao.finishCount(ic);
			}
			else{
				dao.saveCount(ic);
			}
			return Response.ok().build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}

	@Path("/{countId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelInventoryCount(@PathParam("countId") int countId){
		try{
			InvCountDao dao=new InvCountDao();
			dao.cancelCount(SICConfiguration.getInstance().getStationId(), countId);
			return Response.ok().build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}




	@Path("/{countId}/history")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHistoryCount(@PathParam("countId") int countId){
		try{
			InvCountDao dao=new InvCountDao();
			InventoryCount ic= dao.getInventoryCount(SICConfiguration.getInstance().getStationId(), countId,null);
			return Response.ok().entity(ic).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}




}
