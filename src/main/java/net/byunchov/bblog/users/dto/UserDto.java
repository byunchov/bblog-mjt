package net.byunchov.bblog.users.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserDto implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
