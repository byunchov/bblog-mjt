package net.byunchov.bblog.posts.services;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import net.byunchov.bblog.posts.converter.PostConverter;
import net.byunchov.bblog.posts.dto.PostDto;
import net.byunchov.bblog.posts.exceptions.PostNotFoundException;
import net.byunchov.bblog.posts.models.PostDao;
import net.byunchov.bblog.posts.repositories.PostRepository;
import net.byunchov.bblog.users.models.UserDao;
import net.byunchov.bblog.users.repositories.UserRepository;
import net.byunchov.bblog.utils.DataUtils;
import net.byunchov.bblog.utils.MessageUtils;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostConverter postConverter;

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

    public PostDao updatePost(Long id, PostDto post, String username)
            throws PostNotFoundException, AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        PostDao existingPost = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(String.format(MessageUtils.POST_NOT_FOUND_MSG, id)));

        String authorUsername = existingPost.getAuthor().getUsername();

        Boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!authorUsername.equals(username) && !isAdmin) {
            throw new AccessDeniedException(MessageUtils.ACC_DENIED_MSG);
        }

        DataUtils.copyNonNullProperties(post, existingPost);
        existingPost.setUpdatedAt(LocalDateTime.now());

        PostDao createdUser = postRepository.save(existingPost);
        return createdUser;
    }

    public void deletePost(PostDao post) {
        try {
            postRepository.delete(post);
        } catch (EmptyResultDataAccessException e) {
            throw new PostNotFoundException(String.format(MessageUtils.POST_NOT_FOUND_MSG, ""));
        }
    }

    public void deletePostById(Long id) {
        try {
            postRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new PostNotFoundException(String.format(MessageUtils.POST_NOT_FOUND_MSG, id));
        }
    }

    public PostDao findPostById(Long id) throws PostNotFoundException {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(String.format(MessageUtils.POST_NOT_FOUND_MSG, id)));
    }

    public Page<PostDao> findAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<PostDao> findByTitleContaining(String title, Pageable pageable) {
        return postRepository.findByTitleContaining(title, pageable);
    }

    public Page<PostDao> findByAuthorUsername(String username, Pageable pageable) {
        return postRepository.findByAuthorUsername(username, pageable);
    }
}
