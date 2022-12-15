package net.byunchov.bblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.byunchov.bblog.posts.dto.PostDto;
import net.byunchov.bblog.posts.models.PostDao;
import net.byunchov.bblog.posts.services.PostService;
import net.byunchov.bblog.users.models.Authority;
import net.byunchov.bblog.users.models.UserDao;
import net.byunchov.bblog.users.repositories.AuthorityRepository;
import net.byunchov.bblog.users.repositories.UserRepository;
import net.byunchov.bblog.users.services.UserService;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        List<Authority> authorities = authorityRepository.findAll();

        if (authorities.isEmpty()) {
            List<String> userRoles = Arrays.asList("ROLE_ADMIN", "ROLE_USER");

            userRoles.forEach(role -> {
                Authority authority = new Authority(role);
                authorityRepository.save(authority);
            });
        }

        List<UserDao> users = userRepository.findAll();

        if (users.isEmpty()) {
            File userListFile = new File("users.json");
            File userAuthFile = new File("users_auth.json");

            try {
                List<Map<String, String>> userData = mapper.readValue(
                        userListFile, new TypeReference<List<Map<String, String>>>() {
                        });

                Map<String, String> userAuths = mapper.readValue(
                        userAuthFile, new TypeReference<Map<String, String>>() {
                        });

                userData.forEach(user -> {
                    UserDao userDao = new UserDao();

                    userDao.setId(Long.valueOf(user.get("id")));
                    userDao.setFirstName(user.get("first_name"));
                    userDao.setLastName(user.get("last_name"));
                    userDao.setUsername(user.get("username"));
                    userDao.setEmail(user.get("email"));
                    userDao.setPassword(user.get("password"));

                    Set<Authority> authority = new HashSet<>();
                    authorityRepository.findById(userAuths.get(user.get("id")))
                            .ifPresent(authority::add);
                    userDao.setAuthorities(authority);

                    UserDao savedUser = userService.saveUser(userDao);
                    users.add(savedUser);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<PostDto> posts = postService.findAllPosts();

        if (posts.isEmpty()) {

            File postsFile = new File("posts_content.json");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            try {
                List<Map<String, String>> postData = mapper.readValue(
                        postsFile, new TypeReference<List<Map<String, String>>>() {
                        });

                postData.forEach(post -> {
                    PostDao postDao = new PostDao();
                    Long uid = Long.valueOf(post.get("user_id"));
                    UserDao author = users.stream().filter(usr -> usr.getId() == uid).findFirst().get();
                    LocalDateTime cretaedAt = LocalDateTime.parse(post.get("created_at"), formatter);;
                    LocalDateTime updatedAt = LocalDateTime.parse(post.get("updated_at"), formatter);

                    postDao.setId(Long.valueOf(post.get("id")));
                    postDao.setTitle(post.get("title"));
                    postDao.setContent(post.get("content"));
                    postDao.setAuthor(author);
                    postDao.setCreatedAt(cretaedAt);
                    postDao.setUpdatedAt(updatedAt);

                    postService.createPost(postDao);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}