package net.byunchov.bblog.users.dto;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import net.byunchov.bblog.users.models.Authority;

@Data
public class UserDto implements Serializable {
    private String firstName;
    private String lastName;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Authority> authorities;   
}
