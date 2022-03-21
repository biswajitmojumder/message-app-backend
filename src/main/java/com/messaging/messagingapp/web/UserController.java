package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.services.implementations.UserServiceImplementation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidParameterException;

@Controller
@RequestMapping("/api")
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

    @GetMapping("/is-logged-in")
    public ResponseEntity<?> isCurrentSessionValid(HttpServletRequest request){
        if(request.isRequestedSessionIdValid())
            return ResponseEntity.ok().build();
        return ResponseEntity.status(403).build();
    }
}
