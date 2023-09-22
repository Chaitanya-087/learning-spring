package com.learning.spring.social.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.learning.spring.social.entities.Tag;

import lombok.Data;

@Data
public class PostDTO {
    private int id;
    private String title;
    private String content;
    private UserDTO author;
    private Date createdAt;
    private int likesCount;
    private int commentsCount;
    private Set<TagDTO> tags = new HashSet<>();
    private List<CommentDTO> comments = new ArrayList<>();
}
