package com.learning.spring.controllers;

import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.spring.social.dto.PostDTO;
import com.learning.spring.social.service.PostService;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin
@RequestMapping("/api/forum")
public class ForumRestController {
    @Autowired
    private PostService postService;

    @GetMapping("/posts") 
    @Transactional
    ResponseEntity<List<PostDTO>> getPosts(){
        List<PostDTO> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    @Transactional
    ResponseEntity<PostDTO> getPostById(@PathVariable("id") int id){
        Optional<PostDTO> post = postService.findById(id);
        if(post.isPresent()){
            return ResponseEntity.ok(post.get());
        } return ResponseEntity.notFound().build();
    }
}
