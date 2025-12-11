package in.amalamama.authify.service;

import in.amalamama.authify.entity.UserEntity;
import in.amalamama.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
//class need to be used in the securityConfig file
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity existingUser=userRepository.findByEmail(email).orElseThrow(
                ()->new UsernameNotFoundException("user not found with email "+email));
        return new User(
                existingUser.getEmail(),
                existingUser.getPassword(),
                new ArrayList<>() //this list present the authorities
        );
    }
}
