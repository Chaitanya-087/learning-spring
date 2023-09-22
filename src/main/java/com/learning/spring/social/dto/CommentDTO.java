package com.learning.spring.social.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CommentDTO {
    private Integer id;
    private UserDTO user;
    private String content;
    private List<ReplyDTO> replies = new ArrayList<>();
}
