package com.bossabox.supervuttr.service;

import com.bossabox.supervuttr.data.AppUser;
import com.bossabox.supervuttr.error.UserNotFoundException;
import com.bossabox.supervuttr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Checks if the unique username is not already taken
     * @param username the username to check for availability
     * @return true if the username does not exist, otherwise false
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Retrieves a user by its username from the database
     * @param username the username of the user to retrieve
     * @return the AppUser with the specified unique username
     * @throws UserNotFoundException if a user with this username
     * does not exist
     */
    public AppUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    /**
     * Creates a new AppUser on the database
     * @param user the AppUser to create, with plain text password
     * @return the saved AppUser, with the auto generated id
     * @throws IllegalArgumentException if the username is not available
     */
    @Transactional
    public AppUser createUser(AppUser user) {
        // Ensures the user will be created, not updated
        user.setId(null);

        if (!isUsernameAvailable(user.getUsername())) {
            throw new IllegalArgumentException("Username not available");
        }

        var plainPwd = user.getPassword();
        var hashedPwd = passwordEncoder.encode(plainPwd);
        user.setPassword(hashedPwd);

        return userRepository.save(user);
    }

    /**
     * Deletes a AppUser from the database
     * @param username the unique username of the AppUser to delete
     * @throws UserNotFoundException if the AppUser does not exist;
     */
    @Transactional
    public void deleteUser(String username) {
        var userToDelete =  getUserByUsername(username);
        userRepository.delete(userToDelete);
    }

}
