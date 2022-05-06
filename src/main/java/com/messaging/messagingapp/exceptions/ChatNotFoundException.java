package com.messaging.messagingapp.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class ChatNotFoundException extends Exception{
    public ChatNotFoundException() {
        super("Chat not found.");
    }
}
