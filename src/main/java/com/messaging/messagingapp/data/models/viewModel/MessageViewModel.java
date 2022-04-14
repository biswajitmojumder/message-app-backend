package com.messaging.messagingapp.data.models.viewModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageViewModel {
    private String senderUsername;
    private String senderNickname;
    private String textContent;
    private String imageLink;
    private ReplyMessageViewModel replyTo;
    private Long chatId;
}
