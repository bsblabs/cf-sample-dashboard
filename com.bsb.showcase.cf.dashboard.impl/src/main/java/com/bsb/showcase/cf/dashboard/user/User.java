package com.bsb.showcase.cf.dashboard.user;

import javax.persistence.Entity;

/**
 * Dashboard human user.
 *
 * @author Sebastien Gerard
 */
@Entity
@SuppressWarnings("serial")
public class User extends BaseUser {

    private String fullName;

    public User() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
