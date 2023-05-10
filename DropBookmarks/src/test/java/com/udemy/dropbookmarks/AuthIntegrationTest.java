package com.udemy.dropbookmarks;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.SslConfigurator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.net.ssl.SSLContext;
import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
public class AuthIntegrationTest {
    private static DropwizardAppExtension<DropBookmarksConfiguration> EXT = new DropwizardAppExtension<>(
            DropBookmarksApplication.class,
            ResourceHelpers.resourceFilePath("test-config.yml")
    );

    private static final String TRUST_STORE_FILE_NAME = "dropbookmarks.keystore";
    private static final String TRUST_STORE_PASSWORD = "password";

    @Test
    void failsAuthenticationGivenNoAuthProvided() {
        Client client = EXT.client();

        Response response = client.target(
                        String.format("http://localhost:8080/hello/secured", EXT.getLocalPort()))
                .request().get();

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void successfulAuthenticationGivenAuthProvided() {
        String credential = "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes());
        Client client = EXT.client();

        Response response = client.target(
                        String.format("http://localhost:8080/hello/secured", EXT.getLocalPort()))
                .request()
                .header(HttpHeaders.AUTHORIZATION, credential)
                .get();

        String content = response.readEntity(String.class);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(content)
                .isEqualTo("Hello secured world");
    }

    @Test
    void successfulAuthenticationGivenAuthProvidedOnHttps() {
        SslConfigurator configurator = SslConfigurator.newInstance();
        configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
                .trustStorePassword(TRUST_STORE_PASSWORD);
        SSLContext context = configurator.createSSLContext();

        String credential = "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes());
        Client client = ClientBuilder.newBuilder()
                .sslContext(context)
                .build();

        Response response = client.target(
                        String.format("https://localhost:8443/hello/secured", EXT.getLocalPort()))
                .request()
                .header(HttpHeaders.AUTHORIZATION, credential)
                .get();

        String content = response.readEntity(String.class);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(content)
                .isEqualTo("Hello secured world");
    }

}
