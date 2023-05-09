package com.udemy.dropbookmarks.resources;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class HelloResourceTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new ResourceConfig(HelloResource.class);
    }

    @Test
    void works() {
        Response response = target("/hello").request().get();
        String content = response.readEntity(String.class);

        assertThat(content)
                .isEqualTo("Hello World");
    }
}