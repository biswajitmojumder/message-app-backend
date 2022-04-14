package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.data.models.viewModel.SmallUserInfoViewModel;

import java.util.List;

public interface UserService {
    UserEntity registerUser(RegisterUserBindingModel newUser);
    UserEntity returnUserByUsername(String username);
    UserEntity returnUserById(Long id);
    SmallUserInfoViewModel returnSmallInfoOfLoggedUser(String username);
    List<SmallUserInfoViewModel> searchUsersByUsername(String username, int pageNum);
}
