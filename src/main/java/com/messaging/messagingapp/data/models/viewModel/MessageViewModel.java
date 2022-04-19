package com.messaging.messagingapp.data.models.viewModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageViewModel {
    private Long id;
    private String senderUsername;
    private String senderNickname;
    private String textContent;
    private String imageLink;
    private ReplyMessageViewModel replyTo;
    private Long chatId;
    private Boolean replyDeleted;
    private Boolean unseenMessages;
}
