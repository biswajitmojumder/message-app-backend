package com.messaging.messagingapp.data.repositories;

import com.messaging.messagingapp.data.entities.RoleEntity;
import com.messaging.messagingapp.data.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleName(RoleEnum roleName);
}
