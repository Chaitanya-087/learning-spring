package com.learning.spring.social.business;

import org.springframework.stereotype.Component;

import com.learning.spring.social.entities.User;

import lombok.Data;

@Data
public class LoggedInUser {
    private User loggedInUser;
}