package com.learning.spring.social.dto;

import lombok.Data;

@Data
public class ReplyDTO {
    int id;
    String content;
    UserDTO user;
}
