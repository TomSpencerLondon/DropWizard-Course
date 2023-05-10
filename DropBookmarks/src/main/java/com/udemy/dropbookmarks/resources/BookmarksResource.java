package com.udemy.dropbookmarks.resources;

import com.udemy.dropbookmarks.core.Bookmark;
import com.udemy.dropbookmarks.core.User;
import com.udemy.dropbookmarks.db.BookmarkDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;
import com.google.common.base.Strings;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/bookmarks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookmarksResource {

    public static final String WRONG_BODY_DATA_FORMAT
            = "Wrong body data format";

    private static final Logger LOGGER
            = LoggerFactory.getLogger(BookmarksResource.class);

    private final BookmarkDAO bookmarkDAO;

    public BookmarksResource(final BookmarkDAO bookmarkDAO) {
        this.bookmarkDAO = bookmarkDAO;
    }

    @GET
    @UnitOfWork
    public List<Bookmark> getBookmarks(@Auth User user) {
        return bookmarkDAO.findByUserId(user.getId());
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Optional<Bookmark> getBookmark(@PathParam("id") IntParam id,
                                          @Auth User user) {
        return bookmarkDAO.findByIdAndUserId(id.get(), user.getId());
    }

    @POST
    @UnitOfWork
    public Bookmark addBookmark(@Valid @NotNull Bookmark bookmark,
                                @Auth User user) {

        bookmark.setUser(user);
        return bookmarkDAO.save(bookmark);
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    public Bookmark modifyBookmark(@PathParam("id") Integer id,
                                   String jsonData,
                                   @Auth User user) {

        Bookmark bookmark = findBookmarkOrTrowException(id, user);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> changeMap = null;
        try {
            changeMap = objectMapper.readValue(jsonData, HashMap.class);
            purgeMap(changeMap);
            BeanUtils.populate(bookmark, changeMap);
            return bookmarkDAO.save(bookmark);
        } catch (IOException | IllegalAccessException | InvocationTargetException ex) {
            LOGGER.warn(WRONG_BODY_DATA_FORMAT, ex);
            throw new WebApplicationException(WRONG_BODY_DATA_FORMAT,
                    ex,
                    Response.Status.BAD_REQUEST);
        } finally {
            if (changeMap != null) {
                changeMap.clear();
            }
        }
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Bookmark deleteBookmark(@PathParam("id") Integer id,
                                   @Auth User user) {
        Bookmark bookmark
                = findBookmarkOrTrowException(id, user);
        bookmarkDAO.delete(id);
        return bookmark;
    }

    protected void purgeMap(final Map<String, String> changeMap) {
        changeMap.remove("id");
        changeMap.entrySet().removeIf(
                entry -> Strings.isNullOrEmpty(entry.getValue())
        );
    }

    private Bookmark findBookmarkOrTrowException(Integer id,
                                                 @Auth User user) {
        return bookmarkDAO.findByIdAndUserId(
                id, user.getId()
        ).orElseThrow(()
                -> new NotFoundException("Bookmark requested was not found."));
    }
}

