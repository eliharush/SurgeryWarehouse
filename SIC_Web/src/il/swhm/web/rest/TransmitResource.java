package il.swhm.web.rest;

import il.swhm.data.dao.impl.GeneralDao;
import il.swhm.shared.entities.Transmitable;
import il.swhm.shared.enums.EntityType;
import il.swhm.web.config.SICConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

@Path("/transmit")
public class TransmitResource {
	private static String DELIMITER="_";
	private static Logger logger=Logger.getLogger(TransmitResource.class);
	@Context UriInfo ui;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response transmitICS(){
		try{
			List<String> entitiesStr= ui.getQueryParameters().get("entity");
			
			Map<Integer,EntityType> entities=new HashMap<Integer,EntityType>();
			for(String entityStr:entitiesStr){
				String[] arr = entityStr.split(DELIMITER);
				entities.put(Integer.valueOf(arr[1]),EntityType.valueOf(arr[0]));
			}
			GeneralDao dao=new GeneralDao();
			String user=(SICConfiguration.getInstance().getUser().equals(""))?"mobile" : SICConfiguration.getInstance().getUser();
			boolean res=dao.transmitCounts(SICConfiguration.getInstance().getStationId(), entities,user);
			if(res){
				return Response.ok().build();
			}
			else{
				return Response.serverError().build();
			}

		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEntitiesForTtransmit(){
		try{
			GeneralDao dao=new GeneralDao();
			List<Transmitable> ics=dao.getEntitiesForTransmit(SICConfiguration.getInstance().getStationId());
			return Response.ok().entity(ics).build();
		}
		catch (Exception e) {
			logger.error("Exception occured",e);
			return Response.serverError().build();
		}
	}

}
