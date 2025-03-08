package hu.bmiklos.bc.model.entity;

import static jakarta.persistence.FetchType.EAGER;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {

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
    private PasswordEntity password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = EAGER)
    private List<EmailEntity> emails;

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isAdmin, externalId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof UserEntity))
            return false;
        UserEntity other = (UserEntity) obj;
        return Objects.equals(id, other.id) && Objects.equals(name, other.name) && isAdmin == other.isAdmin
                && externalId == other.externalId;
    }
}
