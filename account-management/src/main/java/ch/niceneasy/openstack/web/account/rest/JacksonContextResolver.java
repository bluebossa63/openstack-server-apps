/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.web.account.rest;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * The Class JacksonContextResolver.
 * 
 * @author Daniele
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

	/** The default mapper. */
	private ObjectMapper defaultMapper;

	/** The wrapped mapper. */
	private ObjectMapper wrappedMapper;

	/**
	 * Instantiates a new jackson context resolver.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public JacksonContextResolver() throws Exception {
		defaultMapper = new ObjectMapper();
		wrappedMapper = new ObjectMapper();
		defaultMapper.setSerializationInclusion(Include.NON_NULL);
		defaultMapper.enable(SerializationFeature.INDENT_OUTPUT);
		defaultMapper
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		defaultMapper
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		wrappedMapper.setSerializationInclusion(Include.NON_NULL);
		wrappedMapper.enable(SerializationFeature.INDENT_OUTPUT);
		wrappedMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		wrappedMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		wrappedMapper
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		wrappedMapper
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		ResteasyProviderFactory resteasyProviderFactory = ResteasyProviderFactory
				.getInstance();
		resteasyProviderFactory
				.addMessageBodyReader(new ResteasyJackson2Provider());
		resteasyProviderFactory
				.addMessageBodyWriter(new ResteasyJackson2Provider());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.ContextResolver#getContext(java.lang.Class)
	 */
	@Override
	public ObjectMapper getContext(Class<?> objectType) {
		System.out
				.println(objectType.getAnnotation(JsonRootName.class) == null);
		return objectType.getAnnotation(JsonRootName.class) == null ? defaultMapper
				: wrappedMapper;
	}
}
