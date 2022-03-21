package com.messaging.messagingapp.data.models.bindingModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageBindingModel {
    private String textContent;
    private String imageLink;
    private Long chatId;
    private Long messageReplyId;
}
