package com.udemy.dropbookmarks.auth;

import com.udemy.dropbookmarks.core.User;
import com.udemy.dropbookmarks.db.UserDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;

import java.util.Optional;

public class DBAuthenticator implements Authenticator<BasicCredentials, User> {

    private final UserDAO userDAO;

    private final SessionFactory sessionFactory;

    private final PasswordEncryptor passwordEncryptor
            = new BasicPasswordEncryptor();

    public DBAuthenticator(final UserDAO userDAO,
                           final SessionFactory sessionFactory) {
        this.userDAO = userDAO;
        this.sessionFactory = sessionFactory;
    }

    @UnitOfWork
    @Override
    public final Optional<User> authenticate(BasicCredentials credentials)
            throws AuthenticationException {
        Session session = sessionFactory.openSession();
        Optional<User> result;
        try {
            ManagedSessionContext.bind(session);

            result = userDAO.findByUsername(credentials.getUsername());

            if (!result.isPresent()) {
                return result;
            } else               {
                if (passwordEncryptor.checkPassword(
                        credentials.getPassword(),
                        result.get().getPassword())) {
                    return result;
                } else {
                    return Optional.empty();
                }
            }

        } catch (Exception e) {
            throw new AuthenticationException(e);
        } finally {
            ManagedSessionContext.unbind(sessionFactory);
            session.close();
        }

    }

}

