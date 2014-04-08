/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.web.account.rest;

import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import ch.niceneasy.openstack.web.account.model.PendingRegistration;
import ch.niceneasy.openstack.web.account.model.PendingRegistrationService;

import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Role;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.User;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;

/**
 * The Class UserResourceRESTService.
 * 
 * @author Daniele
 */
@Path("/users")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@RequestScoped
public class UserResourceRESTService {

	/** The servlet request. */
	@Context
	private HttpServletRequest servletRequest;

	/** The servlet context. */
	@Context
	private ServletContext servletContext;

	/** The pending registration service. */
	@Inject
	PendingRegistrationService pendingRegistrationService;

	/** The roles. */
	@Inject
	private Map<String, Role> roles;

	/** The mail session. */
	@Resource(mappedName = "java:jboss/mail/Default")
	private Session mailSession;

	/** The keystone. */
	@Inject
	private Keystone keystone;

	/** The logger. */
	@Inject
	private Logger logger;

	/** The login confirmation. */
	@Inject
	@CurrentUser
	private LoginConfirmation loginConfirmation;

	/**
	 * Gets the user.
	 * 
	 * @param id
	 *            the id
	 * @param username
	 *            the username
	 * @return the user
	 */
	@GET
	public User getUser(@HeaderParam("id") String id,
			@HeaderParam("username") String username) {
		try {
			User user = null;
			if (id != null) {
				user = keystone.users().show(id).execute();
			} else if (username != null) {
				user = keystone.users().find(username).execute();
			} else {
				throw new RuntimeException("missing parameters");
			}
			logger.info(user.toString());
			return user;
		} catch (OpenStackResponseException e) {
			throw new WebApplicationException(e, e.getStatus());
		}
	}

	/**
	 * Update user.
	 * 
	 * @param pUser
	 *            the user
	 * @return the user
	 */
	@POST
	public User updateUser(User pUser) {
		try {
			User user = keystone.users().update(pUser.getId(), pUser).execute();
			logger.info(user.toString());
			return user;
		} catch (OpenStackResponseException e) {
			throw new WebApplicationException(e, e.getStatus());
		}
	}

	/**
	 * Creates the user.
	 * 
	 * @param pUser
	 *            the user
	 * @return the user
	 */
	@PUT
	public User createUser(User pUser) {
		PendingRegistration pendingRegistration = new PendingRegistration(pUser);
		String token = pendingRegistration.getId();
		token = new String(Hex.encodeHex(token.getBytes()));
		try {
			MimeMessage m = new MimeMessage(mailSession);
			Address from = new InternetAddress("admin@niceneasy.ch");
			Address[] to = new InternetAddress[] { new InternetAddress(
					pUser.getEmail()) };
			m.setFrom(from);
			m.setRecipients(Message.RecipientType.TO, to);
			m.setSubject("JBoss AS 7 Mail");
			m.setSentDate(new java.util.Date());
			m.setContent(
					"Mail sent from JBoss AS 7\n"
							+ "https://openstack.niceneasy.ch:7443"
							+ servletContext.getContextPath()
							+ "/rest/users/confirm?token=" + token,
					"text/plain");
			Transport.send(m);
			pendingRegistrationService.persist(pendingRegistration);

		} catch (Exception e) {
			logger.severe(e.getLocalizedMessage());
			throw new WebApplicationException(e);
		}
		logger.info(pUser.toString());
		return pUser;
	}

	/**
	 * Delete user.
	 * 
	 * @param pUser
	 *            the user
	 */
	@DELETE
	public void deleteUser(User pUser) {
		try {
			keystone.users().delete(pUser.getId()).execute();
		} catch (OpenStackResponseException e) {
			throw new WebApplicationException(e, e.getStatus());
		}
	}

	/**
	 * Confirm user.
	 * 
	 * @param token
	 *            the token
	 * @return the user
	 */
	@Path("/confirm")
	@GET
	public User confirmUser(@QueryParam("token") String token) {
		try {
			String key = null;
			try {
				key = new String(Hex.decodeHex(token.toCharArray()));
			} catch (DecoderException e) {
				throw new RuntimeException(e);
			}
			PendingRegistration pendingRegistration = pendingRegistrationService
					.find(key);
			User user = pendingRegistration.getUser();
			if (user == null) {
				throw new RuntimeException("token does not match");
			}
			Tenant tenant = new Tenant();
			tenant.setName(user.getUsername());
			tenant.setEnabled(true);
			tenant.setDescription("tenant for user " + user.getName());
			tenant = keystone.tenants().create(tenant).execute();
			user.setTenantId(tenant.getId());

			user.setEnabled(true);
			user = keystone.users().create(user).execute();
			// keystone.users().

			keystone.tenants()
					.addUser(tenant.getId(), user.getId(),
							roles.get("admin").getId()).execute();
			keystone.tenants()
					.addUser(tenant.getId(), user.getId(),
							roles.get("Member").getId()).execute();
			logger.info(user.toString());
			pendingRegistrationService.remove(pendingRegistration);
			return user;
		} catch (OpenStackResponseException e) {
			throw new WebApplicationException(e, e.getStatus());
		}
	}

	/**
	 * Login.
	 * 
	 * @param pUser
	 *            the user
	 * @return the login confirmation
	 */
	@Path("/login")
	@POST
	public LoginConfirmation login(User pUser) {
		logger.info(pUser.toString());
		// Tenants tenants = keystone.tenants().list().queryParam("name",
		// pUser.getUsername()).execute();
		try {
			Access access = keystone
					.tokens()
					.authenticate(
							new UsernamePassword(pUser.getUsername(), pUser
									.getPassword()))
					.withTenantName(pUser.getUsername()).execute();
			logger.info(access.getUser().toString());
			User user = keystone.users().show(access.getUser().getId())
					.execute();

			loginConfirmation.setUser(user);
			loginConfirmation.setTenantName(pUser.getUsername());
			return loginConfirmation;
		} catch (OpenStackResponseException e) {
			throw new WebApplicationException(e, e.getStatus());
		}
	}

}
