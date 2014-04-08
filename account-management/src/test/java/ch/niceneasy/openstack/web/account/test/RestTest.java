/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.web.account.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.User;

/**
 * The Class RestTest.
 * 
 * @author Daniele
 */
public class RestTest {

	/** The default mapper. */
	static ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

	/** The wrapped mapper. */
	static ObjectMapper WRAPPED_MAPPER = new ObjectMapper();

	static {

		DEFAULT_MAPPER.setSerializationInclusion(Include.NON_NULL);
		DEFAULT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		DEFAULT_MAPPER
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		DEFAULT_MAPPER
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		WRAPPED_MAPPER.setSerializationInclusion(Include.NON_NULL);
		WRAPPED_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		WRAPPED_MAPPER.enable(SerializationFeature.WRAP_ROOT_VALUE);
		WRAPPED_MAPPER.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		WRAPPED_MAPPER
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		WRAPPED_MAPPER
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	}

	/**
	 * Gets the context.
	 * 
	 * @param type
	 *            the type
	 * @return the context
	 */
	public static ObjectMapper getContext(Class<?> type) {
		return type.getAnnotation(JsonRootName.class) == null ? DEFAULT_MAPPER
				: DEFAULT_MAPPER;
	}

	/**
	 * The Class Client.
	 */
	public class Client implements ClientInterface {

		/* (non-Javadoc)
		 * @see ch.niceneasy.openstack.web.account.test.ClientInterface#createUser(com.woorea.openstack.keystone.model.Tenant)
		 */
		@Override
		public Tenant createUser(Tenant tenant) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see ch.niceneasy.openstack.web.account.test.ClientInterface#createUser(com.woorea.openstack.keystone.model.User)
		 */
		@Override
		public User createUser(User user) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		try {

			ClientRequest request = new ClientRequest(
					"http://ubuntu.ne.local:9080/account-management/rest/users");
			request.accept("application/json");

			User user = new User();
			user.setEmail("luca@niceneasy.ch");
			user.setUsername("lucaskylander");
			user.setPassword("Benzolieren1");
			user.setName("Luca Ulrich");
			user.setEnabled(Boolean.TRUE);

			ObjectMapper mapper = getContext(User.class);
			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, user);
			// String input = "{\"qty\":100,\"name\":\"iPad 4\"}";
			request.body("application/json", writer.toString());

			ClientResponse<String> response = request.put(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}
