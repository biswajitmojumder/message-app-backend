package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.models.viewModel.AdminSearchUserViewModel;
import com.messaging.messagingapp.exceptions.UserNotFoundException;

import java.util.List;

public interface AdminService {
    AdminSearchUserViewModel searchUserById(Long id) throws UserNotFoundException;
    List<AdminSearchUserViewModel> searchUsersByUsername(String username, int page);
}
