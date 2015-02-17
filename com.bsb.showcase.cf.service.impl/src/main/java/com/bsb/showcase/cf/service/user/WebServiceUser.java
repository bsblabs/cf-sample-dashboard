package com.bsb.showcase.cf.service.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.NaturalId;

/**
 * Technical user.
 *
 * @author Sebastien Gerard
 */
@Entity
@SuppressWarnings("serial")
public class WebServiceUser implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @NaturalId
    private String name;

    @Column(nullable = false)
    private String password;

    public WebServiceUser() {
    }

    /**
     * Returns the technical entity id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the unique user name.
     */
    public String getName() {
        return name;
    }

    /**
     * Specifies the unique user name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Specifies the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
