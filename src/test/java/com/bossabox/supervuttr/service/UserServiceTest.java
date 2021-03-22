package com.bossabox.supervuttr.service;

import com.bossabox.supervuttr.data.AppUser;
import com.bossabox.supervuttr.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

/**
 * Implements relevant unit testing to UserService class
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService = new UserService();

    @Captor
    private ArgumentCaptor<AppUser> userCaptor;

    @Test
    public void test_noPlainPasswordsOnDatabase() {
        var userToAdd = new AppUser("1",
                "test_username",
                "plaintext");

        when(passwordEncoder.encode(eq(userToAdd.getPassword()))).thenReturn("hashedpassword");

        userService.createUser(userToAdd);

        verify(userRepository, atLeastOnce()).save(userCaptor.capture());
        var savedUser = userCaptor.getValue();

        verify(passwordEncoder, atLeastOnce()).encode(any());
        assertNotEquals("plaintext", savedUser.getPassword());
    }


}
