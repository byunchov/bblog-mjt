package net.byunchov.bblog.users.services;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.byunchov.bblog.users.exceptions.UserNotFoundException;
import net.byunchov.bblog.users.models.Authority;
import net.byunchov.bblog.users.models.UserDao;
import net.byunchov.bblog.users.repositories.AuthorityRepository;
import net.byunchov.bblog.users.repositories.UserRepository;
import net.byunchov.bblog.utils.DataUtils;

@Service
@Slf4j
public class UserService {
    // @Autowired
    // private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public UserDao save(UserDao user) {

        if (user.getId() == null) {
            Set<Authority> authorities = user.getAuthorities();
            if (authorities == null || authorities.isEmpty()) {
                authorities = new HashSet<>();
                authorityRepository.findById("ROLE_USER").ifPresent(authorities::add);
                user.setAuthorities(authorities);
            }
        }

        user.setPassword(passwordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserDao updateUser(Long id, UserDao user) {
        log.info(user.toString());
        UserDao existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by id %d was not found", id)));
        DataUtils.copyNonNullProperties(user, existingUser);

        if(user.getPassword() != null){
            existingUser.setPassword(passwordEncoder().encode(user.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    public void delete(UserDao user) {
        userRepository.delete(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public UserDao findById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by id %d was not found", id)));
    }

    public UserDao findByName(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by %s was not found", username)));
    }

    public UserDao findOneByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with %s was not found", email)));
    }

    public List<GrantedAuthority> getGrantedAuthoritiesByName(String username) throws UserNotFoundException {

        UserDao user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by %s was not found", username)));

        var authorities = user.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return Collections.emptyList();
        }

        return authorities
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
    }

}
