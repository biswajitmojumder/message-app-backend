package com.messaging.messagingapp.data.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class MessageEntity extends BaseEntity{
    @ManyToOne
    private UserEntity sender;
    @ManyToOne
    private ChatEntity chat;
    private String textContent;
    private String imageLink;
}
