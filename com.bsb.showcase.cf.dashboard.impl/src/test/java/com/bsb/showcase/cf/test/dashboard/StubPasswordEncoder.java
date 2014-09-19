package com.bsb.showcase.cf.test.dashboard;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Sebastien Gerard
 */
public class StubPasswordEncoder implements PasswordEncoder {

    public static final String ENCODED_PWD = "1234";

    public static StubPasswordEncoder stubPasswordEncoder() {
        return new StubPasswordEncoder();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return ENCODED_PWD;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return true;
    }
}
