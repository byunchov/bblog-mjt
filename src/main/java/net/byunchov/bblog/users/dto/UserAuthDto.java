package net.byunchov.bblog.users.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserAuthDto implements Serializable {
    private String username;
    private String password;
}
