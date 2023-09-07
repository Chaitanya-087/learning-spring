package com.learning.spring.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.learning.spring.social.bindings.AddCommentForm;
import com.learning.spring.social.bindings.AddPostForm;
import com.learning.spring.social.bindings.HybridComment;
import com.learning.spring.social.bindings.RegistrationForm;
import com.learning.spring.social.entities.Comment;
import com.learning.spring.social.entities.Like;
import com.learning.spring.social.entities.LikeId;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.User;
import com.learning.spring.social.exceptions.ResourceNotFoundException;
import com.learning.spring.social.repositories.CommentRepository;
import com.learning.spring.social.repositories.LikeCRUDRepository;
import com.learning.spring.social.repositories.PostRepository;
import com.learning.spring.social.repositories.UserRepository;
import com.learning.spring.social.service.CommentService;
import com.learning.spring.social.service.DomainUserService;

import jakarta.servlet.ServletException;

@Controller
@RequestMapping("/forum")
public class ForumController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeCRUDRepository likeCRUDRepository;

    @GetMapping("/post/form")
    public String getPostForm(Model model,  @AuthenticationPrincipal UserDetails userDetails) {
        AddPostForm postForm = new AddPostForm();
        User author = domainUserService.getByName(userDetails.getUsername()).get();
        postForm.setUserId(author.getId());
        model.addAttribute("postForm", new AddPostForm());
        return "forum/postForm";
    }

    @PostMapping("/post/add")
    public String addNewPost(@ModelAttribute("postForm") AddPostForm postForm, BindingResult bindingResult,
            RedirectAttributes attr,@AuthenticationPrincipal UserDetails userDetails) throws ServletException {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getFieldErrors());
            attr.addFlashAttribute("org.springframework.validation.BindingResult.post", bindingResult);
            attr.addFlashAttribute("post", postForm);
            return "redirect:/forum/post/form";
        }

        User user = domainUserService.getByName(userDetails.getUsername()).get();
        // User user = loggedInUser.getLoggedInUser();
        Post post = new Post();
        post.setAuthor(user);
        post.setContent(postForm.getContent());
        post.setTitle(postForm.getTitle());
        postRepository.save(post);

        return String.format("redirect:/forum/post/%d", post.getId());
    }

    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable int id, Model model, @AuthenticationPrincipal UserDetails userDetails) throws ResourceNotFoundException {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new ResourceNotFoundException("No post with the requested ID");
        }
        List<HybridComment> commentList = commentService.findAllByPostId(id);
        model.addAttribute("commentList", commentList);
        model.addAttribute("post", post.get());
        int numLikes = likeCRUDRepository.countByPostId(id);
        model.addAttribute("likeCount", numLikes);
        model.addAttribute("commentForm", new AddCommentForm());
        return "forum/posts";
    }

    @PostMapping("/post/{id}/like")
    public String postLike(@PathVariable int id,@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes attr) {
        LikeId likeId = new LikeId();
        likeId.setUser(domainUserService.getByName(userDetails.getUsername()).get());
        likeId.setPost(postRepository.findById(id).get());
        Like like = new Like();
        like.setLikeId(likeId);
        likeCRUDRepository.save(like);
        return String.format("redirect:/forum/post/%d", id);
    }

    @PostMapping("/post/{id}/comment")
    public String commentOnPost(@ModelAttribute("commentForm") AddCommentForm commentForm, @PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Post> post = postRepository.findById(id);
        Comment comment = new Comment();
        comment.setContent(commentForm.getContent());
        comment.setPost(post.get());
        comment.setUser(domainUserService.getByName(userDetails.getUsername()).get());
        commentRepository.save(comment);
        return String.format("redirect:/forum/post/%d", id);
    }

    @PostMapping("/post/{id}/reply/{parentId}")
    public String replyToComment(@RequestParam("content") String content, @PathVariable int id, @PathVariable int parentId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Post> post = postRepository.findById(id);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post.get());
        comment.setUser(domainUserService.getByName(userDetails.getUsername()).get());
        comment.setParent(commentRepository.findById(parentId).get());
        commentRepository.save(comment);
        return String.format("redirect:/forum/post/%d", id);
    }
    @GetMapping("/register")
    public String getRegistrationForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "forum/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registrationForm") RegistrationForm registrationForm,
            BindingResult bindingResult,
            RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            attr.addFlashAttribute("org.springframework.validation.BindingResult.registrationForm", bindingResult);
            attr.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        }
        if (!registrationForm.isValid()) {
            attr.addFlashAttribute("message", "Passwords must match");
            attr.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        }
        System.out.println(domainUserService.save(registrationForm.getUsername(), registrationForm.getPassword()));
        attr.addFlashAttribute("result", "Registration success!");
        return "redirect:/login";
    }

}