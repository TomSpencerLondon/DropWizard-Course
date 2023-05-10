### DropWizard

Dropwizard is a collection of Java frameworks combined for creating RESTful APIs and microservices.
It was created by Coda Hale. 
This course is quite good for dropwizard:
https://www.udemy.com/course/getting-started-with-dropwizard

### Overview
In this course we will look at:
- REST architecture
- Creating Dropwizard project using CLI, IDE
- "Hello World"
- Running the application
- Checking output using cURL, Postman
- Unit testing
- Basic authentication
- Configuration
- Integration testing
- Migrations
- Hibernate
- Resources

### Course expectations
- Build an example application to store bookmarks
- Be able to create simple microservices with DropWizard

### Introduction to REST
The Richardson Maturity Model defines 4 levels of API maturity:
- Level 0 - single URI and POST method
- Level 1 - resource identifiers
  - /users
  - /users/1
  - /users/Jessica
  - /users/1f30f437-0691-4394-83ed-9ea9747d8cf2
  - /users?page=10
  - /users/1/bookmarks
  - /users/1/bookmarks/3
  - /bookmarks
  - /bookmarks/3
- Level 2: Http verbs used: GET, POST, DELETE, etc.
  - GET /bookmarks
  - GET /bookmarks/3 -> 404 or 200 or ...
  - POST /bookmarks
  - DELETE /bookmarks/3
  - PUT /bookmarks/4

### REST
REST - Representational state transfer was introduced by Dr. Fielding in "Architectural Styles and the Design of Network-based Software Architectures". In REST we define resources and representations in JSON:
https://www.ics.uci.edu/~fielding/pubs/dissertation/top.htm

Uniform Resource Identifiers (URIs) are created according to the HTTP protocol. REST is a client server application style. The server side is stateless.
These links are quite useful for explaining REST:
https://en.wikipedia.org/wiki/HATEOAS
https://roy.gbiv.com/untangled/2008/rest-apis-must-be-hypertext-driven
https://en.wikipedia.org/wiki/Hypertext_Application_Language
https://jsonapi.org/
https://json-ld.org/
https://www.infoq.com/news/2014/05/uber-hypermedia/

### HATEOAS
HATEOAS stands for Hypermedia As The Engine Of An Application State. It partly means that we should introduce hyperlinks to resource representations.
This is an example bookmark:
```json
[
  {
    "id": 1,
    "name": "Dropwizard",
    "url": "https://dropwizard.github.io/dropwizard/getting-started.html",
    "description": "Dropwizard Getting Started"
  },
  {
    "id": 2,
    "name": "Session",
    "url": "https://docs.jboss.org/hibernate/orm/3.5/api/org/hibernate/context/ManagedSessionContext.html",
    "description": "Hibernate Docs"
  },
  {
    "id": 5,
    "name": "udemy",
    "url": "http://udemy.com",
    "description": "e-learning site"
  }
]
```
The benefits of adding hyperlinks is that we can inform the client about what operations can be performed with a particular
resource (list of bookmarks) and we can provide the client with the URI to perform an operation, so that if we decide to change
the URI on the server side, that will not break the client as it takes the URI from the representation. In addition, by adding new hyperlinks to the representations,
we inform client developers about new features about our API. One more operation which deserves its own hyperlink could be removing all bookmarks from the list.

Another example could include pagination. Our list may be very long, so that we may need to split the list into chunks. In each chunk we provide links 
to the previous and next pages, which helps to make our API stateless. Information about how to move to the next page is stored by the client and there is no necessity
to store the current page number on the server.

One more idea of adding hyperlinks to the representation includes the decision over how much information to include in the respresentation.
For example, instead of splitting out all information about a bookmark, one could produce only the name of the bookmark and a hyperlink to obtain full data.
This can save bandwidth and memory, particularly if we deal with objects that contain much more data than bookmarks.

### Example API design
https://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html_single/#example-mappings

We have the following tables. 
![EmployerEmployee](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/eec4139b-5e7d-47b3-bb68-9554d4686aab)

