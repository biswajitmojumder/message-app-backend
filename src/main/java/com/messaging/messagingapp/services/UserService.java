package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;

public interface UserService {
    void registerUser(RegisterUserBindingModel newUser);
}
