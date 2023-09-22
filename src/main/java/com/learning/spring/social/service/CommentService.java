package com.learning.spring.social.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.learning.spring.social.dto.CommentDTO;
import com.learning.spring.social.dto.ReplyDTO;
import com.learning.spring.social.dto.UserDTO;
import com.learning.spring.social.entities.Comment;
// import com.learning.spring.social.entities.User;
import com.learning.spring.social.repositories.CommentRepository;

@Component
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<CommentDTO> findAllByPostId(Integer postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        Map<Integer, CommentDTO> commentMap = new HashMap<>();

        for (Comment comment : comments) {
            if (comment.getParent() == null) {
                commentMap.putIfAbsent(comment.getId(), createCommentDTO(comment));
            } else {
                Comment ancestor = findCommonAncestor(comment);
                ReplyDTO replyDTO = creaReplyDTO(comment);
                commentMap.putIfAbsent(ancestor.getId(), createCommentDTO(ancestor));
                commentMap.get(ancestor.getId()).getReplies().add(replyDTO);
            }
        }
        return commentMap.values().stream().toList();
    }

    private CommentDTO createCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
         UserDTO userDTO = new UserDTO();
        userDTO.setId(comment.getUser().getId());
        userDTO.setName(comment.getUser().getName());
        userDTO.setSymbol(comment.getUser().getName().substring(0, 1));
        commentDTO.setUser(userDTO);
        return commentDTO;
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

    public Optional<Comment> findById(Integer id) {
        return commentRepository.findById(id);
    }

    private ReplyDTO creaReplyDTO(Comment comment) {
        ReplyDTO replyDTO = new ReplyDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(comment.getUser().getId());
        userDTO.setName(comment.getUser().getName());
        userDTO.setSymbol(comment.getUser().getName().substring(0, 1));
        replyDTO.setId(comment.getId());
        replyDTO.setContent(comment.getContent());
        replyDTO.setUser(userDTO);
        return replyDTO;
    }
}
