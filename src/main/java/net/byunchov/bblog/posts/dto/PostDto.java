package net.byunchov.bblog.posts.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class PostDto implements Serializable {
    private String title;
    private String content;
}
