package hu.bmiklos.bc.model.entity;

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
public class VoteEntity {
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
    private BookEntity book;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity userById;

    @OneToOne
    @JoinColumn(name = "userExternalId", referencedColumnName = "externalId", insertable = false, updatable = false)
    private UserEntity userByExternalId;

    public VoteEntity() {}

    public VoteEntity(UUID bookId, UUID userId) {
        this(bookId, userId, null);
    }

    public VoteEntity(UUID bookId, Integer userExternalId) {
        this(bookId, null, userExternalId);
    }

    public VoteEntity(UUID bookId, UUID userId, Integer userExternalId) {
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

    public BookEntity getBook() {
        return book;
    }

    public void setBook(BookEntity book) {
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

    public UserEntity getUserById() {
        return userById;
    }

    public void setUserById(UserEntity userById) {
        this.userById = userById;
    }

    public UserEntity getUserByExternalId() {
        return userByExternalId;
    }

    public void setUserByExternalId(UserEntity userByExternalId) {
        this.userByExternalId = userByExternalId;
    }
}
