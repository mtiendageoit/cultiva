package com.cultiva.webapp.security.oauth2;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.cultiva.webapp.security.*;
import com.cultiva.webapp.user.UserRole;

public class GoogleOAuth2UserInfo {
    public static UserPrincipal userPrincipalFrom(Map<String, Object> attributes) {
        String name = attributes.get("name").toString();
        String email = attributes.get("email").toString();
        String avatar = attributes.get("picture").toString();
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(UserRole.USER.name()));
        return new UserPrincipal(email, name, avatar, authorities, AuthProvider.google);
    }
}
