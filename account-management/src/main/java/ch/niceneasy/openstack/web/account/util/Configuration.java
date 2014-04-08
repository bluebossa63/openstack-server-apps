/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.web.account.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import ch.niceneasy.openstack.web.account.rest.CurrentUser;
import ch.niceneasy.openstack.web.account.rest.LoginConfirmation;

import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Role;
import com.woorea.openstack.keystone.model.Roles;
import com.woorea.openstack.keystone.model.User;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;

/**
 * The Class Configuration.
 * 
 * @author Daniele
 */
@ApplicationScoped
@Startup
public class Configuration {

	/** The pending confirmations. */
	Map<String, User> pendingConfirmations = new HashMap<String, User>();

	/** The configuration. */
	private Map<String, String> configuration = new HashMap<String, String>();

	/** The roles. */
	private Map<String, Role> roles = new HashMap<String, Role>();

	/** The instance. */
	public static Configuration INSTANCE;

	/**
	 * Instantiates a new configuration.
	 */
	public Configuration() {
		INSTANCE = this;
	}

	/** The logger. */
	@Inject
	private Logger logger;

	/**
	 * Fetch configuration.
	 */
	@PostConstruct
	public void fetchConfiguration() {
		Properties props = new Properties();
		try {
			props.load(Resources.class.getClassLoader().getResourceAsStream(
					"openstack.properties"));
			for (Entry<Object, Object> entry : props.entrySet()) {
				configuration.put(entry.getKey().toString(), entry.getValue()
						.toString());
			}
			Roles rolesList = getKeystone(null).roles().list().execute();
			for (Role role : rolesList) {
				roles.put(role.getName(), role);
			}
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}

	/**
	 * Gets the login information.
	 * 
	 * @param point
	 *            the point
	 * @return the login information
	 */
	@Produces
	@CurrentUser
	LoginConfirmation getLoginInformation(InjectionPoint point) {
		LoginConfirmation loginConfirmation = new LoginConfirmation();
		loginConfirmation.setCeilometerEndpoint(configuration
				.get("ceilometerEndpoint"));
		loginConfirmation.setKeystoneAdminAuthUrl(configuration
				.get("keystoneExternalAdminAuthUrl"));
		loginConfirmation.setKeystoneAuthUrl(configuration
				.get("keystoneExternalAuthUrl"));
		loginConfirmation.setKeystoneEndpoint(configuration
				.get("keystoneExternalEndpoint"));
		loginConfirmation.setNovaEndpoint(configuration.get("novaEndpoint"));
		loginConfirmation.setTenantName(configuration.get("tenantName"));
		loginConfirmation.setSwiftUrl(configuration
				.get("swiftExternalEndpoint"));
		return loginConfirmation;
	}

	/**
	 * Gets the string.
	 * 
	 * @param point
	 *            the point
	 * @return the string
	 */
	@Produces
	public String getString(InjectionPoint point) {
		String fieldName = point.getMember().getName();
		String valueForFieldName = configuration.get(fieldName);
		return valueForFieldName;
	}

	/**
	 * Gets the integer.
	 * 
	 * @param point
	 *            the point
	 * @return the integer
	 */
	@Produces
	public int getInteger(InjectionPoint point) {
		fetchConfiguration();
		String stringValue = getString(point);
		return Integer.parseInt(stringValue);
	}

	/**
	 * Gets the keystone.
	 * 
	 * @param point
	 *            the point
	 * @return the keystone
	 */
	@Produces
	public Keystone getKeystone(InjectionPoint point) {
		Keystone keystone = new Keystone(configuration.get("keystoneAuthUrl"));
		Access access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(configuration
								.get("keystoneUsername"), configuration
								.get("keystonePassword")))
				.withTenantName("admin").execute();
		keystone = new Keystone(configuration.get("keystoneAdminAuthUrl"));
		keystone.setTokenProvider(new OpenStackSimpleTokenProvider(access
				.getToken().getId()));
		return keystone;
	}

	/**
	 * Gets the pending confirmations.
	 * 
	 * @param point
	 *            the point
	 * @return the pending confirmations
	 */
	@Produces
	public Map<String, User> getPendingConfirmations(InjectionPoint point) {
		return pendingConfirmations;
	}

	/**
	 * Reset connection.
	 */
	public void resetConnection() {
	}

	/**
	 * Gets the roles.
	 * 
	 * @return the roles
	 */
	@Produces
	public Map<String, Role> getRoles() {
		return roles;
	}

}
