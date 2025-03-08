package hu.bmiklos.bc.business.security;

import org.springframework.security.core.GrantedAuthority;

public enum BookClubAuthority implements GrantedAuthority {
    BOOKCLUB_USER("ROLE_USER"), BOOKCLUB_ADMIN("ROLE_ADMIN");

    private String authority;

    BookClubAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

}
