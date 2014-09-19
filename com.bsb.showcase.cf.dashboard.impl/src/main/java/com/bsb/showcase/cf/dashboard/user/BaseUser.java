package com.bsb.showcase.cf.dashboard.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.annotations.NaturalId;

/**
 * Base user implementation.
 *
 * @author Sebastien Gerard
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SuppressWarnings("serial")
public abstract class BaseUser implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @NaturalId
    private String name;

    protected BaseUser() {
    }

    /**
     * Returns the technical entity id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Specifies the technical entity id.
     */
    public void setId(Long id) {
        this.id = id;
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
}
