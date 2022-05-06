package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.data.models.viewModel.SmallUserInfoViewModel;
import com.messaging.messagingapp.exceptions.UserNotFoundException;

import java.util.List;

public interface UserService {
    UserEntity registerUser(RegisterUserBindingModel newUser);
    UserEntity returnUserByUsername(String username) throws UserNotFoundException;
    UserEntity returnUserById(Long id) throws UserNotFoundException;
    SmallUserInfoViewModel returnSmallInfoOfLoggedUser(String username) throws UserNotFoundException;
    List<SmallUserInfoViewModel> searchUsersByUsername(String username, int pageNum);
    void changeProfilePictureLinkOfLoggedUser(String username, String newProfilePictureLink) throws UserNotFoundException;
    void changePublicNameOfLoggedUser(String username, String newPublicName) throws UserNotFoundException;
    void changePasswordOfLoggedUser(String username, String oldPassword, String newPassword) throws UserNotFoundException;
}
