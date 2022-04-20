package com.messaging.messagingapp.data.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "chat_participants")
@Getter
@Setter
public class ChatParticipantEntity extends BaseEntity{
    @ManyToOne
    private UserEntity user;
    @ManyToOne
    private ChatEntity chat;
    @OneToMany(mappedBy = "sender")
    private List<MessageEntity> messages;
    private String nickname;
    private Boolean unseenMessages;
    private Boolean chatClosed = false;
}
