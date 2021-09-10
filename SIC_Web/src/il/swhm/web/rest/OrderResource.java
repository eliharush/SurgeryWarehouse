package il.swhm.web.rest;


import il.swhm.data.dao.impl.OrderDao;
import il.swhm.shared.entities.Customer;
import il.swhm.shared.entities.Hospital;
import il.swhm.shared.entities.order.PushOrderHead;
import il.swhm.shared.enums.EntityStatus;
import il.swhm.shared.enums.OrderType;
import il.swhm.web.config.SICConfiguration;

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

@Path("/order")
public class OrderResource {

	private static Logger logger=Logger.getLogger(OrderResource.class);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNewOrder(@QueryParam("hospitalId") String hospitalId,@QueryParam("customerId") String customerId,@QueryParam("type") OrderType type){
		try{
			OrderDao dao=new OrderDao();
			PushOrderHead order=new PushOrderHead();
			Hospital h=new Hospital();
			h.setId(hospitalId);
			Customer c=new Customer();
			c.setHospital(h);
			c.setId(customerId);
			order.setCustomer(c);
			order.setType(type);
			order.setStationId(SICConfiguration.getInstance().getStationId());

			dao.addOrder(order);


			order=dao.getOrder(SICConfiguration.getInstance().getStationId(), order.getOrderId(),EntityStatus.IN_PROGRESS);
			return Response.ok().entity(order).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}

	@Path("/{orderId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrder(@PathParam("orderId") int orderId,@QueryParam("type") OrderType type){
		try{
			OrderDao dao=new OrderDao();
			PushOrderHead order= dao.getOrder(SICConfiguration.getInstance().getStationId(), orderId,EntityStatus.IN_PROGRESS);
			return Response.ok().entity(order).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}


	@Path("/{orderId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateOrder(@QueryParam("finish") Boolean finish, PushOrderHead order){
		try{
			OrderDao dao=new OrderDao();
			if(finish){
				dao.finishOrder(order);
			}
			else{
				dao.saveOrder(order);
			}
			return Response.ok().build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	@Path("/{orderId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelOrder(@PathParam("orderId") int orderId){
		try{
			OrderDao dao=new OrderDao();
			dao.cancelOrder(SICConfiguration.getInstance().getStationId(), orderId);
			return Response.ok().build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	

	
	@Path("/{orderId}/history")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHistoryOrder(@PathParam("orderId") int orderId){
		try{
			OrderDao dao=new OrderDao();
			PushOrderHead order= dao.getOrder(SICConfiguration.getInstance().getStationId(), orderId,null);
			return Response.ok().entity(order).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	


}
