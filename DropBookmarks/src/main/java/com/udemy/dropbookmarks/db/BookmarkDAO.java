package com.udemy.dropbookmarks.db;

import com.udemy.dropbookmarks.core.Bookmark;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class BookmarkDAO extends AbstractDAO<Bookmark> {

    public BookmarkDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Bookmark> findByUserId(int id) {
        return list(namedTypedQuery("Bookmark.findByUserId")
                .setParameter("id", id));
    }

    public Optional<Bookmark> findById(int id) {
        return Optional.ofNullable(get(id));
    }

    public Optional<Bookmark> findByIdAndUserId(int id, int userId) {
        return Optional.ofNullable(
                uniqueResult(
                        namedTypedQuery("Bookmark.findByIdAndUserId")
                                .setParameter("id", id)
                                .setParameter("userId", userId)
                )
        );
    }

    public Bookmark save(Bookmark bookmark) {
        return persist(bookmark);
    }

    public void delete(Integer id) {
        namedQuery("Bookmark.remove")
                .setParameter("id", id)
                .executeUpdate();
    }
}
