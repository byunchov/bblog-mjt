package net.byunchov.bblog.users.services;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.byunchov.bblog.users.converter.UserConverter;
import net.byunchov.bblog.users.dto.UserAuthDto;
import net.byunchov.bblog.users.dto.UserDto;
import net.byunchov.bblog.users.exceptions.UserAlreadyExistsException;
import net.byunchov.bblog.users.exceptions.UserNotAuthenticatedException;
import net.byunchov.bblog.users.exceptions.UserNotFoundException;
import net.byunchov.bblog.users.models.Authority;
import net.byunchov.bblog.users.models.UserDao;
import net.byunchov.bblog.users.repositories.AuthorityRepository;
import net.byunchov.bblog.users.repositories.UserRepository;
import net.byunchov.bblog.utils.DataUtils;
import net.byunchov.bblog.utils.MessageUtils;

@Service
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

    public UserDto createUser(UserDto user) throws UserAlreadyExistsException {

        boolean isExistingUser = userRepository.findByUsername(user.getUsername()).isPresent();

        if (isExistingUser) {
            throw new UserAlreadyExistsException(String.format(MessageUtils.USER_EXISTS_MSG, user.getUsername()));
        }
        
        UserDao userDao = userConverter.convertDtoToEntity(user);
        userDao.setAuthorities(setAuthoritiesByUser(userDao));
        userDao.setPassword(passwordEncoder().encode(user.getPassword()));
        UserDao createdUser = userRepository.save(userDao);

        return userConverter.convertEntityToDto(createdUser);
    }

    public UserDto updateUser(Long id, UserDto user)
            throws UserNotFoundException, UserNotAuthenticatedException, AccessDeniedException {
        // log.info(user.toString());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserDao loggedInUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UserNotAuthenticatedException(MessageUtils.USER_NOT_AUTH_MSG));

        UserDao existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(MessageUtils.USER_NOT_FOUND_MSG, id)));

        DataUtils.copyNonNullProperties(user, existingUser);

        String loggedUserName = loggedInUser.getUsername();

        Boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!loggedUserName.equals(existingUser.getUsername()) && !isAdmin) {
            throw new AccessDeniedException(MessageUtils.ACC_DENIED_MSG);
        }

        if (user.getPassword() != null) {
            existingUser.setPassword(passwordEncoder().encode(user.getPassword()));
        }

        if (existingUser.getAuthorities().isEmpty()) {
            existingUser.setAuthorities(setAuthoritiesByUser(existingUser));
        }

        UserDao createdUser = userRepository.save(existingUser);

        return userConverter.convertEntityToDto(createdUser);
    }

    public void delete(UserDao user) {
        try {
            userRepository.delete(user);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format(MessageUtils.USER_NOT_FOUND_MSG, user.getId()));
        }
    }

    public void deleteById(Long id) throws UserNotFoundException {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format(MessageUtils.USER_NOT_FOUND_MSG, id));
        }
    }

    public UserDto findById(Long id) throws UserNotFoundException {
        UserDao user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(MessageUtils.USER_NOT_FOUND_MSG, id)));
        return userConverter.convertEntityToDto(user);
    }

    public UserDto findByUsername(String username) throws UserNotFoundException {
        UserDao user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format(MessageUtils.USER_NOT_FOUND_MSG, username)));
        return userConverter.convertEntityToDto(user);
    }

    public UserAuthDto authByUsername(String username) throws UserNotFoundException {
        UserDao user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format(MessageUtils.USER_NOT_FOUND_MSG, username)));
        return userConverter.convertEntityToAuthDto(user);
    }

    public UserDto findOneByEmail(String email) throws UserNotFoundException {
        UserDao user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(MessageUtils.USER_NOT_FOUND_MSG, email)));
        return userConverter.convertEntityToDto(user);
    }

    public List<GrantedAuthority> getGrantedAuthoritiesByName(String username) throws UserNotFoundException {

        UserDao user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format(MessageUtils.USER_NOT_FOUND_MSG, username)));

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
