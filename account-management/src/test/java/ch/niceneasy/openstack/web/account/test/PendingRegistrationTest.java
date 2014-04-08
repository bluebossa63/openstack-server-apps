/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.web.account.test;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.osgi.spi.ManifestBuilder;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.niceneasy.openstack.web.account.model.PendingRegistration;
import ch.niceneasy.openstack.web.account.model.PendingRegistrationService;
import ch.niceneasy.openstack.web.account.util.Resources;

import com.woorea.openstack.keystone.model.Extra;
import com.woorea.openstack.keystone.model.User;

/**
 * The Class PendingRegistrationTest.
 * 
 * @author Daniele
 */
@RunWith(Arquillian.class)
public class PendingRegistrationTest {
	
	/**
	 * Creates the test archive.
	 * 
	 * @return the archive
	 */
	@Deployment
	public static Archive<?> createTestArchive() {
		MavenDependencyResolver resolver = DependencyResolvers.use(
				MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");
		Archive<WebArchive> war = ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClasses(PendingRegistration.class,
						PendingRegistrationService.class, User.class,
						Extra.class, Resources.class)
				.addAsResource("META-INF/test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				// Deploy our test datasource
				.addAsWebInfResource("test-ds.xml")
				.addAsLibraries(
						resolver.artifact("com.woorea:keystone-model")
								.resolveAsFiles())
				.addAsLibraries(
						resolver.artifact("commons-lang:commons-lang")
								.resolveAsFiles()).setManifest(new Asset() {
					@Override
					public InputStream openStream() {
						ManifestBuilder builder = ManifestBuilder.newInstance();
						String osgidep = "org.osgi.core,org.jboss.osgi.framework";
						String apidep = ",org.jboss.msc";
						builder.addManifestHeader("Dependencies", osgidep
								+ apidep);
						return builder.openStream();
					}
				});
		return war;
	}

	/** The pending registration service. */
	@Inject
	PendingRegistrationService pendingRegistrationService;

	/** The log. */
	@Inject
	Logger log;

	/**
	 * Test register.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testRegister() throws Exception {
		User user = new User();
		user.setEmail("daniele.ulrich@niceneasy.ch");
		PendingRegistration pendingRegistration = new PendingRegistration(user);
		pendingRegistrationService.persist(pendingRegistration);
		assertNotNull(pendingRegistration.getId());
		log.info(pendingRegistration.getUser() + " was persisted with id "
				+ pendingRegistration.getId());
		pendingRegistration = pendingRegistrationService
				.find(pendingRegistration.getId());
		log.info(pendingRegistration.getUser() + " fetched with id "
				+ pendingRegistration.getId());
	}

}
