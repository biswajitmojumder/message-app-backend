package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.services.implementations.UserServiceImplementation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.InvalidParameterException;

@Controller
public class UserController {
    private final UserServiceImplementation userServiceImplementation;

    public UserController(UserServiceImplementation userServiceImplementation) {
        this.userServiceImplementation = userServiceImplementation;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(RegisterUserBindingModel newUserModel){
        if(!newUserModel.getUsername().trim().isEmpty() &&
                !newUserModel.getEmail().trim().isEmpty() &&
                !newUserModel.getPublicName().trim().isEmpty() &&
                !newUserModel.getPassword().trim().isEmpty()
        ){
            try {
                userServiceImplementation.registerUser(newUserModel);
            }catch (InvalidParameterException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.badRequest().body("Empty parameters not allowed");
    }
}
