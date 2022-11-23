package net.byunchov.bblog.users.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;

@Entity
@Data
@Table(name = "users")
public class UserDao implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String firstName;

    @Column
    private String lastName;
    
    @Column
    private String email;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_authority", joinColumns = {
            @JoinColumn(name = "account_id", referencedColumnName = "id") }, inverseJoinColumns = {
            @JoinColumn(name = "authority_name", referencedColumnName = "name") })
    private Set<Authority> authorities;
    // @Builder.Default
    // private Set<Authority> authorities = new HashSet<>();
}

