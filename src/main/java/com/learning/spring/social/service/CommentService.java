package com.learning.spring.social.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.learning.spring.social.bindings.HybridComment;
import com.learning.spring.social.entities.Comment;
import com.learning.spring.social.repositories.CommentRepository;


@Component
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<HybridComment> findAllByPostId(Integer postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        Map<Integer, HybridComment> commentMap = new HashMap<>();

        for (Comment comment : comments) {
            HybridComment hComment = new HybridComment();
            if (comment.getParent() == null) {
                hComment = createHybridComment(comment);
                // topLevelComments.add(hComment);
                commentMap.put(comment.getId(), hComment);
            }
        }

        for (Comment comment : comments) {
            if (comment.getParent() != null) {
                Comment parentComment = findCommonAncestor(comment);
                commentMap.get(parentComment.getId()).getReplies().add(comment);
            }
        }
        return commentMap.values().stream().toList();
    }

    
    private HybridComment createHybridComment(Comment comment) {
        HybridComment hComment = new HybridComment();
        hComment.setId(comment.getId());
        hComment.setContent(comment.getContent());
        hComment.setUser(comment.getUser());
        hComment.setPost(comment.getPost());
        return hComment;
    }
    private Comment findCommonAncestor(Comment comment) {
        if (comment.getParent() == null) {
            return comment;
        }
        return findCommonAncestor(comment.getParent());
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

}
