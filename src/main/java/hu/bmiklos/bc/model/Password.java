package hu.bmiklos.bc.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_password")
public class Password implements Serializable {

    private static final long serialVersionUID = -2679510096228680927L;

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "salt")
    private String salt;

    @Column(name = "hash_algorithm")
    private String hashAlgorithm;

	@OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Password() {}

    public Password(UUID userId, String passwordHash, String salt, String hashAlgorithm) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.hashAlgorithm = hashAlgorithm;
    }

    public Password(User user, String passwordHash, String salt, String hashAlgorithm) {
        this(user.getId(), passwordHash, salt, hashAlgorithm);
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID id) {
        this.userId = id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }
}
