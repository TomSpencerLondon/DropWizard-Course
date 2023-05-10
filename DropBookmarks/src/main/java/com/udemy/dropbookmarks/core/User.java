package com.udemy.dropbookmarks.core;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.security.Principal;
import java.util.*;
@Entity
@Table(name = "users")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.findById",
                query = "SELECT u FROM User u WHERE u.id = :id"),
        @NamedQuery(name = "User.findByUsernameAndPassword",
                query = "SELECT u FROM User u WHERE u.username = :username "
                        + "and u.password = :password"),
        @NamedQuery(name = "User.findByUsername",
                query = "SELECT u FROM User u WHERE u.username = :username")})
public class User implements Principal, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "username")
    private String username;

    @JsonIgnore
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final Set<Bookmark> bookmarks = new HashSet<>();

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addBookmark(final Bookmark bookmark) {
        Objects.requireNonNull(bookmark);
        bookmark.setUser(this);
        bookmarks.add(bookmark);
    }

    public Set<Bookmark> getBookmarks() {
        return bookmarks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id,
                this.username,
                this.password);
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
        final User other = (User) obj;
        return Objects.equals(this.username, other.username)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username
                + ", password=" + password
                + ", bookmarks=" + bookmarks.size()
                + '}';
    }

    @Override
    public String getName() {
        return username;
    }

}
