/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.web.account.model;

import java.io.Serializable;
import java.rmi.server.UID;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.SerializationUtils;

import com.woorea.openstack.keystone.model.User;

/**
 * The Class PendingRegistration.
 * 
 * @author Daniele
 */
@Entity
@NamedQuery(name = "findExpiredRegistrations", query = "delete from PendingRegistration p where p.creationTime < :exipiration")
public class PendingRegistration implements Serializable {
	/** Default value included to remove warning. Remove or modify at will. **/
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	private String id;

	/** The creation time. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime = new Date();

	/** The user object. */
	@Lob
	@Column(name = "members", length = Integer.MAX_VALUE - 1)
	private byte[] userObject;

	/**
	 * Instantiates a new pending registration.
	 */
	public PendingRegistration() {
	}

	/**
	 * Instantiates a new pending registration.
	 * 
	 * @param user
	 *            the user
	 */
	public PendingRegistration(User user) {
		super();
		setId(new UID().toString());
		setUser(user);
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the user object.
	 * 
	 * @return the user object
	 */
	public byte[] getUserObject() {
		return userObject;
	}

	/**
	 * Sets the user object.
	 * 
	 * @param userObject
	 *            the new user object
	 */
	public void setUserObject(byte[] userObject) {
		this.userObject = userObject;
	}

	/**
	 * Gets the creation time.
	 * 
	 * @return the creation time
	 */
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * Sets the creation time.
	 * 
	 * @param creationTime
	 *            the new creation time
	 */
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	@Transient
	public User getUser() {
		return (User) SerializationUtils.deserialize(getUserObject());
	}

	/**
	 * Sets the user.
	 * 
	 * @param user
	 *            the new user
	 */
	public void setUser(User user) {
		setUserObject(SerializationUtils.serialize(user));
	}

}