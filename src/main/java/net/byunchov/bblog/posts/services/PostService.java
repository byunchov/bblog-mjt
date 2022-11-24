package net.byunchov.bblog.posts.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.byunchov.bblog.posts.converter.PostConverter;
import net.byunchov.bblog.posts.dto.PostDto;
import net.byunchov.bblog.posts.exceptions.PostNotFoundException;
import net.byunchov.bblog.posts.models.PostDao;
import net.byunchov.bblog.posts.repositories.PostRepository;
import net.byunchov.bblog.users.models.UserDao;
import net.byunchov.bblog.users.repositories.UserRepository;
import net.byunchov.bblog.utils.DataUtils;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostConverter postConverter;

    private static final String NOT_FOUND_MSG = "Post %d not found";

    public PostDao createPost(PostDao post) {
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public PostDao createPost(PostDto post, String username) {
        PostDao newPost = postConverter.convertDtoToEntity(post);
        UserDao user = userRepository.findByUsername(username).get();
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setUpdatedAt(LocalDateTime.now());
        newPost.setAuthor(user);
        return postRepository.save(newPost);
    }

    public PostDao updatePost(Long id, PostDao post) {
        PostDao existingPost = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(String.format(NOT_FOUND_MSG, id)));
        DataUtils.copyNonNullProperties(post, existingPost);
        existingPost.setUpdatedAt(LocalDateTime.now());

        PostDao createdUser = postRepository.save(existingPost);
        return createdUser;
    }

    public void deletePost(PostDao post) {
        try {
            postRepository.delete(post);
        } catch (EmptyResultDataAccessException e) {
            throw new PostNotFoundException("Post not found");
        }
    }

    public void deletePostById(Long id) {
        try {
            postRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new PostNotFoundException("Post not found");
        }
    }

    public PostDao findPostById(Long id) throws PostNotFoundException {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(String.format(NOT_FOUND_MSG, id)));
    }

    public Page<PostDao> findAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<PostDao> findByTitleContaining(String title, Pageable pageable) {
        return postRepository.findByTitleContaining(title, pageable);
    }
}
