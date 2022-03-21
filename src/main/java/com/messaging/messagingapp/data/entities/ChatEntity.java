package com.messaging.messagingapp.data.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "chats")
@Getter
@Setter
public class ChatEntity extends BaseEntity{
    @OneToMany(mappedBy = "chat")
    private List<ChatParticipantEntity> participants;
    @OneToMany(mappedBy = "chat")
    private List<MessageEntity> messages;
    private String backgroundImageLink;
}
