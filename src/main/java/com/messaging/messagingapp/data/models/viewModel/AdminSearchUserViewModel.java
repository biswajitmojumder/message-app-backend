package com.messaging.messagingapp.data.models.viewModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminSearchUserViewModel {
    private Long id;
    private String username;
    private String publicName;
    private String profilePicLink;
    private List<AdminSearchRoleViewModel> roles;
}
