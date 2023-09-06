package hu.bmiklos.bc.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID bookId;

    @Column
    private UUID userId;

    @Column
    private Integer userExternalId;

    @OneToOne
    @JoinColumn(name = "bookId", referencedColumnName = "id", insertable = false, updatable = false)
    private Book book;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id", insertable = false, updatable = false)
    private User userById;

    @OneToOne
    @JoinColumn(name = "userExternalId", referencedColumnName = "externalId", insertable = false, updatable = false)
    private User userByExternalId;

    public Vote() {}

    public Vote(UUID bookId, UUID userId) {
        this(bookId, userId, null);
    }

    public Vote(UUID bookId, Integer userExternalId) {
        this(bookId, null, userExternalId);
    }

    public Vote(UUID bookId, UUID userId, Integer userExternalId) {
        this.bookId = bookId;
        this.userId = userId;
        this.userExternalId = userExternalId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Integer getUserExternalId() {
        return userExternalId;
    }

    public void setUserExternalId(Integer userExternalId) {
        this.userExternalId = userExternalId;
    }

    public User getUserById() {
        return userById;
    }

    public void setUserById(User userById) {
        this.userById = userById;
    }

    public User getUserByExternalId() {
        return userByExternalId;
    }

    public void setUserByExternalId(User userByExternalId) {
        this.userByExternalId = userByExternalId;
    }
}
