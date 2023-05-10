package com.udemy.dropbookmarks;

import com.udemy.dropbookmarks.auth.HelloAuthenticator;
import com.udemy.dropbookmarks.core.User;
import com.udemy.dropbookmarks.resources.HelloResource;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

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
        // TODO: application initialization
    }

    @Override
    public void run(final DropBookmarksConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application

        environment.jersey().register(new HelloResource());

        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(new HelloAuthenticator(configuration.getPassword()))
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()
        ));
    }

}
