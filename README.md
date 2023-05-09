### DropWizard

Dropwizard is a collection of Java frameworks combined for creating RESTful APIs and microservices.
It was created by Coda Hale. 

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

