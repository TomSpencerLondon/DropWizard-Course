package com.udemy.dropbookmarks.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.udemy.dropbookmarks.core.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "bookmarks")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Bookmark.findAll",
                query = "SELECT b FROM Bookmark b"),
        @NamedQuery(name = "Bookmark.findById",
                query = "SELECT b FROM Bookmark b WHERE b.id = :id"),
        @NamedQuery(name = "Bookmark.findByUrl",
                query = "SELECT b FROM Bookmark b WHERE b.url = :url"),
        @NamedQuery(name = "Bookmark.findByDescription",
                query = "SELECT b FROM Bookmark b "
                        + "WHERE b.description = :description"),
        @NamedQuery(name = "Bookmark.findByUserId",
                query = "SELECT b FROM Bookmark b WHERE b.user.id = :id"),
        @NamedQuery(name = "Bookmark.remove", query = "DELETE FROM Bookmark b "
                + "where b.id = :id"),
        @NamedQuery(name = "Bookmark.findByIdAndUserId",
                query = "SELECT b FROM Bookmark b WHERE b.id = :id AND "
                        + "b.user.id = :userId")})
public class Bookmark implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "url")
    private String url;

    @Size(max = 2048)
    @Column(name = "description")
    private String description;

    @JsonIgnore
    @ManyToOne
    private User user;

    public Bookmark() {
    }

    public Bookmark(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id,
                this.url,
                this.description,
                this.user);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Bookmark other = (Bookmark) obj;
        return Objects.equals(this.user, other.user)
                && Objects.equals(this.url, other.url)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Bookmark{" + "id=" + id + ", url=" + url
                + ", description=" + description
                + ", user=" + Objects.toString(user) + '}';
    }

}

