package com.udemy.dropbookmarks;

import com.udemy.dropbookmarks.auth.HelloAuthenticator;
import com.udemy.dropbookmarks.core.User;
import com.udemy.dropbookmarks.resources.HelloResource;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;

public class DropBookmarksApplication extends Application<DropBookmarksConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DropBookmarksApplication().run(args);
    }

    @Override
    public String getName() {
        return "DropBookmarks";
    }

    @Override
    public void initialize(final Bootstrap<DropBookmarksConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<DropBookmarksConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(DropBookmarksConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(final DropBookmarksConfiguration configuration,
                    final Environment environment) {

        environment.jersey().register(new HelloResource());

        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(new HelloAuthenticator(configuration.getPassword()))
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()
        ));
    }

}
