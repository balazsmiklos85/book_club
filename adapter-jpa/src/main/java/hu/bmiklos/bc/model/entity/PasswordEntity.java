package hu.bmiklos.bc.model.entity;

import static jakarta.persistence.FetchType.EAGER;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_password")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordEntity implements Serializable {
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

	  @OneToOne(fetch = EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
