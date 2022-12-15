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
import net.byunchov.bblog.utils.UserRoleUtil;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostConverter postConverter;

    public PostDto createPost(PostDao post) {
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        post.setUpdatedAt(LocalDateTime.now());
        
        PostDao createdPost = postRepository.save(post);
        return postConverter.convertEntityToDto(createdPost);
    }

    public PostDto createPost(PostDto post, String username) {
        PostDao newPost = postConverter.convertDtoToEntity(post);
        UserDao user = userRepository.findByUsername(username).get();
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setUpdatedAt(LocalDateTime.now());
        newPost.setAuthor(user);
        PostDao createdPost = postRepository.save(newPost);
        return postConverter.convertEntityToDto(createdPost);
    }

    public PostDto updatePost(Long id, PostDto post, String username)
            throws PostNotFoundException, AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        PostDao existingPost = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(String.format(MessageUtils.POST_NOT_FOUND_MSG, id)));

        String authorUsername = existingPost.getAuthor().getUsername();

        Boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(UserRoleUtil.ROLE_ADMIN));

        if (!authorUsername.equals(username) && !isAdmin) {
            throw new AccessDeniedException(MessageUtils.ACC_DENIED_MSG);
        }

        DataUtils.copyNonNullProperties(post, existingPost);
        existingPost.setUpdatedAt(LocalDateTime.now());

        PostDao createdPost = postRepository.save(existingPost);
        return postConverter.convertEntityToDto(createdPost);
    }

    public void deletePost(PostDao post) {
        try {
            postRepository.delete(post);
        } catch (EmptyResultDataAccessException e) {
            throw new PostNotFoundException(String.format(MessageUtils.POST_NOT_FOUND_MSG, post.getTitle()));
        }
    }

    public void deletePostById(Long id) {
        try {
            postRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new PostNotFoundException(String.format(MessageUtils.POST_NOT_FOUND_MSG, id));
        }
    }

    public PostDto findPostById(Long id) throws PostNotFoundException {
        PostDao post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(String.format(MessageUtils.POST_NOT_FOUND_MSG, id)));

        return postConverter.convertEntityToDto(post);
    }

    public Page<PostDto> findAllPosts(Pageable pageable) {
       return postRepository.findAll(pageable).map(postConverter::convertEntityToDto);
    }

    public Page<PostDto> findByTitleContaining(String title, Pageable pageable) {
        return postRepository.findByTitleContaining(title, pageable).map(postConverter::convertEntityToDto);
    }

    public Page<PostDto> findByAuthorUsername(String username, Pageable pageable) {
        return postRepository.findByAuthorUsername(username, pageable).map(postConverter::convertEntityToDto);
    }
}
