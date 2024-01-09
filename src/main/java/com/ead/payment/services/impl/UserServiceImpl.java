package com.ead.payment.services.impl;

import com.ead.payment.models.UserModel;
import com.ead.payment.repositories.UserRepository;
import com.ead.payment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Override
    public UserModel save(UserModel userModel) {
        return userRepository.save(userModel);
    }
    @Transactional
    @Override
    public void delete(UUID userId) {
        userRepository.deleteById(userId);
    }
}
