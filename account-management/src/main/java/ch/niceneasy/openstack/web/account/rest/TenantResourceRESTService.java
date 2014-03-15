package ch.niceneasy.openstack.web.account.rest;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.woorea.openstack.keystone.model.Tenant;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members table.
 */
@Path("/tenants")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class TenantResourceRESTService {
	
	   @PUT
	   public Tenant createUser(Tenant tenant) {
		   System.out.println(tenant.toString());
		   return null;
	   }

}
