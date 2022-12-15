package net.byunchov.bblog.posts.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import net.byunchov.bblog.users.dto.UserDto;

@Data
public class PostDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    private String title;
    private String content;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto author;

}
