package hu.bmiklos.bc.service.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import hu.bmiklos.bc.model.Email;

public class EmailPrincipal implements UserDetails {

    private Email email;

    public EmailPrincipal(Email email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (email.getUser().isAdmin()) {
            return Arrays.asList(() -> "ROLE_ADMIN");
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getPassword() {
        return email.getUser().getPassword().getPasswordHash();
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
