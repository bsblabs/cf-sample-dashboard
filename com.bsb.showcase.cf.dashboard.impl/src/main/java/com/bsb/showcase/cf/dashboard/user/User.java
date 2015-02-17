package com.bsb.showcase.cf.dashboard.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.NaturalId;

/**
 * Dashboard human user.
 *
 * @author Sebastien Gerard
 */
@Entity
@SuppressWarnings("serial")
public class User  implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @NaturalId
    private String name;

    @Column(nullable = false)
    private String fullName;

    public User() {
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
