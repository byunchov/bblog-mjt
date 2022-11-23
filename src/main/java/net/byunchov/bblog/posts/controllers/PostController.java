package net.byunchov.bblog.posts.controllers;

import java.security.Principal;
import java.util.List;

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
import net.byunchov.bblog.posts.dto.PostDto;
import net.byunchov.bblog.posts.exceptions.PostNotFoundException;
import net.byunchov.bblog.posts.models.PostDao;
import net.byunchov.bblog.posts.services.PostService;

@Controller
@RequestMapping("/posts")
@Slf4j
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping("")
    public ResponseEntity<List<PostDao>> getAllPosts() {
        List<PostDao> user = postService.getAllPosts();
        return ResponseEntity.ok(user);
    }

    @SneakyThrows
    @GetMapping("/{id}")
    public ResponseEntity<PostDao> getUserById(@PathVariable Long id) {
        PostDao user = postService.findPostById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<PostDao> createUser(@RequestBody PostDto post, Principal principal) {
        log.info(principal.toString());
        log.info(post.toString());
        PostDao newPost = postService.createPost(post, principal.getName());
        return new ResponseEntity<PostDao>(newPost, HttpStatus.CREATED);
    }

    @PatchMapping(value="/{id}/update")
    public ResponseEntity<PostDao> updateUser(@PathVariable Long id, @RequestBody PostDao user) {
        PostDao updatedPost = postService.updatePost(id, user);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping(value="/{id}/delete")
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
}