package com.messaging.messagingapp.security.Handlers;

import com.messaging.messagingapp.data.entities.UserEntity;
import com.messaging.messagingapp.data.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoginHandler implements UserDetailsService {
    private final UserRepository userRepository;

    public LoginHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userOrNull = userRepository.findByUsername(username);
        if(userOrNull.isPresent()){
            return userEntityToUserDetails(userOrNull.get());
        }
        throw new UsernameNotFoundException("Username not found");
    }

    private UserDetails userEntityToUserDetails(UserEntity user){
        List<GrantedAuthority> roles = user.getRoles()
                .stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRoleName().name()))
                .collect(Collectors.toList());
        return new User(user.getUsername(), user.getPassword(), roles);
    }
}