This might be the table design:
```sql
create table employers (
    id BIGINT not null, 
    name VARCHAR(255), 
    primary key (id)
)

create table employment_periods (
    id BIGINT not null,
    hourly_rate NUMERIC(12, 2),
    currency VARCHAR(12), 
    employee_id BIGINT not null, 
    employer_id BIGINT not null, 
    end_date TIMESTAMP, 
    start_date TIMESTAMP, 
    primary key (id)
)

create table employees (
    id BIGINT not null, 
    firstName VARCHAR(255), 
    initial CHAR(1), 
    lastName VARCHAR(255), 
    taxfileNumber VARCHAR(255), 
    primary key (id)
)

alter table employment_periods 
    add constraint employment_periodsFK0 foreign key (employer_id) references employers
alter table employment_periods 
    add constraint employment_periodsFK1 foreign key (employee_id) references employees
create sequence employee_id_seq
create sequence employment_id_seq
create sequence employer_id_seq
```

The possible API endpoints for the service might include:
- POST /v1/employees
- POST /batch
- GET /v1/employees
- GET /v1/employees/{id}
- PUT /v1/employees/{id}
- PATCH /v1/employees/{id}
- DELETE /v1/employees/{id}

### Hello worlds
We will now create an example dropwizard api using:
```bash
  mvn archetype:generate \
  -DgroupId=com.udemy \
  -DartifactId=DropBookmarks \
  -Dpackage=com.udemy.dropbookmarks \
  -Dname=DropBookmarks \
  -DarchetypeGroupId=io.dropwizard.archetypes \
  -DarchetypeArtifactId=java-simple \
  -DarchetypeVersion=4.0.0 \
  -DinteractiveMode=false
```

We can then run the application by adding:
```bash
server config.yml
```
to our run configuration in IntelliJ:
![image](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/76b6f867-40f4-4596-99dc-85552fec7cd9)

This runs the config.yml file:
```yaml
logging:
  level: INFO
  loggers:
    com.udemy: DEBUG
```
Alternatively we can package the application with:

```bash
mvn package
java -jar ./target/DropBookmarks-1.0-SNAPSHOT.jar server
```

The java version is set in the pom.xml:
```xml
    <profiles>
        <profile>
            <id>java11+</id>
            <activation>
                <jdk>[11,)</jdk>
            </activation>
            <properties>
                <!--
                Workaround for "javadoc: error - The code being documented uses modules but the packages
                defined in https://docs.oracle.com/javase/8/docs/api/ are in the unnamed module."
                -->
                <maven.javadoc.skip>true</maven.javadoc.skip>
            </properties>
        </profile>
    </profiles>
```

We have added a resource in /src/main/java/com/udemy/dropbookmarks/resources/HelloResource.java:
```java
@Path("/hello")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "Hello World";
    }
}
```
This uses the jakarta.ws.rs library for both @Path and @GET. We have set the expected output with @Produces(MediaType.TEXT_PLAIN).
We then refer to the resource in the DropBookmarks application:
```java
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
    }

}
```

Here we have used io.dropwizard.core.setup.Environment.jersey() to register our resource.
We can see the result in the browser or use curl to query the API:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course$ curl -w "\n" localhost:8080/hello
Hello World
```

### Add Test for HelloResource
We can add the following dependencies for testing our resource:
```xml

<dependencies>
    <dependency>
        <groupId>io.dropwizard</groupId>
        <artifactId>dropwizard-testing</artifactId>
        <version>${dropwizard.version}</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.glassfish.jersey.test-framework</groupId>
        <artifactId>jersey-test-framework-core</artifactId>
        <version>3.1.1</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.glassfish.jersey.test-framework.providers</groupId>
        <artifactId>jersey-test-framework-provider-grizzly2</artifactId>
        <version>3.1.1</version>
        <scope>test</scope>
    </dependency>

    <!-- https://mvnrepository.com/artifact/org.assertj/assertj-core -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.24.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

We can then write our first test on the HelloResource:

```java
@ExtendWith(DropwizardExtensionsSupport.class)
class HelloResourceTest {
  private static final ResourceExtension EXT = ResourceExtension.builder()
          .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
          .addResource(new HelloResource())
          .build();


  @Test
  void returnsMessage() {
    Response response = EXT.target("/hello").request().get();
    String content = response.readEntity(String.class);

    assertThat(content)
            .isEqualTo("Hello World");
  }
}
```
This link is quite useful for adding tests for Jersey:
https://www.baeldung.com/jersey-test


### Basic Authentication

