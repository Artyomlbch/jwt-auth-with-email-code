package com.artyom.jwtsms.service;

import com.artyom.jwtsms.entity.User;
import com.artyom.jwtsms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

}
