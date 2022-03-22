package com.messaging.messagingapp.data.models.viewModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatMessagesViewModel {
    private Long id;
    private String participantNickname;
    private String participantProfilePicLink;
    private List<MessageViewModel> last50Messages;
}
