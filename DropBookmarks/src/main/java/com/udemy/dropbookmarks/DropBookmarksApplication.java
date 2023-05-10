package com.udemy.dropbookmarks;

import com.udemy.dropbookmarks.core.Bookmark;
import com.udemy.dropbookmarks.core.User;
import com.udemy.dropbookmarks.db.BookmarkDAO;
import com.udemy.dropbookmarks.db.UserDAO;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.AuthorizationContext;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import com.udemy.dropbookmarks.auth.DBAuthenticator;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.hibernate.SessionFactory;
import com.udemy.dropbookmarks.resources.BookmarksResource;

public class DropBookmarksApplication extends Application<DropBookmarksConfiguration> {

    private final HibernateBundle<DropBookmarksConfiguration> hibernateBundle
            = new HibernateBundle<DropBookmarksConfiguration>(
            User.class,
            Bookmark.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(
                DropBookmarksConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        new DropBookmarksApplication().run(args);
    }

    @Override
    public String getName() {
        return "DropBookmarks";
    }

    @Override
    public void initialize(
            final Bootstrap<DropBookmarksConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(
                new MigrationsBundle<DropBookmarksConfiguration>() {
                    @Override
                    public DataSourceFactory getDataSourceFactory(
                            DropBookmarksConfiguration configuration) {
                        return configuration.getDataSourceFactory();
                    }
                });
    }

    @Override
    public void run(final DropBookmarksConfiguration configuration,
                    final Environment environment) {
        final UserDAO userDAO
                = new UserDAO(hibernateBundle.getSessionFactory());
        final BookmarkDAO bookmarkDAO
                = new BookmarkDAO(hibernateBundle.getSessionFactory());

        final DBAuthenticator authenticator
                = new UnitOfWorkAwareProxyFactory(hibernateBundle)
                .create(DBAuthenticator.class,
                        new Class<?>[]{UserDAO.class, SessionFactory.class},
                        new Object[]{userDAO,
                                hibernateBundle.getSessionFactory()});

        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(authenticator)
                        .setAuthorizer(new Authorizer<User>() {
                            @Override
                            public boolean authorize(User user, String s, @Nullable ContainerRequestContext containerRequestContext) {
                                return true;
                            }
                        })
                        .setRealm("SECURITY REALM")
                        .buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(
                new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(new BookmarksResource(bookmarkDAO));
    }

}
