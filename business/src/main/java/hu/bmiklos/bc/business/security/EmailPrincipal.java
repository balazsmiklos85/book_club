package hu.bmiklos.bc.business.security;

import static hu.bmiklos.bc.business.security.BookClubAuthority.BOOKCLUB_ADMIN;
import static hu.bmiklos.bc.business.security.BookClubAuthority.BOOKCLUB_USER;

import hu.bmiklos.bc.domain.entities.Email;
import hu.bmiklos.bc.domain.entities.Password;
import hu.bmiklos.bc.domain.entities.User;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class EmailPrincipal implements UserDetails {

    private final Email email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var result = new ArrayList<GrantedAuthority>(2);
        result.add(BOOKCLUB_USER);
        User user = email.getUser();
        if (user.isAdmin()) {
            result.add(BOOKCLUB_ADMIN);
        }
        return result;
    }

    public int getExternalId() {
        return email.getUser().getExternalId();
    }

    @Override
    public String getPassword() {
        User user = email.getUser();
        Password password = user.getPassword();
        return password.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return email.getEmailAddress();
    }

    public User getUser() {
        return email.getUser();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
