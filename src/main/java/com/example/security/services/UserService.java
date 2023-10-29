package com.example.security.services;

import com.example.security.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    public User createUser(Map<String, String> map) {
        User user = new User();
        user.setFirstname(map.get("firstname"));
        user.setLastname(map.get("lastname"));
        user.setEmail(map.get("email"));
        user.setGender(map.get("gender"));


        return user;
    }

}
