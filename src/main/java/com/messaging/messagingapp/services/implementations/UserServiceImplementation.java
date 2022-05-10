package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.RoleEntity;
import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.enums.RoleEnum;
import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.data.models.viewModel.SmallUserInfoViewModel;
import com.messaging.messagingapp.data.repositories.UserRepository;
import com.messaging.messagingapp.data.repositories.pageableRepositories.PageableUserRepository;
import com.messaging.messagingapp.exceptions.UserNotFoundException;
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
                if(userRepository.count() == 0)
                    user.setRoles(List.of(
                            roleServiceImplementation.returnUserRole(),
                            roleServiceImplementation.returnAdminRole()));
                user.setProfilePicLink("https://images.nightcafe.studio//assets/profile.png?tr=w-1600,c-at_max");
                userRepository.save(user);
                return user;
            }
            else throw new InvalidParameterException("Email is taken!");
        }
        else throw new InvalidParameterException("Username is taken!");
    }

    @Override
    public UserEntity returnUserByUsername(String username) throws UserNotFoundException {
        Optional<UserEntity> userOrNull = userRepository.findByUsername(username);
        if(userOrNull.isPresent())
            return userOrNull.get();
        throw new UserNotFoundException();
    }

    @Override
    public UserEntity returnUserById(Long id) throws UserNotFoundException {
        Optional<UserEntity> userOrNull = userRepository.findById(id);
        if(userOrNull.isPresent())
            return userOrNull.get();
        throw new UserNotFoundException();
    }

    @Override
    public SmallUserInfoViewModel returnSmallInfoOfLoggedUser(String username) throws UserNotFoundException {
        UserEntity user = returnUserByUsername(username);
        SmallUserInfoViewModel mappedUser = new SmallUserInfoViewModel();
        modelMapper.map(user, mappedUser);
        if(user.getRoles().stream().map(RoleEntity::getRoleName).anyMatch(r -> r == RoleEnum.ADMIN)){
            mappedUser.setAdmin(true);
        }
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

    @Override
    public void changeProfilePictureLinkOfLoggedUser(String username, String newProfilePictureLink)
            throws UserNotFoundException {
        UserEntity loggedUser = returnUserByUsername(username);
        loggedUser.setProfilePicLink(newProfilePictureLink);
        userRepository.save(loggedUser);
    }

    @Override
    public void changePublicNameOfLoggedUser(String username, String newPublicName) throws UserNotFoundException {
        UserEntity loggedUser = returnUserByUsername(username);
        loggedUser.setPublicName(newPublicName);
        userRepository.save(loggedUser);
    }

    @Override
    public void changePasswordOfLoggedUser(String username, String oldPassword, String newPassword)
            throws UserNotFoundException, InvalidParameterException {
        UserEntity loggedUser = returnUserByUsername(username);
        if (passwordEncoder.matches(oldPassword, loggedUser.getPassword())){
            loggedUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(loggedUser);
        }
        else throw new InvalidParameterException("Wrong password");
    }
    private boolean isUsernameTaken(String username){
        return userRepository.findByUsername(username).isPresent();
    }
    private boolean isEmailTaken(String email){
        return userRepository.findByEmail(email).isPresent();
    }
}
