package com.learning.spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/classroom")
public class ClassroomController {
    
    @GetMapping
    public String classroom() {
        return "classroom";
    }
}
