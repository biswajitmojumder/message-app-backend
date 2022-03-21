package com.messaging.messagingapp.services;

import com.messaging.messagingapp.data.entities.MessageEntity;
import com.messaging.messagingapp.data.models.bindingModel.MessageBindingModel;

import java.io.FileNotFoundException;

public interface MessageService {
    void sendMessage(MessageBindingModel incomingMessage, String senderUsername) throws FileNotFoundException;
}
