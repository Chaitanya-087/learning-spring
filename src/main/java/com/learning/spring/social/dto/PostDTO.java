package com.learning.spring.social.dto;

import java.util.Date;

import com.learning.spring.social.entities.User;

import lombok.Data;

@Data
public class PostDTO {
    private int id;
    private String title;
    private String content;
    private User author;
    private Date createdAt;
    private int likes;
    private int comments;

    //TODO: tags list to be added
}
