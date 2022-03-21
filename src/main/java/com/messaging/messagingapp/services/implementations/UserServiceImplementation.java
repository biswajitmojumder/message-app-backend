package com.messaging.messagingapp.services.implementations;

import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.data.repositories.UserRepository;
import com.messaging.messagingapp.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;

@Service
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final RoleServiceImplementation roleServiceImplementation;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImplementation(
            UserRepository userRepository,
            RoleServiceImplementation roleServiceImplementation,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleServiceImplementation = roleServiceImplementation;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(RegisterUserBindingModel newUser) throws InvalidParameterException{
        if(!isUsernameTaken(newUser.getUsername())){
            if(!isEmailTaken(newUser.getEmail())){
                UserEntity user = new UserEntity();
                modelMapper.map(newUser, user);
                user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                user.setRoles(List.of(roleServiceImplementation.returnUserRole()));
                userRepository.save(user);
            }
            else throw new InvalidParameterException("Email is taken!");
        }
        else throw new InvalidParameterException("Username is taken!");
    }

    @Override
    public UserEntity returnUserByUsername(String username) {
        return null;
    }

    private boolean isUsernameTaken(String username){
        return userRepository.findByUsername(username).isPresent();
    }
    private boolean isEmailTaken(String email){
        return userRepository.findByEmail(email).isPresent();
    }
}
