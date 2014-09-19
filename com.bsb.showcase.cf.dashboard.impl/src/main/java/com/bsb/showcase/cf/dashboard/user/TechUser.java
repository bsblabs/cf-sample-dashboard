package com.bsb.showcase.cf.dashboard.user;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Technical user.
 *
 * @author Sebastien Gerard
 */
@Entity
@SuppressWarnings("serial")
public class TechUser extends BaseUser {

    @Column(nullable = false)
    private String password;

    public TechUser() {
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
