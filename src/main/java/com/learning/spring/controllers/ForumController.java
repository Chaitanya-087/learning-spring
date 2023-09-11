package com.learning.spring.controllers;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import com.learning.spring.social.bindings.RegistrationForm;
import com.learning.spring.social.dto.CommentDTO;
import com.learning.spring.social.dto.PostDTO;
import com.learning.spring.social.entities.Comment;
import com.learning.spring.social.entities.Like;
import com.learning.spring.social.entities.LikeId;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.Tag;
import com.learning.spring.social.entities.User;
import com.learning.spring.social.exceptions.ResourceNotFoundException;
import com.learning.spring.social.repositories.LikeCRUDRepository;
import com.learning.spring.social.repositories.PostRepository;
import com.learning.spring.social.repositories.TagRepository;
import com.learning.spring.social.service.CommentService;
import com.learning.spring.social.service.DomainUserService;
import com.learning.spring.social.service.PostService;

import jakarta.servlet.ServletException;

@Controller
@RequestMapping("/forum")
public class ForumController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeCRUDRepository likeCRUDRepository;

    @GetMapping
    public String home(Principal principal, Model model) {
        model.addAttribute("isLoggedIn", principal != null);
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        model.addAttribute("posts", postService.findAll());
        return "forum/home";
    }

    @GetMapping("/post/form")
    public String getPostForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("postForm", new AddPostForm());
        return "forum/postForm";
    }

    @GetMapping("/search")
    public String searchPost(@RequestParam("search") String search, Model model) {
        if (search == null || search.isEmpty()) {
            return "redirect:/forum";
        } else {
            model.addAttribute("posts", postRepository.findPostsByTagName(search.toLowerCase()));
        }
        return "forum/home";
    }

    @PostMapping("/post/add")
    @Transactional
    public String addNewPost(@ModelAttribute("postForm") AddPostForm postForm, BindingResult bindingResult,
            RedirectAttributes attr, @AuthenticationPrincipal UserDetails userDetails) throws ServletException {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getFieldErrors());
            attr.addFlashAttribute("org.springframework.validation.BindingResult.post", bindingResult);
            attr.addFlashAttribute("post", postForm);
            return "redirect:/forum/post/form";
        }
        Set<Tag> postTags = new HashSet<>();
        String[] tags = postForm.getTags().split(",");
        for (int i = 0; i < tags.length; i++) {
            Tag existingTag = tagRepository.findByName(tags[i]);
            if (existingTag == null) {
                Tag newTag = new Tag();
                newTag.setName(tags[i]);
                tagRepository.save(newTag);
                postTags.add(newTag);
            } else {
                postTags.add(existingTag);
            }
        }
        User user = domainUserService.getByName(userDetails.getUsername()).get();
        Post post = new Post();
        post.setAuthor(user);
        post.setContent(postForm.getContent());
        post.setTitle(postForm.getTitle());
        post.setTags(postTags);
        postRepository.save(post);

        return String.format("redirect:/forum/post/%d", post.getId());
    }

    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable int id, Model model, @AuthenticationPrincipal UserDetails userDetails)
            throws ResourceNotFoundException {
        PostDTO postDTO = postService.findById(id);

        // List<CommentDTO> commentList = commentService.findAllByPostId(id);
        // model.addAttribute("commentList", commentList);
        model.addAttribute("post", postDTO);
        // int numLikes = likeCRUDRepository.countByPostId(id);
        // model.addAttribute("likeCount", numLikes);
        // model.addAttribute("commentForm", new AddCommentForm());
        return "forum/posts";
    }

    @PostMapping("/post/{id}/like")
    public String postLike(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes attr) {
        LikeId likeId = new LikeId();
        likeId.setUser(domainUserService.getByName(userDetails.getUsername()).get());
        likeId.setPost(postRepository.findById(id).get());
        Like like = new Like();
        like.setLikeId(likeId);
        likeCRUDRepository.save(like);
        return String.format("redirect:/forum/post/%d", id);
    }

    @PostMapping("/post/{id}/comment")
    public String commentOnPost(@ModelAttribute("commentForm") AddCommentForm commentForm, @PathVariable int id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Post> post = postRepository.findById(id);
        Comment comment = new Comment();
        comment.setContent(commentForm.getContent());
        comment.setPost(post.get());
        comment.setUser(domainUserService.getByName(userDetails.getUsername()).get());
        commentService.save(comment);
        return String.format("redirect:/forum/post/%d", id);
    }

    @PostMapping("/post/{id}/reply/{parentId}")
    public String replyToComment(@RequestParam("content") String content, @PathVariable int id,
            @PathVariable int parentId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Post> post = postRepository.findById(id);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post.get());
        comment.setUser(domainUserService.getByName(userDetails.getUsername()).get());
        comment.setParent(commentService.findById(parentId).get());
        commentService.save(comment);
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
        domainUserService.save(registrationForm.getUsername(), registrationForm.getPassword());
        attr.addFlashAttribute("result", "Registration success!");
        return "redirect:/login";
    }

}