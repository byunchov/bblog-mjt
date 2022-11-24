package net.byunchov.bblog.posts.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;

import lombok.SneakyThrows;
import net.byunchov.bblog.posts.dto.PostDto;
import net.byunchov.bblog.posts.exceptions.PostNotFoundException;
import net.byunchov.bblog.posts.models.PostDao;
import net.byunchov.bblog.posts.services.PostService;

@Controller
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping("")
    public ResponseEntity<Page<PostDao>> getAllPosts(@RequestParam(required = false) String title,
    @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        if (title != null) {
            return ResponseEntity.ok(postService.findByTitleContaining(title, pageRequest));
        }
        if (username != null) {
            return ResponseEntity.ok(postService.findByAuthorUsername(username, pageRequest));
        }
        return ResponseEntity.ok(postService.findAllPosts(pageRequest));
    }

    @SneakyThrows
    @GetMapping("/{id}")
    public ResponseEntity<PostDao> getUserById(@PathVariable Long id) {
        PostDao user = postService.findPostById(id);
        return ResponseEntity.ok(user);
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<PostDao> createUser(@RequestBody PostDto post, Principal principal) {
        PostDao newPost = postService.createPost(post, principal.getName());
        return new ResponseEntity<PostDao>(newPost, HttpStatus.CREATED);
    }

    @SneakyThrows
    @PatchMapping(value = "/{id}/update")
    public ResponseEntity<PostDao> updateUser(@PathVariable Long id, @RequestBody PostDto body, Principal principal) {
        PostDao updatedPost = postService.updatePost(id, body, principal.getName());
        return ResponseEntity.ok(updatedPost);
    }

    @SneakyThrows
    @DeleteMapping(value = "/{id}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        postService.deletePostById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(value = PostNotFoundException.class)
    private ResponseEntity<String> handlePostNotFoundException(PostNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    private ResponseEntity<String> handlePostNotFoundException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }
}
