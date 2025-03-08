package hu.bmiklos.bc.model.entity;

import static jakarta.persistence.FetchType.EAGER;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "emails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailEntity implements Serializable {
    private static final long serialVersionUID = 4126957909483700948L;

    @Id
    @Column(name = "email_address", length = 255)
    private String emailAddress;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
