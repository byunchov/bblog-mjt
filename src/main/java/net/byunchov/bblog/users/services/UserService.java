package net.byunchov.bblog.users.services;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.byunchov.bblog.users.converter.UserConverter;
import net.byunchov.bblog.users.dto.UserAuthDto;
import net.byunchov.bblog.users.dto.UserDto;
import net.byunchov.bblog.users.exceptions.UserNotFoundException;
import net.byunchov.bblog.users.models.Authority;
import net.byunchov.bblog.users.models.UserDao;
import net.byunchov.bblog.users.repositories.AuthorityRepository;
import net.byunchov.bblog.users.repositories.UserRepository;
import net.byunchov.bblog.utils.DataUtils;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    private static final String ROLE_USER = "ROLE_USER";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public UserDto createUser(UserDao user) {

        if (user.getId() == null) {            
            user.setAuthorities(setAuthoritiesByUser(user));
        }
        user.setPassword(passwordEncoder().encode(user.getPassword()));
        UserDao createdUser = userRepository.save(user);

        return userConverter.convertEntityToDto(createdUser);
    }

    public UserDto updateUser(Long id, UserDao user) {
        log.info(user.toString());
        UserDao existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by id %d was not found", id)));
        DataUtils.copyNonNullProperties(user, existingUser);

        if (user.getPassword() != null) {
            existingUser.setPassword(passwordEncoder().encode(user.getPassword()));
        }

        if(existingUser.getAuthorities().isEmpty()){
            existingUser.setAuthorities(setAuthoritiesByUser(existingUser));
        }

        UserDao createdUser = userRepository.save(existingUser);

        return userConverter.convertEntityToDto(createdUser);
    }

    public void delete(UserDao user) {
        userRepository.delete(user);
    }

    public void deleteById(Long id) throws UserNotFoundException {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("User by id %d was not found", id));
        }
    }

    public UserDto findById(Long id) throws UserNotFoundException {
        UserDao user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by id %d was not found", id)));
        return userConverter.convertEntityToDto(user);
    }

    public UserDto findByUsername(String username) throws UserNotFoundException {
        UserDao user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by %s was not found", username)));
        return userConverter.convertEntityToDto(user);
    }

    public UserAuthDto authByUsername(String username) throws UserNotFoundException {
        UserDao user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by %s was not found", username)));
        return userConverter.convertEntityToAuthDto(user);
    }

    public UserDto findOneByEmail(String email) throws UserNotFoundException {
        UserDao user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with %s was not found", email)));
        return userConverter.convertEntityToDto(user);
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

    public Set<Authority> setAuthoritiesByUser(UserDao user) throws UserNotFoundException {
        Set<Authority> authorities = user.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            authorities = new HashSet<>();
            authorityRepository.findById(ROLE_USER).ifPresent(authorities::add);
        }

        return authorities;
    }
}
