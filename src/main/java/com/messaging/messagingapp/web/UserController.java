package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.viewModel.SmallUserInfoViewModel;
import com.messaging.messagingapp.exceptions.UserNotFoundException;
import com.messaging.messagingapp.services.implementations.UserServiceImplementation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    private final UserServiceImplementation userServiceImplementation;

    public UserController(UserServiceImplementation userServiceImplementation) {
        this.userServiceImplementation = userServiceImplementation;
    }

/*    @PostMapping("/register")
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
    }*/

    @GetMapping("/is-logged-in")
    public ResponseEntity<?> isCurrentSessionValid(HttpServletRequest request){
        if(request.isRequestedSessionIdValid())
            return ResponseEntity.ok().build();
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/user-info/small")
    public ResponseEntity<?> returnSmallUserInfo(Principal principal){
        SmallUserInfoViewModel user;
        try {
            user = userServiceImplementation.returnSmallInfoOfLoggedUser(principal.getName());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUserByUsername(
            @RequestParam(value = "username") Optional<String> username,
            @RequestParam(value = "page", required = false, defaultValue = "0") int pageNum){
        if (username.isEmpty() || username.get().trim().isEmpty())
            return ResponseEntity.badRequest().body("Field cannot be empty");
        List<SmallUserInfoViewModel> foundUsers;
        foundUsers = userServiceImplementation.searchUsersByUsername(username.get(), pageNum);
        return ResponseEntity.ok(foundUsers);
    }

    @PatchMapping("/change/profile-picture-link")
    public ResponseEntity<?> changeProfilePicLink(
            @RequestParam(value = "profilePictureLink") Optional<String> newProfilePicLink,
            Principal principal){
        if(newProfilePicLink.isEmpty() || newProfilePicLink.get().trim().isEmpty())
            return ResponseEntity.badRequest().body("Field cannot be empty");
        try {
            userServiceImplementation.changeProfilePictureLinkOfLoggedUser(principal.getName(), newProfilePicLink.get());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/change/public-name")
    public ResponseEntity<?> changePublicName(
            @RequestParam(value = "publicName") Optional<String> newPublicName,
            Principal principal){
        if(newPublicName.isEmpty() || newPublicName.get().trim().isEmpty())
            return ResponseEntity.badRequest().body("Field cannot be empty");
        try {
            userServiceImplementation.changePublicNameOfLoggedUser(principal.getName(), newPublicName.get());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/change/password")
    public ResponseEntity<?> changePassword(
            @RequestParam(value = "oldPassword") Optional<String> oldPassword,
            @RequestParam(value = "newPassword") Optional<String> newPassword,
            Principal principal){
        if (oldPassword.isEmpty() || newPassword.isEmpty() ||
                oldPassword.get().trim().isEmpty() || newPassword.get().trim().isEmpty())
            return ResponseEntity.badRequest().body("Field cannot be empty");
        try {
            userServiceImplementation
                    .changePasswordOfLoggedUser(principal.getName(), oldPassword.get(), newPassword.get());
        } catch (InvalidParameterException e){
            return ResponseEntity.status(403).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok().build();
    }
}
