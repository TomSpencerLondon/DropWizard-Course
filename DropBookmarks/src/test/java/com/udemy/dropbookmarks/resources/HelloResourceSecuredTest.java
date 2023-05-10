package com.udemy.dropbookmarks.resources;

import com.udemy.dropbookmarks.auth.HelloAuthenticator;
import com.udemy.dropbookmarks.core.User;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Base64;

@ExtendWith(DropwizardExtensionsSupport.class)
public class HelloResourceSecuredTest {
    public ResourceExtension resourceExtension = ResourceExtension
            .builder()
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .addProvider(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                    .setAuthenticator(new HelloAuthenticator("password"))
                    .buildAuthFilter()))
            .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
            .addResource(new HelloResource())
            .build();


    @Test
    public void testProtectedResource(){
        String credential = "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes());

        Response response = resourceExtension
                .target("/hello/secured")
                .request()
                .header(HttpHeaders.AUTHORIZATION, credential)
                .get();

        int status = response.getStatus();
        assertThat(status)
                .isEqualTo(200);
    }

}
