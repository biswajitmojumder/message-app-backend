package com.messaging.messagingapp.data.entities;

import com.messaging.messagingapp.data.enums.RoleEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class RoleEntity extends BaseEntity{
    @Enumerated(EnumType.STRING)
    private RoleEnum roleName;
    @ManyToMany(mappedBy = "roles")
    private List<UserEntity> users;
}
