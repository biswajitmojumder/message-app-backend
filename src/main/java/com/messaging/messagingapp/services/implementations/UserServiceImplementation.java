package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.data.models.viewModel.SmallUserInfoViewModel;
import com.messaging.messagingapp.data.repositories.UserRepository;
import com.messaging.messagingapp.data.repositories.pageableRepositories.PageableUserRepository;
import com.messaging.messagingapp.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final PageableUserRepository pageableUserRepository;
    private final RoleServiceImplementation roleServiceImplementation;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImplementation(
            UserRepository userRepository,
            PageableUserRepository pageableUserRepository,
            RoleServiceImplementation roleServiceImplementation,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.pageableUserRepository = pageableUserRepository;
        this.roleServiceImplementation = roleServiceImplementation;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity registerUser(RegisterUserBindingModel newUser) throws InvalidParameterException{
        if(!isUsernameTaken(newUser.getUsername())){
            if(!isEmailTaken(newUser.getEmail())){
                UserEntity user = new UserEntity();
                modelMapper.map(newUser, user);
                user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                user.setRoles(List.of(roleServiceImplementation.returnUserRole()));
                userRepository.save(user);
                return user;
            }
            else throw new InvalidParameterException("Email is taken!");
        }
        else throw new InvalidParameterException("Username is taken!");
    }

    @Override
    public UserEntity returnUserByUsername(String username) {
        Optional<UserEntity> userOrNull = userRepository.findByUsername(username);
        if(userOrNull.isPresent())
            return userOrNull.get();
        throw new NullPointerException("User not found");
    }

    @Override
    public UserEntity returnUserById(Long id) {
        Optional<UserEntity> userOrNull = userRepository.findById(id);
        if(userOrNull.isPresent())
            return userOrNull.get();
        throw new NullPointerException("User not found");
    }

    @Override
    public SmallUserInfoViewModel returnSmallInfoOfLoggedUser(String username) {
        UserEntity user = returnUserByUsername(username);
        SmallUserInfoViewModel mappedUser = new SmallUserInfoViewModel();
        modelMapper.map(user, mappedUser);
        return mappedUser;
    }

    @Override
    public List<SmallUserInfoViewModel> searchUsersByUsername(String username, int pageNum) {
        Pageable page = PageRequest.of(pageNum, 10);
        List<UserEntity> foundUsers = pageableUserRepository.findAllByUsernameContains(username, page);
        List<SmallUserInfoViewModel> mappedFoundUsers = new ArrayList<>();
        for (UserEntity user :
                foundUsers) {
            SmallUserInfoViewModel mappedUser = new SmallUserInfoViewModel();
            modelMapper.map(user, mappedUser);
            mappedFoundUsers.add(mappedUser);
        }
        return mappedFoundUsers;
    }

    private boolean isUsernameTaken(String username){
        return userRepository.findByUsername(username).isPresent();
    }
    private boolean isEmailTaken(String email){
        return userRepository.findByEmail(email).isPresent();
    }
}
