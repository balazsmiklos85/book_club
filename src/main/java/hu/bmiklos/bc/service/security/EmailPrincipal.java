package hu.bmiklos.bc.service.security;

import static hu.bmiklos.bc.service.security.BookClubAuthority.BOOKCLUB_ADMIN;
import static hu.bmiklos.bc.service.security.BookClubAuthority.BOOKCLUB_USER;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import hu.bmiklos.bc.model.Email;
import hu.bmiklos.bc.model.Password;
import hu.bmiklos.bc.model.User;

public class EmailPrincipal implements UserDetails {

    private Email email;

    public EmailPrincipal(Email email) {
        this.email = email;
    }

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
