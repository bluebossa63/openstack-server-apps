/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.web.account.test;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.User;

/**
 * The Interface ClientInterface.
 * 
 * @author Daniele
 */
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface ClientInterface {
	
	/**
	 * Creates the user.
	 * 
	 * @param tenant
	 *            the tenant
	 * @return the tenant
	 */
	@Path("/tenants")
	@PUT
	public Tenant createUser(Tenant tenant);

	/**
	 * Creates the user.
	 * 
	 * @param user
	 *            the user
	 * @return the user
	 */
	@Path("/users")
	@PUT	
	public User createUser(User user);
}
