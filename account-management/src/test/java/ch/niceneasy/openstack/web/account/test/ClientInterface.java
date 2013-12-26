package ch.niceneasy.openstack.web.account.test;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.User;

@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface ClientInterface {
	
	@Path("/tenants")
	@PUT
	public Tenant createUser(Tenant tenant);

	@Path("/users")
	@PUT	
	public User createUser(User user);
}
