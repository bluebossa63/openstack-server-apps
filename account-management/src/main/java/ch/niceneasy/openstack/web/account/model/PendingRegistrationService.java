/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.web.account.model;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * The Class PendingRegistrationService.
 * 
 * @author Daniele
 */
@Stateless
public class PendingRegistrationService {

	/** The em. */
	@Inject
	private EntityManager em;

	/**
	 * Find.
	 * 
	 * @param token
	 *            the token
	 * @return the pending registration
	 */
	public PendingRegistration find(String token) {
		return em.find(PendingRegistration.class, token);
	}

	/**
	 * Persist.
	 * 
	 * @param pendingRegistration
	 *            the pending registration
	 */
	public void persist(PendingRegistration pendingRegistration) {
		em.persist(pendingRegistration);
	}

	/**
	 * Removes the.
	 * 
	 * @param pendingRegistration
	 *            the pending registration
	 */
	public void remove(PendingRegistration pendingRegistration) {
		PendingRegistration persistent = em.find(PendingRegistration.class,
				pendingRegistration.getId());
		em.remove(persistent);
	}

}
