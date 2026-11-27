package com.cdran.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
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

import com.cdran.dto.ContentFlagForm;
import com.cdran.dto.ForumAnswerForm;
import com.cdran.dto.ForumPostForm;
import com.cdran.model.entity.ForumPost;
import com.cdran.model.enums.FlagTarget;
import com.cdran.repository.CropRepository;
import com.cdran.service.ForumService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/forum")
@PreAuthorize("isAuthenticated()")
public class ForumController {

    private final ForumService forumService;
    private final CropRepository cropRepository;

    public ForumController(ForumService forumService, CropRepository cropRepository) {
        this.forumService = forumService;
        this.cropRepository = cropRepository;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) Long cropId,
            @RequestParam(required = false) String q,
            Model model) {
        model.addAttribute("posts", forumService.searchPosts(cropId, q));
        model.addAttribute("crops", cropRepository.findAll());
        model.addAttribute("selectedCropId", cropId);
        model.addAttribute("query", q != null ? q : "");
        return "forum/list";
    }

    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        model.addAttribute("postForm", new ForumPostForm());
        model.addAttribute("crops", cropRepository.findAll());
        return "forum/new";
    }

    @PostMapping("/posts")
    public String createPost(
            @Valid @ModelAttribute("postForm") ForumPostForm form,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("crops", cropRepository.findAll());
            return "forum/new";
        }
        ForumPost created = forumService.createPost(form, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "forum.post.created");
        return "redirect:/forum/posts/" + created.getId();
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        ForumPost post = forumService.getVisiblePost(id);
        ContentFlagForm flagForm = new ContentFlagForm();
        flagForm.setTargetType(FlagTarget.FORUM_POST);
        flagForm.setTargetId(id);

        model.addAttribute("post", post);
        model.addAttribute("answerForm", new ForumAnswerForm());
        model.addAttribute("flagForm", flagForm);
        return "forum/detail";
    }

    @PostMapping("/posts/{id}/answers")
    public String addAnswer(
            @PathVariable Long id,
            @Valid @ModelAttribute("answerForm") ForumAnswerForm form,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            ContentFlagForm flagForm = new ContentFlagForm();
            flagForm.setTargetType(FlagTarget.FORUM_POST);
            flagForm.setTargetId(id);
            model.addAttribute("post", forumService.getVisiblePost(id));
            model.addAttribute("flagForm", flagForm);
            return "forum/detail";
        }
        forumService.addAnswer(id, form, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "forum.answer.created");
        return "redirect:/forum/posts/" + id;
    }

    @PostMapping("/answers/{answerId}/verify")
    public String verifyAnswer(
            @PathVariable Long answerId,
            @RequestParam Long postId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        forumService.verifyAnswer(answerId, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "forum.answer.verified");
        return "redirect:/forum/posts/" + postId;
    }

    @PostMapping("/posts/{id}/hide")
    public String hidePost(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        forumService.hidePost(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "forum.post.hidden");
        return "redirect:/forum";
    }

    @PostMapping("/flag")
    public String flagContent(
            @Valid @ModelAttribute("flagForm") ContentFlagForm form,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Long postId = (form.getTargetType() == FlagTarget.FORUM_POST) ? form.getTargetId() : null;
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "forum.validation.flagReason.required");
            return postId != null ? "redirect:/forum/posts/" + postId : "redirect:/forum";
        }
        forumService.flagContent(form, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "forum.flag.submitted");
        return postId != null ? "redirect:/forum/posts/" + postId : "redirect:/forum";
    }
}