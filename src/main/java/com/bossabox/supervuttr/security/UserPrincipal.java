package com.bossabox.supervuttr.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserPrincipal extends User {
    private String id;

    public UserPrincipal(String name, String hashedPassword,
                         Collection<GrantedAuthority> authorities, String id) {
        super(name, hashedPassword, authorities);
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
