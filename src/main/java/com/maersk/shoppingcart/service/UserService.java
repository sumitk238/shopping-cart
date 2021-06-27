package com.maersk.shoppingcart.service;

import com.maersk.shoppingcart.entity.User;
import com.maersk.shoppingcart.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sumit Kumar
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
