package com.messaging.messagingapp.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class UserNotFoundException extends Exception{
    public UserNotFoundException(){
        super("User not found");
    }
}
