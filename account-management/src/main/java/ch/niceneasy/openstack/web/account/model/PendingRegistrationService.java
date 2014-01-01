package ch.niceneasy.openstack.web.account.model;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
public class PendingRegistrationService {

	@Inject
	private EntityManager em;

	public PendingRegistration find(String token) {
		return em.find(PendingRegistration.class, token);
	}

	public void persist(PendingRegistration pendingRegistration) {
		em.persist(pendingRegistration);
	}

	public void remove(PendingRegistration pendingRegistration) {
		PendingRegistration persistent = em.find(PendingRegistration.class, pendingRegistration.getId());
		em.remove(persistent);
	}

}
