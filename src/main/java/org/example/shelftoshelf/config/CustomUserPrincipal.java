package org.example.shelftoshelf.config;

import lombok.Getter;
import org.example.shelftoshelf.Entity.Enum.CustomerRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String name;
    private final String surname;
    private final CustomerRole role;
    private final boolean verified;
    private final boolean isStoreEntity;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserPrincipal(Long id,
                               String email,
                               String password,
                               String name,
                               String surname,
                               CustomerRole role,
                               boolean verified,
                               boolean isStoreEntity) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.verified = verified;
        this.isStoreEntity = isStoreEntity;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return verified;
    }
}