We will now look at adding basic authentication to the project. First we need to add a class implementing the Authenticator interface.
We then register the Authenticator in the Appplication class. Finally we secure the method.

First we add the Authenticator class:
```java
public class HelloAuthenticator implements Authenticator<BasicCredentials, User> {
    @Override
    public Optional<User> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
        if ("password".equals(basicCredentials.getPassword())) {
            return Optional.of(new User());
        }
        
        return Optional.empty();
    }
}
```

We then add a new secured path to the HelloResource:
```java
@Path("/hello")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "Hello World";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/secured")
    public String getSecuredGreeting(@Auth User user) {
        return "Hello secured world";
    }
}
```
and also register the authentication to the DropBookmarksApplication:
```java
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
                        .setAuthenticator(new HelloAuthenticator())
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()
        ));
    }

}
```

We can create a base 64 version of our username and password:
https://www.base64encode.org/

![image](https://github.com/TomSpencerLondon/DropWizard-Course/assets/27693622/11893caf-6966-4eb0-839b-575df18f6d9e)
And then query the API with Basic Authorization:
![image](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/e9bf404f-0e56-4fac-9eb5-c5a3b342d17e)

We can also use Basic Auth and provide the username and password. Postman would then generate the encoded string for us.

We can also check the secured resource using curl:

```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course$ curl -w "\n" localhost:8080/hello/secured -i
HTTP/1.1 401 Unauthorized
Date: Wed, 10 May 2023 08:19:19 GMT
WWW-Authenticate: Basic realm="SUPER SECRET STUFF"
Content-Type: text/plain
Content-Length: 49

Credentials are required to access this resource.

```

We can add the required authorisation with the header option and the base 64 encoded string we gathered earlier:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course$ curl -w "\n" localhost:8080/hello/secured -i -H "Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ="
HTTP/1.1 200 OK
Date: Wed, 10 May 2023 08:21:07 GMT
Content-Type: text/plain
Vary: Accept-Encoding
Content-Length: 19

Hello secured world

```

We can also use the -u option:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course$ curl -w "\n" localhost:8080/hello/secured -i -u username:password
HTTP/1.1 200 OK
Date: Wed, 10 May 2023 08:22:19 GMT
Content-Type: text/plain
Vary: Accept-Encoding
Content-Length: 19

Hello secured world

```

We can then add a test for our authentication:
```java
@ExtendWith(DropwizardExtensionsSupport.class)
public class HelloResourceSecuredTest {
    public ResourceExtension resourceExtension = ResourceExtension
            .builder()
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .addProvider(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                    .setAuthenticator(new HelloAuthenticator())
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
```

### Configuration and HTTPS
First we can experiment with the port on which we will expose our service:

```yaml
logging:
  level: INFO
  loggers:
    com.udemy: DEBUG
password: password
server:
  applicationConnectors:
    - type: http
      port: 8085
```
We can now start our application and see the running app on 8085:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course$ curl -w "\n" localhost:8085/hello
Hello World
```

We can now add https. First we will add a keystore for the encrypted connection:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course$ keytool -genkeypair \
-keyalg RSA \
-dname "CN=localhost" \
-keystore dropbookmarks.keystore \
-keypass password \
-storepass password

```
This creates the keystore:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ ls
config.yml  dependency-reduced-pom.xml  dropbookmarks.keystore  pom.xml  README.md  src  target
```

We will now add https support to our application:
```bash
logging:
  level: INFO
  loggers:
    com.udemy: DEBUG
password: password
server:
  applicationConnectors:
    - type: http
      port: 8080
    - type: https
      port: 8443
      keyStorePath: dropbookmarks.keystore
      keyStorePassword: password
      validateCerts: false
```
For production, we will need to produce a certificate which the browser trusts.

```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ curl -w "\n" https://localhost:8443/hello
curl: (60) SSL certificate problem: self-signed certificate
More details here: https://curl.se/docs/sslcerts.html

curl failed to verify the legitimacy of the server and therefore could not
establish a secure connection to it. To learn more about this situation and
how to fix it, please visit the web page mentioned above.
```
We can use -k to ignore the warning:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ curl -w "\n" https://localhost:8443/hello -k
Hello World
```

We can now add a test for the http request expecting a successful authentication when auth is provided:
```java
@ExtendWith(DropwizardExtensionsSupport.class)
public class AuthIntegrationTest {
  private static DropwizardAppExtension<DropBookmarksConfiguration> EXT = new DropwizardAppExtension<>(
          DropBookmarksApplication.class,
          ResourceHelpers.resourceFilePath("test-config.yml")
  );



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

}

```

We will now add a test for https:
```java

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
```

This link was quite useful for https:
https://gist.github.com/javaeeeee/8fea2304b481fbcc4f5b

### Database connection
We can now configure the database connection. First we add the dependencies:
```xml
<dependencies>
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
  </dependency>
  <dependency>
    <groupId>io.dropwizard</groupId>
    <artifactId>dropwizard-hibernate</artifactId>
    <version>${dropwizard.version}</version>
  </dependency>
</dependencies>
```

Next we add the connection configuration to config.yml:
```yaml
logging:
  level: INFO
  loggers:
    com.udemy: DEBUG
password: password
server:
  applicationConnectors:
    - type: http
      port: 8080
    - type: https
      port: 8443
      keyStorePath: dropbookmarks.keystore
      keyStorePassword: password
      validateCerts: false
database:
  driverClass: com.mysql.jdbc.Driver
  user: root
  password: password
  url: jdbc:mysql://localhost:3306/DropBookmarks
```

To reset the password on ubuntu we can run:
```bash
sudo service mysql start
sudo mysql

mysql> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
tom@tom-ubuntu:~$ sudo mysql -u root -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 13
Server version: 8.0.33-0ubuntu0.22.10.1 (Ubuntu)

Copyright (c) 2000, 2023, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> 
```

We then add the DataSourceFactory to our configuration file:
```java
public class DropBookmarksConfiguration extends Configuration {
    @NotEmpty
    private String password;
    
    @NotNull
    @Valid
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }
}
```

### Liquibase Database Migrations
This is the database set up for our users and bookmarks tables which we will use in our application:
![bookmarksdb](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/34e1a326-4017-4c69-8971-e2b25343a384)

We could create our tables using SQL:
```sql
create table users(
    id bigint primary key,
    username varchar(255),
    password varchar(255)
)

create table bookmarks(
    id bigint primary key,
    name varchar(255),
    url varchar(1024),
    description varchar(2048),
    user_id bigint,
    foreign key (id) references users(id)
)
```
But we should bear in mind that on a project several developers work together and each environment needs databases in sync.
To avoid a mess we should use database migrations. This creates a separate table so that we can confirm the migrations that have
been applied. We will use Liquibase as our migration tool. This link is quite useful for Liquibase:
https://docs.liquibase.com/start/get-started/liquibase-sql.html

First we add the Datasource to our DropBookmarksConfguration file:
```java
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
```

Then we add the migrations.xml file for our migrations:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
    <changeSet id="1" author="tom" dbms="mysql">
        <createTable tableName="users">
            <column name="id" type="bigint" autoIncrement="true">
                <constraints primaryKey="true" nullable="false" />
            </column>
            <column name="username" type="varchar(255)">
                <constraints nullable="false" />
            </column>
            <column name="password" type="varchar(255)">
                <constraints nullable="false" />
            </column>
        </createTable>
        <comment>Script to create user table</comment>
    </changeSet>

</databaseChangeLog>

```
Next we add the Liquibase plugin to our pom.xml:
```xml

<plugin>
  <groupId>org.liquibase</groupId>
  <artifactId>liquibase-maven-plugin</artifactId>
  <version>4.21.1</version>
  <configuration>
    <changeLogFile>migrations.xml</changeLogFile>
    <driver>com.mysql.jdbc.Driver</driver>
    <url>jdbc:mysql://localhost:3306/DropBookmarks</url>
    <username>root</username>
    <password>root</password>
  </configuration>
</plugin>
```
Here we are using a local database. Next we add the DropBookmarks database in mysql:

```bash
tom@tom-ubuntu:~$ sudo mysql -u root -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 22
Server version: 8.0.33-0ubuntu0.22.10.1 (Ubuntu)

Copyright (c) 2000, 2023, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> create database DropBookmarks;
Query OK, 1 row affected (0.02 sec)
```
We run the migrations:
```bash
mvn package
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ mvn liquibase:update
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------------------< com.udemy:DropBookmarks >-----------------------
[INFO] Building DropBookmarks 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[WARNING] The artifact mysql:mysql-connector-java:jar:8.0.33 has been relocated to com.mysql:mysql-connector-j:jar:8.0.33
[INFO] 
[INFO] --- liquibase-maven-plugin:4.21.1:update (default-cli) @ DropBookmarks ---
[INFO] ------------------------------------------------------------------------
[INFO] ####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ## 
##  Get documentation at docs.liquibase.com       ##
##  Get certified courses at learn.liquibase.com  ## 
##  Free schema change activity reports at        ##
##      https://hub.liquibase.com                 ##
##                                                ##
####################################################
Starting Liquibase at 13:43:28 (version 4.19.0 #6648 built at 2023-01-17 15:02+0000)
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
[INFO] Executing on Database: jdbc:mysql://localhost:3306/DropBookmarks
[WARNING] Unable to register command 'dbUrlConnectionCommandStep' argument 'defaultSchemaName': Duplicate argument 'defaultSchemaName' found for command 'dbUrlConnectionCommandStep'
[INFO] Cannot load service: liquibase.command.CommandStep: Provider liquibase.command.core.DbUrlConnectionCommandStep could not be instantiated
[INFO] Successfully acquired change log lock
[INFO] Creating database history table with name: DATABASECHANGELOG
[INFO] Reading from DATABASECHANGELOG
[INFO] Using deploymentId: 3722610641
[INFO] Reading from DATABASECHANGELOG
Running Changeset: migrations.xml::1::tom
[INFO] Table users created
[INFO] ChangeSet migrations.xml::1::tom ran successfully in 50ms
[INFO] UPDATE SUMMARY
[INFO] Run:                          1
[INFO] Previously run:               0
[INFO] Filtered out:                 0
[INFO] -------------------------------
[INFO] Total change sets:            1


UPDATE SUMMARY
Run:                          1
Previously run:               0
Filtered out:                 0
-------------------------------
Total change sets:            1

[INFO] Update summary generated
[INFO] Update command completed successfully.
Liquibase: Update has been successful.
[INFO] Successfully released change log lock
[INFO] Successfully released change log lock
[INFO] Command execution complete
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.815 s
[INFO] Finished at: 2023-05-10T13:43:30+01:00
[INFO] ------------------------------------------------------------------------

```
We can now check our database to ensure that the migration has run successfully:

```bash
mysql> use DropBookmarks;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+-------------------------+
| Tables_in_DropBookmarks |
+-------------------------+
| DATABASECHANGELOG       |
| DATABASECHANGELOGLOCK   |
| users                   |
+-------------------------+
3 rows in set (0.00 sec)

mysql> select * from users;
Empty set (0.00 sec)

mysql> describe users;
+----------+--------------+------+-----+---------+----------------+
| Field    | Type         | Null | Key | Default | Extra          |
+----------+--------------+------+-----+---------+----------------+
| id       | bigint       | NO   | PRI | NULL    | auto_increment |
| username | varchar(255) | NO   |     | NULL    |                |
| password | varchar(255) | NO   |     | NULL    |                |
+----------+--------------+------+-----+---------+----------------+
3 rows in set (0.01 sec)
```

If we want to drop the tables we can use:
```bash
mvn liquibase:dropAll
```
We can also view the change log in the DATABASECHANGELOG table:
![image](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/1721198e-47a3-4211-ad6b-73c630d38a08)

We would then add a change set if we wanted to add a column to the database.

We can now add the bookmarks table and add data to the user table:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
    <changeSet id="1" author="tom" dbms="mysql">
        <createTable tableName="users">
            <column name="id" type="bigint" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="username" type="varchar(255)">
                <constraints nullable="false"/>
            </column>
            <column name="password" type="varchar(255)">
                <constraints nullable="false"/>
            </column>
        </createTable>
        <comment>Script to create user table</comment>
    </changeSet>

    <changeSet id="2" author="tom">
        <createTable tableName="bookmarks">
            <column name="id" type="bigint" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="name" type="varchar(255)">
                <constraints nullable="false"/>
            </column>
            <column name="url" type="varchar(1024)">
                <constraints nullable="false"/>
            </column>
            <column name="description" type="varchar(2048)"/>
            <column name="user_id" type="bigint">
                <constraints nullable="false"
                             foreignKeyName="fk_users_id"
                             referencedTableName="users"
                             referencedColumnNames="id"
                />
            </column>
        </createTable>
    </changeSet>

    <changeSet id="3" author="tom" context="DEV">
        <insert tableName="users">
            <column name="id" value="1" />
            <column name="username" value="udemy" />
            <column name="password" value="password" />
        </insert>
        <rollback>
            <delete tableName="users">
                <where>
                    id=1
                </where>
            </delete>
        </rollback>
    </changeSet>
</databaseChangeLog>
```

We can now delete the migrations:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ mvn liquibase:dropAll
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------------------< com.udemy:DropBookmarks >-----------------------
[INFO] Building DropBookmarks 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[WARNING] The artifact mysql:mysql-connector-java:jar:8.0.33 has been relocated to com.mysql:mysql-connector-j:jar:8.0.33
[INFO] 
[INFO] --- liquibase-maven-plugin:4.21.1:dropAll (default-cli) @ DropBookmarks ---
[INFO] ------------------------------------------------------------------------
[INFO] ####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ## 
##  Get documentation at docs.liquibase.com       ##
##  Get certified courses at learn.liquibase.com  ## 
##  Free schema change activity reports at        ##
##      https://hub.liquibase.com                 ##
##                                                ##
####################################################
Starting Liquibase at 14:11:54 (version 4.19.0 #6648 built at 2023-01-17 15:02+0000)
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
[INFO] Executing on Database: jdbc:mysql://localhost:3306/DropBookmarks
[WARNING] Unable to register command 'dbUrlConnectionCommandStep' argument 'defaultSchemaName': Duplicate argument 'defaultSchemaName' found for command 'dbUrlConnectionCommandStep'
[INFO] Cannot load service: liquibase.command.CommandStep: Provider liquibase.command.core.DbUrlConnectionCommandStep could not be instantiated
[INFO] Successfully acquired change log lock
[INFO] Dropping Database Objects in schema: DropBookmarks.DropBookmarks
[INFO] Successfully deleted all supported object types in schema DropBookmarks.DropBookmarks.
[INFO] Successfully released change log lock
All objects dropped from root@localhost@jdbc:mysql://localhost:3306/DropBookmarks
[INFO] Command execution complete
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  4.005 s
[INFO] Finished at: 2023-05-10T14:11:56+01:00
[INFO] ------------------------------------------------------------------------

```

Now we use the DEV context to run our migration:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ mvn liquibase:updateSQL \
> -Dliquibase.contexts=DEV
```
Each time we should remember that we need to run mvn package to ensure that the new migration is added to
/target/classes/migrations.xml.
We can then see the updated tables:
```bash
mysql> show tables;
+-------------------------+
| Tables_in_DropBookmarks |
+-------------------------+
| DATABASECHANGELOG       |
| DATABASECHANGELOGLOCK   |
| bookmarks               |
| users                   |
+-------------------------+
4 rows in set (0.01 sec)

mysql> select * from users;
+----+----------+----------+
| id | username | password |
+----+----------+----------+
|  1 | udemy    | password |
+----+----------+----------+
1 row in set (0.00 sec)
```

#### Migration

We can see the changesets with:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ mvn liquibase:updateSQL -Dliquibase.contexts=DEV
```
This produces a migrate.sql in /target/liquibase:
```sql

--  Create Database Lock Table
CREATE TABLE DATABASECHANGELOGLOCK (ID INT NOT NULL, `LOCKED` BIT(1) NOT NULL, LOCKGRANTED datetime NULL, LOCKEDBY VARCHAR(255) NULL, CONSTRAINT PK_DATABASECHANGELOGLOCK PRIMARY KEY (ID));

--  Initialize Database Lock Table
DELETE FROM DATABASECHANGELOGLOCK;

INSERT INTO DATABASECHANGELOGLOCK (ID, `LOCKED`) VALUES (1, 0);

--  Lock Database
UPDATE DATABASECHANGELOGLOCK SET `LOCKED` = 1, LOCKEDBY = 'tom-ubuntu (192.168.49.1)', LOCKGRANTED = NOW() WHERE ID = 1 AND `LOCKED` = 0;

--  Create Database Change Log Table
CREATE TABLE DATABASECHANGELOG (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED datetime NOT NULL, ORDEREXECUTED INT NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35) NULL, `DESCRIPTION` VARCHAR(255) NULL, COMMENTS VARCHAR(255) NULL, TAG VARCHAR(255) NULL, LIQUIBASE VARCHAR(20) NULL, CONTEXTS VARCHAR(255) NULL, LABELS VARCHAR(255) NULL, DEPLOYMENT_ID VARCHAR(10) NULL);

--  *********************************************************************
--  Update Database Script
--  *********************************************************************
--  Change Log: migrations.xml
--  Ran at: 10/05/2023, 15:04
--  Against: root@localhost@jdbc:mysql://localhost:3306/DropBookmarks
--  Liquibase version: 4.19.0
--  *********************************************************************

--  Changeset migrations.xml::1::tom
--  Script to create user table
CREATE TABLE users (id BIGINT AUTO_INCREMENT NOT NULL, username VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, CONSTRAINT PK_USERS PRIMARY KEY (id));

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, `DESCRIPTION`, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('1', 'tom', 'migrations.xml', NOW(), 1, '8:89916a7999d9f39d1ffbdc75fe24582a', 'createTable tableName=users', 'Script to create user table', 'EXECUTED', NULL, NULL, '4.19.0', '3727488397');

--  Changeset migrations.xml::2::tom
--  Script to create bookmark table
CREATE TABLE bookmarks (id BIGINT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, url VARCHAR(1024) NOT NULL, `description` VARCHAR(2048) NULL, user_id BIGINT NOT NULL, CONSTRAINT PK_BOOKMARKS PRIMARY KEY (id), CONSTRAINT fk_users_id FOREIGN KEY (user_id) REFERENCES users(id));

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, `DESCRIPTION`, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('2', 'tom', 'migrations.xml', NOW(), 2, '8:ee00e8370f09936706b139b925b3c37d', 'createTable tableName=bookmarks', 'Script to create bookmark table', 'EXECUTED', NULL, NULL, '4.19.0', '3727488397');

--  Changeset migrations.xml::3::tom
INSERT INTO users (id, username, password) VALUES ('1', 'udemy', 'password');

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, `DESCRIPTION`, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('3', 'tom', 'migrations.xml', NOW(), 3, '8:1ea312ee6827e396c6db2f904186aae8', 'insert tableName=users', '', 'EXECUTED', 'DEV', NULL, '4.19.0', '3727488397');

--  Release Database Lock
UPDATE DATABASECHANGELOGLOCK SET `LOCKED` = 0, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;

```
this shows the lock table to avoid updates from other machines. The DatabaseChangeLog table is created and then the users and bookmarks tables are created and data is inserted into the users table.

#### Additional liquibase commands
We can run rollbacks with Liquibase:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ mvn liquibase:rollbackSQL \
> -Dliquibase.rollbackCount=1
```
We can also use date and tag for our rollbacks. Our rollback SQL shows the following:
```bash
--  Lock Database
UPDATE DATABASECHANGELOGLOCK SET `LOCKED` = 1, LOCKEDBY = 'tom-ubuntu (192.168.49.1)', LOCKGRANTED = NOW() WHERE ID = 1 AND `LOCKED` = 0;

--  *********************************************************************
--  Rollback 1 Change(s) Script
--  *********************************************************************
--  Change Log: migrations.xml
--  Ran at: 10/05/2023, 15:13
--  Against: root@localhost@jdbc:mysql://localhost:3306/DropBookmarks
--  Liquibase version: 4.19.0
--  *********************************************************************

--  Rolling Back ChangeSet: migrations.xml::3::tom
DELETE FROM users WHERE id=1;

DELETE FROM DATABASECHANGELOG WHERE ID = '3' AND AUTHOR = 'tom' AND FILENAME = 'migrations.xml';

--  Release Database Lock
UPDATE DATABASECHANGELOGLOCK SET `LOCKED` = 0, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;
```
The data about our user has been removed from the table.
We can view our change logs with:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ mvn liquibase:generateChangeLog -Dliquibase.outputChangeLogFile=changelog.xml
```
This produces a changelog.xml file in /target:

```xml
<?xml version="1.1" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog" xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext" xmlns:pro="http://www.liquibase.org/xml/ns/pro" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd http://www.liquibase.org/xml/ns/pro http://www.liquibase.org/xml/ns/pro/liquibase-pro-latest.xsd http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">
    <changeSet author="tom (generated)" id="1683728354402-1">
        <createTable tableName="bookmarks">
            <column autoIncrement="true" name="id" type="BIGINT">
                <constraints nullable="false" primaryKey="true"/>
            </column>
            <column name="name" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="url" type="VARCHAR(1024)">
                <constraints nullable="false"/>
            </column>
            <column name="description" type="VARCHAR(2048)"/>
            <column name="user_id" type="BIGINT">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>
    <changeSet author="tom (generated)" id="1683728354402-2">
        <createTable tableName="users">
            <column autoIncrement="true" name="id" type="BIGINT">
                <constraints nullable="false" primaryKey="true"/>
            </column>
            <column name="username" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="password" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>
    <changeSet author="tom (generated)" id="1683728354402-3">
        <createIndex indexName="fk_users_id" tableName="bookmarks">
            <column name="user_id"/>
        </createIndex>
    </changeSet>
    <changeSet author="tom (generated)" id="1683728354402-4">
        <addForeignKeyConstraint baseColumnNames="user_id" baseTableName="bookmarks" constraintName="fk_users_id" deferrable="false" initiallyDeferred="false" onDelete="RESTRICT" onUpdate="RESTRICT" referencedColumnNames="id" referencedTableName="users" validate="true"/>
    </changeSet>
</databaseChangeLog>
```

We can also check the changesets that have not been applied with:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ java -jar target/DropBookmarks-1.0-SNAPSHOT.jar db status config.yml
WARN  [2023-05-10 15:27:19,896] org.hibernate.validator.internal.properties.javabean.JavaBeanExecutable: HV000254: Missing parameter metadata for ResponseMeteredLevel(String, int), which declares implicit or synthetic parameters. Automatic resolution of generic type information for method parameters may yield incorrect results if multiple parameters have the same erasure. To solve this, compile your code with the '-parameters' flag.
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
INFO  [2023-05-10 14:27:21,015] liquibase.changelog.StandardChangeLogHistoryService: Reading from DropBookmarks.DATABASECHANGELOG
1 changesets have not been applied to root@localhost@jdbc:mysql://localhost:3306/DropBookmarks
```

We can apply the changeset with:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ java -jar target/DropBookmarks-1.0-SNAPSHOT.jar db migrate -i DEV config.yml
WARN  [2023-05-10 15:28:43,038] org.hibernate.validator.internal.properties.javabean.JavaBeanExecutable: HV000254: Missing parameter metadata for ResponseMeteredLevel(String, int), which declares implicit or synthetic parameters. Automatic resolution of generic type information for method parameters may yield incorrect results if multiple parameters have the same erasure. To solve this, compile your code with the '-parameters' flag.
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
INFO  [2023-05-10 14:28:43,959] liquibase.lockservice.StandardLockService: Successfully acquired change log lock
INFO  [2023-05-10 14:28:44,492] liquibase.changelog.StandardChangeLogHistoryService: Reading from DropBookmarks.DATABASECHANGELOG
Running Changeset: migrations.xml::3::tom
INFO  [2023-05-10 14:28:44,597] liquibase.changelog.ChangeSet: New row inserted into users
INFO  [2023-05-10 14:28:44,612] liquibase.changelog.ChangeSet: ChangeSet migrations.xml::3::tom ran successfully in 22ms
INFO  [2023-05-10 14:28:44,635] liquibase.lockservice.StandardLockService: Successfully released change log lock
```

We can then verify the changeset has been applied:
```bash
tom@tom-ubuntu:~/Projects/Dropwizard-Course/DropBookmarks$ java -jar target/DropBookmarks-1.0-SNAPSHOT.jar db status config.yml
WARN  [2023-05-10 15:29:19,667] org.hibernate.validator.internal.properties.javabean.JavaBeanExecutable: HV000254: Missing parameter metadata for ResponseMeteredLevel(String, int), which declares implicit or synthetic parameters. Automatic resolution of generic type information for method parameters may yield incorrect results if multiple parameters have the same erasure. To solve this, compile your code with the '-parameters' flag.
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
INFO  [2023-05-10 14:29:21,058] liquibase.changelog.StandardChangeLogHistoryService: Reading from DropBookmarks.DATABASECHANGELOG
root@localhost@jdbc:mysql://localhost:3306/DropBookmarks is up to date

```


### Connecting to a Relational Database


