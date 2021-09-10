package il.swhm.web.rest;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/hospital")
public class HospitalResource {
	private @Context UriInfo ui;
	
	@Path("/{hospitalId}/customer")
	public CustomerResource getCustomerResource(@PathParam("hospitalId") String hospitalId){
		return new CustomerResource(ui);
	}
	
}
