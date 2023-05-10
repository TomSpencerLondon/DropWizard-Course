package com.udemy.dropbookmarks.db;

import com.udemy.dropbookmarks.core.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class UserDAO extends AbstractDAO<User> {

    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<User> findAll() {
        return list(namedTypedQuery("User.findAll"));
    }

    public Optional<User> findByUsernameAndPassword(
            String username,
            String password
    ) {
        return Optional.ofNullable(
                uniqueResult(
                        namedTypedQuery("User.findByUsernameAndPassword")
                                .setParameter("username", username)
                                .setParameter("password", password)
                ));
    }

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(get(id));
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(
                uniqueResult(
                        namedTypedQuery("User.findByUsername")
                                .setParameter("username", username)
                ));
    }
}
