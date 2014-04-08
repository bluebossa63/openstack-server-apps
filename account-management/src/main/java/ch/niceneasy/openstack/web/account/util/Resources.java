/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.web.account.util;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * The Class Resources.
 * 
 * @author Daniele
 */
public class Resources {
	// use @SuppressWarnings to tell IDE to ignore warnings about field not
	// being referenced directly
	/** The em. */
	@SuppressWarnings("unused")
	@Produces
	@PersistenceContext
	private EntityManager em;

	/**
	 * Produce log.
	 * 
	 * @param injectionPoint
	 *            the injection point
	 * @return the logger
	 */
	@Produces
	public Logger produceLog(InjectionPoint injectionPoint) {
		return Logger.getLogger(injectionPoint.getMember().getDeclaringClass()
				.getName());
	}
}
