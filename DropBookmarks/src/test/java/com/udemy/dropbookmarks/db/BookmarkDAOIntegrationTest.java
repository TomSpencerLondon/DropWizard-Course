package com.udemy.dropbookmarks.db;

import com.mysql.cj.conf.PropertyKey;
import com.udemy.dropbookmarks.core.Bookmark;
import com.udemy.dropbookmarks.core.User;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.MySQL57Dialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(DropwizardExtensionsSupport.class)
class BookmarkDAOIntegrationTest {
    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0-debian"));


    public DAOTestExtension daoTestRule = DAOTestExtension.newBuilder()
            .customizeConfiguration(c -> c.setProperty(AvailableSettings.DIALECT, MySQL57Dialect.class.getName()))
            .setDriver(mySQLContainer.getDriverClassName())
            .setUrl(mySQLContainer.getJdbcUrl())
            .setUsername(mySQLContainer.getUsername())
            .setPassword(mySQLContainer.getPassword())
            .addEntityClass(User.class)
            .addEntityClass(Bookmark.class)
            .build();

    private BookmarkDAO bookmarkDAO;

    @BeforeEach
    void setUp() {
        bookmarkDAO = new BookmarkDAO(daoTestRule.getSessionFactory());
    }

    @Test
    void createBookmark() {
        Bookmark bookMark = new Bookmark("udemy.com", "courses");
        Bookmark savedBookMark = daoTestRule.inTransaction(() -> bookmarkDAO.save(bookMark));
        assertThat(savedBookMark.getId()).isPositive();
        assertThat(savedBookMark.getUrl()).isEqualTo("udemy.com");
        assertThat(savedBookMark.getDescription()).isEqualTo("courses");
        assertThat(bookmarkDAO.findById(savedBookMark.getId())).isEqualTo(Optional.of(savedBookMark));
    }
//
//    @Test
//    void findAll() {
//        daoTestRule.inTransaction(() -> {
//            bookmarkDAO.create(new Person("Jeff", "The plumber", 1975));
//            bookmarkDAO.create(new Person("Jim", "The cook", 1985));
//            bookmarkDAO.create(new Person("Randy", "The watchman", 1995));
//        });
//
//        final List<Person> persons = bookmarkDAO.findAll();
//        assertThat(persons).extracting("fullName").containsOnly("Jeff", "Jim", "Randy");
//        assertThat(persons).extracting("jobTitle").containsOnly("The plumber", "The cook", "The watchman");
//        assertThat(persons).extracting("yearBorn").containsOnly(1975, 1985, 1995);
//    }
//
//    @Test
//    void handlesNullFullName() {
//        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
//                daoTestRule.inTransaction(() -> bookmarkDAO.create(new Person(null, "The null", 0))));
//    }
}
