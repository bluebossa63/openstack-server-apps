package ch.niceneasy.openstack.web.account.model;

import java.util.Calendar;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Singleton
public class PendingReistrationCleanupBatch {

	@Inject
	private EntityManager em;

	@Schedule
	public void cleanUp() {

		Query q = em.createNamedQuery("findExpiredRegistrations");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1);
		q.setParameter("exipiration", cal.getTime());
		q.executeUpdate();

	}
}
