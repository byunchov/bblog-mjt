package net.byunchov.bblog.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import lombok.extern.slf4j.Slf4j;
import net.byunchov.bblog.users.exceptions.UserNotFoundException;
import net.byunchov.bblog.users.models.UserDao;
import net.byunchov.bblog.users.services.UserService;

@Controller
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @SneakyThrows
    @GetMapping("/{id}")
    public ResponseEntity<UserDao> getUserById(@PathVariable Long id) {
        UserDao user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<UserDao> createUser(@RequestBody UserDao user) {
        log.info(user.toString());
        UserDao newAccount = userService.save(user);
        return new ResponseEntity<UserDao>(newAccount, HttpStatus.CREATED);
    }

    @PatchMapping(value="/{id}/update")
    public ResponseEntity<UserDao> updateUser(@PathVariable Long id, @RequestBody UserDao user) {
        var updatedUser = userService.updateUser(id, user);
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
}
