package com.cultiva.webapp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.*;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import lombok.Data;

import java.util.*;

@Data
public class UserPrincipal implements UserDetails, OidcUser {
    private Long id;
    private String avatar;
    private AuthProvider provider;

    // For UserDetails
    private String email;
    private String username;
    private String password;
    private boolean enabled;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private List<GrantedAuthority> authorities;

    // For OidcUser
    private String name;
    private OidcIdToken idToken;
    private OidcUserInfo userInfo;
    private Map<String, Object> claims;
    private Map<String, Object> attributes;

    public UserPrincipal(long id, String fullname, String email, String password, List<GrantedAuthority> authorities,
            boolean enabled) {
        this.id = id;
        this.email = email;
        this.name = fullname;
        this.password = password;
        this.authorities = authorities;

        this.enabled = enabled;
        this.accountNonLocked = true;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;

        this.provider = AuthProvider.database;
    }

    public UserPrincipal(String email, String name, String avatar, List<GrantedAuthority> authorities, AuthProvider provider) {
        this.username = this.email = email;
        this.name = name;
        this.avatar = avatar;
        this.authorities = authorities;

        this.enabled = true;
        this.accountNonLocked = true;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;

        this.provider = provider;
    }
}