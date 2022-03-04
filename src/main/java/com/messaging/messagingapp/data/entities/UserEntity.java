package com.messaging.messagingapp.data.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "app_users")
@Getter
@Setter
public class UserEntity extends BaseEntity{
    @NotBlank
    @Length(min = 3, max = 16)
    @Column(unique = true)
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    @Column(unique = true)
    //@Pattern()
    private String email;
    @NotBlank
    private String publicName;
    private String profilePicLink;
    @ManyToMany
    private List<RoleEntity> roles;
    @ManyToMany
    private List<ChatEntity> chats;
    @OneToMany(mappedBy = "sender")
    private List<MessageEntity> messages;
}
