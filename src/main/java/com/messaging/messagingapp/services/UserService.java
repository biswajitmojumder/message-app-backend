package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;

public interface UserService {
    UserEntity registerUser(RegisterUserBindingModel newUser);
    UserEntity returnUserByUsername(String username);
    UserEntity returnUserById(Long id);
}
