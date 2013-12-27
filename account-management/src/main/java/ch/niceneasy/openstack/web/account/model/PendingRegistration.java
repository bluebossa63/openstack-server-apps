package ch.niceneasy.openstack.web.account.model;

import java.io.Serializable;
import java.rmi.server.UID;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.SerializationUtils;

import com.woorea.openstack.keystone.model.User;

@Entity
@NamedQuery(name="findExpiredRegistrations",query="delete from PendingRegistration p where p.creationTime < :exipiration")
public class PendingRegistration implements Serializable {
	/** Default value included to remove warning. Remove or modify at will. **/
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime = new Date();

	@Lob
	@Column(name = "members", length = Integer.MAX_VALUE - 1)
	private byte[] userObject;

	public PendingRegistration() {
	}

	public PendingRegistration(User user) {
		super();
		setId(new UID().toString());
		setUser(user);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getUserObject() {
		return userObject;
	}

	public void setUserObject(byte[] userObject) {
		this.userObject = userObject;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	@Transient
	public User getUser() {
		return (User) SerializationUtils.deserialize(getUserObject());
	}

	public void setUser(User user) {
		setUserObject(SerializationUtils.serialize((Serializable) user));
	}	
	
}