package com.messaging.messagingapp.data.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class MessageEntity extends BaseEntity{
    @ManyToOne
    private ChatParticipantEntity sender;
    @ManyToOne
    private ChatEntity chat;
    @ManyToOne
    private MessageEntity replyingTo;
    @Lob
    private String textContent;
    private boolean replyDeleted = false;
    private String imageLink;
    @CreationTimestamp
    private Timestamp createTime;
}
