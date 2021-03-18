package com.bossabox.supervuttr.security;

import com.bossabox.supervuttr.error.UserNotFoundException;
import com.bossabox.supervuttr.repository.UserRepository;
import com.bossabox.supervuttr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        try {
            var user = userService.getUserByUsername(s);
            return new User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.emptyList()
            );
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
