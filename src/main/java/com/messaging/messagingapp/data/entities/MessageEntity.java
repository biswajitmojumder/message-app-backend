package com.messaging.messagingapp.data.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
    private String textContent;
    private String imageLink;
    private Boolean isSeen;
    @CreationTimestamp
    private Timestamp createTime;
}
