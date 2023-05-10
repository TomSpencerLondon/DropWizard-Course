package com.udemy.dropbookmarks.core;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BookmarkTest extends EntityTest {

    @Test
    void testSetUrlIsNull() {
        Bookmark bookmark = new Bookmark();
        bookmark.setUrl(null);

        Set<ConstraintViolation<Bookmark>> constraintViolations
                = validator.validate(bookmark);

        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_NOT_NULL, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    @Test
    public void testSetUrlIsEmpty() {
        Bookmark bookmark = new Bookmark();
        bookmark.setUrl("");

        Set<ConstraintViolation<Bookmark>> constraintViolations
                = validator.validate(bookmark);

        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_LENGTH, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    /**
     * Test of equals method, of class Bookmark.
     */
    @Test
    public void testEqualsNull() {
        String expectedURL = "https://github.com/javaeeeee/DropBookmarks";
        Bookmark bookmark = new Bookmark(
                expectedURL,
                "Project Repository URL");
        Bookmark other = null;

        assertFalse(bookmark.equals(other));
    }

    @Test
    public void testEqualsSame() {
        String expectedURL = "https://github.com/javaeeeee/DropBookmarks";
        Bookmark bookmark = new Bookmark(
                expectedURL,
                "Project Repository URL");
        Bookmark other = bookmark;

        assertTrue(bookmark.equals(other));
        assertEquals(bookmark.hashCode(), other.hashCode());
    }


    @Test
    public void testEqualsUser() {
        String expectedURL = "https://github.com/javaeeeee/DropBookmarks";
        Bookmark bookmark = new Bookmark(
                expectedURL,
                "Project Repository URL");

        assertFalse(bookmark.equals(new User()));
    }

    @Test
    public void testEqualsOk() {
        String expectedURL = "https://github.com/javaeeeee/DropBookmarks";
        Bookmark bookmark = new Bookmark(
                expectedURL,
                "Project Repository URL");
        Bookmark other = new Bookmark(
                expectedURL,
                "Project Repository URL");

        assertTrue(bookmark.equals(other));
        assertEquals(bookmark.hashCode(), other.hashCode());
    }

    @Test
    public void testEqualsUsersNotEqual() {
        String expectedURL = "https://github.com/javaeeeee/DropBookmarks";
        Bookmark bookmark = new Bookmark(
                expectedURL,
                "Project Repository URL");
        User u1 = new User();
        u1.setId(1);
        bookmark.setUser(u1);
        Bookmark other = new Bookmark(
                expectedURL,
                "Project Repository URL");
        User u2 = new User();
        u2.setId(2);
        other.setUser(u2);

        assertFalse(bookmark.equals(other));
        assertNotEquals(bookmark.hashCode(), other.hashCode());
    }
}
