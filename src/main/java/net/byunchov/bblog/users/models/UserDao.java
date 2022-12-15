package net.byunchov.bblog.users.models;

import lombok.Data;
import net.byunchov.bblog.posts.models.PostDao;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "users")
public class UserDao implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(nullable = false, length = 25, unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String firstName;

    @Column
    private String lastName;
    
    @Column(nullable = false, unique = false)
    private String email;

    @OneToMany(mappedBy = "author")
    @JsonBackReference
    private List<PostDao> posts;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
            @JoinColumn(name = "authority_name", referencedColumnName = "name") })
    private Set<Authority> authorities;
}
