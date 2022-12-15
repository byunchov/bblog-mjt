package net.byunchov.bblog.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.SneakyThrows;
import net.byunchov.bblog.users.dto.UserDto;
import net.byunchov.bblog.users.exceptions.EmailAlreadyUsedException;
import net.byunchov.bblog.users.exceptions.InvalidEmailException;
import net.byunchov.bblog.users.exceptions.UserAlreadyExistsException;
import net.byunchov.bblog.users.exceptions.UserNotAuthenticatedException;
import net.byunchov.bblog.users.exceptions.UserNotFoundException;
import net.byunchov.bblog.users.services.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @SneakyThrows
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {
        UserDto newAccount = userService.createUser(user);
        return new ResponseEntity<UserDto>(newAccount, HttpStatus.CREATED);
    }

    @PatchMapping(value="/{id}/update")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto user) {
        UserDto updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(value="/{id}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }    

    @ExceptionHandler(value = UserNotFoundException.class)
    private ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    private ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = InvalidEmailException.class)
    private ResponseEntity<String> handleInvalidEmailException(InvalidEmailException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = EmailAlreadyUsedException.class)
    private ResponseEntity<String> handleEmailAlreadyUsedException(EmailAlreadyUsedException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = UserNotAuthenticatedException.class)
    private ResponseEntity<String> handleUserNotAuthenticatedException(UserNotAuthenticatedException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    private ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }
}
