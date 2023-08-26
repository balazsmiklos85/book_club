package hu.bmiklos.bc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "emails")
public class Email {

    @Id
    @Column(name = "email_address", length = 255)
    private String emailAddress;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Email() {}

    public Email(String emailAddress, User user) {
        this.emailAddress = emailAddress;
        this.user = user;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}