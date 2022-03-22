package com.messaging.messagingapp.data.models.viewModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatListViewModel {
    private Long id;
    private String chatParticipantName;
    private String chatParticipantImageLink;
}
