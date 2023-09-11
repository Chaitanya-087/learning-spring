package com.learning.spring.social.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.learning.spring.social.dto.PostDTO;
import com.learning.spring.social.dto.UserDTO;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.Tag;
import com.learning.spring.social.repositories.CommentRepository;
import com.learning.spring.social.repositories.LikeCRUDRepository;
import com.learning.spring.social.repositories.PostRepository;
import com.learning.spring.social.repositories.TagRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeCRUDRepository likeCRUDRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagRepository tagRepository;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public List<PostDTO> findAll() {
        List<PostDTO> postDTOs = new ArrayList<>();
        List<Post> posts = (List<Post>) postRepository.findAll();

        for (Post post : posts) {
            PostDTO postDTO = new PostDTO();
            UserDTO userDTO = new UserDTO();
            userDTO.setId(post.getAuthor().getId());
            userDTO.setName(post.getAuthor().getName());
            userDTO.setSymbol(post.getAuthor().getName().substring(0, 1));
            int likesCount = likeCRUDRepository.countByPostId(post.getId());
            int commentsCount = commentRepository.countByPostId(post.getId());
            Set<Tag> tags = tagRepository.findByPost(post);
            postDTO.setId(post.getId());
            postDTO.setTitle(post.getTitle());
            postDTO.setContent(post.getContent());
            postDTO.setAuthor(userDTO);
            postDTO.setCreatedAt(post.getCreatedAt());
            postDTO.setLikesCount(likesCount);
            postDTO.setCommentsCount(commentsCount);
            postDTO.setTags(tags);
            postDTOs.add(postDTO);
        }

        return postDTOs;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public PostDTO findById(int id) {
        Post post = postRepository.findById(id).get();
        PostDTO postDTO = new PostDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(post.getAuthor().getId());
        userDTO.setName(post.getAuthor().getName());
        userDTO.setSymbol(post.getAuthor().getName().substring(0, 1));
        int likesCount = likeCRUDRepository.countByPostId(post.getId());
        int commentsCount = commentRepository.countByPostId(post.getId());
        Set<Tag> tags = tagRepository.findByPost(post);
        postDTO.setId(post.getId());
        postDTO.setAuthor(userDTO);
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setLikesCount(likesCount);
        postDTO.setCommentsCount(commentsCount);
        postDTO.setTags(tags);
        return postDTO;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public List<PostDTO> findByPattern(String pattern) {
        List<PostDTO> postDTOs = new ArrayList<>();
        // List<Post> posts = postRepository.findByPattern(pattern);

        // for (Post post : posts) {
        //     PostDTO postDTO = new PostDTO();
        //     UserDTO userDTO = new UserDTO();
        //     userDTO.setId(post.getAuthor().getId());
        //     userDTO.setName(post.getAuthor().getName());
        //     userDTO.setSymbol(post.getAuthor().getName().substring(0, 1));
        //     int likesCount = likeCRUDRepository.countByPostId(post.getId());
        //     int commentsCount = commentRepository.countByPostId(post.getId());
        //     Set<Tag> tags = tagRepository.findByPost(post);
        //     postDTO.setId(post.getId());
        //     postDTO.setTitle(post.getTitle());
        //     postDTO.setContent(post.getContent());
        //     postDTO.setAuthor(userDTO);
        //     postDTO.setCreatedAt(post.getCreatedAt());
        //     postDTO.setLikesCount(likesCount);
        //     postDTO.setCommentsCount(commentsCount);
        //     postDTO.setTags(tags);
        //     postDTOs.add(postDTO);
        // }

        return postDTOs;
    }
}