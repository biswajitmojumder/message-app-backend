package com.messaging.messagingapp.data.models.viewModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatParticipantViewModel {
    private String nickname;
    private String username;
    private String publicName;
    private String profilePicLink;
}
