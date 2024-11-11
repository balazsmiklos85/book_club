package hu.bmiklos.bc.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Deprecated
public class User implements Serializable {

    private static final long serialVersionUID = -688746974752178530L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    @Column(nullable = false)
    private boolean isAdmin;

    @Column(nullable = false, unique = true)
    private int externalId;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Password password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Email> emails;

    public User() {}

    public User(String name, boolean isAdmin, int externalId) {
        this.name = name;
        this.isAdmin = isAdmin;
        this.externalId = externalId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getExternalId() {
        return externalId;
    }

    public void setExternalId(int externalId) {
        this.externalId = externalId;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isAdmin, externalId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof User))
            return false;
        User other = (User) obj;
        return Objects.equals(id, other.id) && Objects.equals(name, other.name) && isAdmin == other.isAdmin
                && externalId == other.externalId;
    }
}
