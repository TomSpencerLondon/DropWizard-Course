package com.udemy.dropbookmarks.core;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.Objects;

public class UserTest extends EntityTest {

    @Test
    public void idIsNull() {
        User user = new User("Coda", "1");


        assertThatThrownBy(() -> user.setId(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void idIsOK() {
        User user = new User("Coda", "1");
        user.setId(1);
        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void usernameIsNull() {
        User user = new User(null, "1");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_NOT_NULL, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    @Test
    public void passwordIsNull() {
        User user = new User("Coda", null);
        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_NOT_NULL, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    @Test
    public void constructorOK() {
        User user = new User("Coda", "1");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void testSetUsernameIsNull() {
        User user = new User("Coda", "1");
        user.setUsername(null);

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_NOT_NULL, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    @Test
    public void testSetUsernameIsEmpty() {
        User user = new User("Coda", "1");
        user.setUsername("");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_LENGTH, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    @Test
    public void testSetUsernameIsOk() {
        User user = new User("Coda", "1");
        user.setUsername("Phil");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void testSetPasswordIsNull() {
        User user = new User("Coda", "1");
        user.setPassword(null);

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_NOT_NULL, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    @Test
    public void testSetPasswordIsEmpty() {
        User user = new User("Coda", "1");
        user.setPassword("");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_LENGTH, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    @Test
    public void testSetPasswordIsOk() {
        User user = new User("Coda", "1");
        user.setPassword("2");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void testAddBookmark() {
        User user = new User("Coda", "1");

        assertThatThrownBy(() -> user.addBookmark(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testAddBookmarkIsNull() {
        User user = new User("Coda", "1");
        user.addBookmark(new Bookmark());
        int expectedId = 1;
        user.setId(expectedId);

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertTrue(constraintViolations.isEmpty());

        Bookmark bookmark = user.getBookmarks().iterator().next();

        assertEquals(expectedId, bookmark.getUser().getId().intValue());
    }

    @Test
    public void testEqualsOtherIsNull() {
        User user = new User("Coda", "1");
        assertNotEquals(null, user);
        assertNotEquals(user.hashCode(), Objects.hashCode(null));
    }

    @Test
    public void testEqualsOtherIsSame() {
        User user = new User("Coda", "1");
        assertEquals(user, user);
    }

    @Test
    public void testEqualsOtherIsBookmark() {
        User user = new User("Coda", "1");
        assertNotEquals(user, new Bookmark());
    }

    @Test
    public void testEqualsAnotherUser() {
        User user = new User("Coda", "1");
        User other = new User();
        assertNotEquals(user, other);
        assertNotEquals(user.hashCode(), other.hashCode());
    }

    @Test
    public void testEqualsOk() {
        User user = new User("Coda", "1");
        int expectedId = 1;
        user.setId(expectedId);
        User other = new User("Coda", "1");
        other.setId(expectedId);
        assertEquals(user, other);
        assertEquals(user.hashCode(), other.hashCode());
    }

    @Test
    public void testEqualsIdIsNull() {
        User user = new User("Coda", "1");
        User other = new User("Coda", "1");
        assertEquals(user, other);
        assertEquals(user.hashCode(), other.hashCode());
    }

    @Test
    public void testEqualsOtherIdIsNull() {
        User user = new User("Coda", "1");
        int expectedId = 1;
        user.setId(expectedId);
        User other = new User("Coda", "1");
        assertNotEquals(user, other);
        assertNotEquals(user.hashCode(), other.hashCode());
    }
}

