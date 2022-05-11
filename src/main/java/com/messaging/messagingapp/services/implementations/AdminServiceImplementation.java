package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.models.viewModel.AdminSearchRoleViewModel;
import com.messaging.messagingapp.data.models.viewModel.AdminSearchUserViewModel;
import com.messaging.messagingapp.data.repositories.UserRepository;
import com.messaging.messagingapp.data.repositories.pageableRepositories.PageableUserRepository;
import com.messaging.messagingapp.exceptions.UserNotFoundException;
import com.messaging.messagingapp.services.AdminService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImplementation implements AdminService {
    private final UserRepository userRepository;
    private final PageableUserRepository pageableUserRepository;
    private final ModelMapper modelMapper;

    public AdminServiceImplementation(
            UserRepository userRepository,
            PageableUserRepository pageableUserRepository,
            ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.pageableUserRepository = pageableUserRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AdminSearchUserViewModel searchUserById(Long id) throws UserNotFoundException {
        if (id > userRepository.count())
            throw new UserNotFoundException();
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()){
            AdminSearchUserViewModel mappedUser = new AdminSearchUserViewModel();
            modelMapper.map(user.get(), mappedUser);
            List<AdminSearchRoleViewModel> mappedRoles = new ArrayList<>();
            user.get().getRoles().forEach(r -> {
                AdminSearchRoleViewModel mappedRole = new AdminSearchRoleViewModel();
                modelMapper.map(r, mappedRole);
                mappedRoles.add(mappedRole);
            });
            mappedUser.setRoles(mappedRoles);
            return mappedUser;
        }
        else throw new UserNotFoundException();
    }

    @Override
    public List<AdminSearchUserViewModel> searchUsersByUsername(String username, int page) {
        Pageable pageable = PageRequest.of(page, 5);
        List<UserEntity> users = pageableUserRepository.findAllByUsernameContains(username, pageable);
        List<AdminSearchUserViewModel> mappedUsers = new ArrayList<>();
        for (UserEntity user :
                users) {
            AdminSearchUserViewModel mappedUser = new AdminSearchUserViewModel();
            List<AdminSearchRoleViewModel> mappedRoles = new ArrayList<>();
            modelMapper.map(user, mappedUser);
            user.getRoles().forEach(r -> {
                AdminSearchRoleViewModel mappedRole = new AdminSearchRoleViewModel();
                modelMapper.map(r, mappedRole);
                mappedRoles.add(mappedRole);
            });
            mappedUsers.add(mappedUser);
        }
        return mappedUsers;
    }
}
