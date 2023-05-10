package com.udemy.dropbookmarks.auth;

import com.udemy.dropbookmarks.core.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

public class HelloAuthenticator implements Authenticator<BasicCredentials, User> {

    private final String password;

    public HelloAuthenticator(String password) {
        this.password = password;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
        if (password.equals(basicCredentials.getPassword())) {
            return Optional.of(new User());
        }

        return Optional.empty();
    }
}
