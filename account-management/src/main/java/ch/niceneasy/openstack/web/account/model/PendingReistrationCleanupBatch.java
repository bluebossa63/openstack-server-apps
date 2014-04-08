/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.web.account.model;

import java.util.Calendar;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * The Class PendingReistrationCleanupBatch.
 * 
 * @author Daniele
 */
@Singleton
public class PendingReistrationCleanupBatch {

	/** The em. */
	@Inject
	private EntityManager em;

	/**
	 * Clean up.
	 */
	@Schedule
	public void cleanUp() {

		Query q = em.createNamedQuery("findExpiredRegistrations");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1);
		q.setParameter("exipiration", cal.getTime());
		q.executeUpdate();

	}
}
