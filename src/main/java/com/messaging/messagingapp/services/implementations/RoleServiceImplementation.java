package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.RoleEntity;
import com.messaging.messagingapp.data.enums.RoleEnum;
import com.messaging.messagingapp.data.repositories.RoleRepository;
import com.messaging.messagingapp.services.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImplementation implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImplementation(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleEntity returnUserRole() {
        return roleRepository.findByRoleName(RoleEnum.USER).get();
    }
}
